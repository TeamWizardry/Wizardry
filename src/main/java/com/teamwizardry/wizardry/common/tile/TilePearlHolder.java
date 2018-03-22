package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.TileManaInteracter;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.common.item.ItemGlassOrb;
import com.teamwizardry.wizardry.common.item.ItemManaOrb;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TilePearlHolder extends TileManaInteracter implements ICooldown {

	@Module
	public ModuleInventory inventory = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			if (stack.getItem() instanceof ItemManaOrb
					|| stack.getItem() instanceof ItemGlassOrb
					|| stack.getItem() instanceof ItemNacrePearl)
				return super.getStackLimit(slot, stack);
			else return 0;
		}
	});

	@Nullable
	@Override
	public IWizardryCapability getCap() {
		if (containsManaOrb()) {
			return WizardryCapabilityProvider.getCap(getItemStack());
		}
		return null;
	}


	/**
	 * isPartOfStructure defines if this holder is part of a structure
	 * and cannot be used for mana networking outside of its own structure
	 */
	@Save
	public boolean isPartOfStructure = false;

	@Save
	@Nullable
	public BlockPos structurePos = null;

	public TilePearlHolder() {
		super(300, 300);
	}

	@Override
	public void update() {
		super.update();
		if (containsManaOrb()) {

			// This holder is not benign, therefore it can suck from the nearest holder
			if (!isPartOfStructure)
				for (BlockPos pearlHolders : getNearestSuckables(TilePearlHolder.class, getWorld(), getPos(), false)) {
					TileEntity tile = getWorld().getTileEntity(pearlHolders);
					if (tile != null && tile instanceof TilePearlHolder && !((TilePearlHolder) tile).isPartOfStructure) {
						if (structurePos == null && ((TilePearlHolder) tile).structurePos == null)
							suckManaFrom(getWorld(), getPos(), getCap(), pearlHolders, 1, true);
					}
				}

			// benign or not, suck from the nearest battery
			for (BlockPos target : getNearestSuckables(TileManaBattery.class, getWorld(), getPos(), false)) {
				if (target.equals(structurePos)) continue;
				suckManaFrom(getWorld(), getPos(), getCap(), target, 1, false);
			}

			if (isPartOfStructure && new CapManager(getCap()).isManaEmpty()) {
				setItemStack(new ItemStack(ModItems.GLASS_ORB));
				markDirty();

				//world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
				//PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 0.5, 0.5, 50, 50, 10, true),
				//		new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
			}


		} else if (containsNacrePearl()) {
			if (world.isRemote) return;

			updateCooldown(getItemStack());

			IWizardryCapability pearlCap = WizardryCapabilityProvider.getCap(getItemStack());
			if (pearlCap == null || pearlCap.getMana() > pearlCap.getMaxMana() || isPartOfStructure || structurePos != null)
				return;

			boolean suckedFromHolder = false;
			for (BlockPos pearlHolders : getNearestSuckables(TilePearlHolder.class, getWorld(), getPos(), false)) {
				TileEntity tile = getWorld().getTileEntity(pearlHolders);
				if (tile != null && tile instanceof TilePearlHolder && !((TilePearlHolder) tile).isPartOfStructure && structurePos == null && ((TilePearlHolder) tile).structurePos == null) {
					suckedFromHolder = true;
					suckManaFrom(getWorld(), getPos(), pearlCap, pearlHolders, 1, false);
				}
			}

			if (!suckedFromHolder) {
				for (BlockPos target : getNearestSuckables(TileManaBattery.class, getWorld(), getPos(), false)) {
					suckManaFrom(getWorld(), getPos(), pearlCap, target, 10, false);
				}
			}

			if (world.isBlockPowered(getPos())) return;
			if (isCoolingDown(getItemStack())) return;
			if (pearlCap.getMana() == 0) return;

			BlockPos closestMagnet = null;
			for (int i = -10; i < 10; i++)
				for (int j = -10; j < 10; j++)
					for (int k = -10; k < 10; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
						if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;
						if (closestMagnet == null) closestMagnet = pos;
						else if (pos.distanceSq(getPos()) < getPos().distanceSq(closestMagnet))
							closestMagnet = pos;
					}
			if (closestMagnet == null) return;

			{
				Vec3d direction = new Vec3d(closestMagnet).subtract(new Vec3d(getPos())).normalize();
				SpellData spell = new SpellData(getWorld());
				spell.addData(LOOK, direction);
				spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 1.5, 0.5));
				spell.addData(CAPABILITY, pearlCap);
				SpellUtils.runSpell(getItemStack(), spell);
				setCooldown(world, null, null, getItemStack(), spell);
			}
		}
	}

	public boolean containsSomething() {
		return containsAnyOrb() || containsNacrePearl();
	}

	public boolean containsAnyOrb() {
		return containsGlassOrb() || containsManaOrb();
	}

	public boolean containsGlassOrb() {
		return getItemStack().getItem() instanceof ItemGlassOrb;
	}

	public boolean containsManaOrb() {
		return getItemStack().getItem() instanceof ItemManaOrb;
	}

	public boolean containsNacrePearl() {
		return getItemStack().getItem() instanceof ItemNacrePearl;
	}

	public ItemStack getItemStack() {
		return inventory.getHandler().getStackInSlot(0);
	}

	public void setItemStack(ItemStack stack) {
		inventory.getHandler().setStackInSlot(0, stack);
	}

	public ItemStack extractItemStack() {
		return inventory.getHandler().extractItem(0, 1, false);
	}
}
