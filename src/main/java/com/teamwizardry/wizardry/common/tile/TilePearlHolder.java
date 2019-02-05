package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.TileManaInteractor;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.item.IManaCell;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.ItemOrb;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque on 5/7/2016.
 */
@TileRegister(Wizardry.MODID + ":pedestal")
public class TilePearlHolder extends TileManaInteractor implements ICooldown {

	@Module
	public ModuleInventory inventory = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			if (stack.getItem() instanceof IManaCell || stack.getItem() instanceof ItemNacrePearl) {
				//Max number of orbs the pearl holder can hold
				return 1;
			} else {
				return 0;
			}
		}
	});

	@Nullable
	@Override
	public IWizardryCapability getWizardryCap() {
		if (containsAnyOrb()) {
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

	@Nonnull
	@Override
	public Vec3d getOffset() {
		return new Vec3d(0, 0.5, 0);
	}

	@Override
	public void update() {
		super.update();
		if (containsAnyOrb())
		{
			ModItems.ORB.onUpdate(getItemStack(), getWorld(), null, 0, true);
		}
		else if (containsNacrePearl()) {
			if (world.isRemote) return;

			IWizardryCapability pearlCap = WizardryCapabilityProvider.getCap(getItemStack());
			if (pearlCap == null || pearlCap.getMana() > pearlCap.getMaxMana() || isPartOfStructure || structurePos != null)
				return;

			if (world.isBlockPowered(getPos())) return;
			if (isCoolingDown(world, getItemStack())) return;
			if (pearlCap.getMana() == 0) return;

			BlockPos closestMagnet = getNearestTilePos(TileManaMagnet.class);
			if (closestMagnet == null) return;

			Vec3d direction = new Vec3d(closestMagnet).subtract(new Vec3d(getPos())).normalize();
			SpellData spell = new SpellData(getWorld());
			spell.addData(LOOK, direction);
			spell.addData(ORIGIN, new Vec3d(getPos()).add(0.5, 1.5, 0.5));
			spell.addData(CAPABILITY, pearlCap);
			SpellUtils.runSpell(getItemStack(), spell);
			setCooldown(world, null, null, getItemStack(), spell);
		}
	}

	public boolean containsSomething() {
		return containsAnyOrb() || containsNacrePearl();
	}

	public boolean containsAnyOrb() {
		return getItemStack().getItem() instanceof ItemOrb;
	}

	public boolean containsGlassOrb() {
		return getItemStack().getItem() instanceof ItemOrb && getItemStack().getItemDamage() == 0;
	}

	public boolean containsManaOrb() {
		return getItemStack().getItem() instanceof ItemOrb && getItemStack().getItemDamage() == 1;
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
}
