package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.TileManaInteracter;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TilePearlHolder extends TileManaInteracter implements ICooldown {

	@Save
	@NotNull
	public ItemStack pearl = ItemStack.EMPTY;

	@Save
	public boolean isBenign = false;

	@Save
	@Nullable
	public BlockPos structurePos = null;

	public TilePearlHolder() {
		super(10000, 10000);
	}

	@Nullable
	@Override
	public IWizardryCapability getCap() {
		if (!pearl.isEmpty() && pearl.getItem() == ModItems.MANA_ORB) {
			return WizardryCapabilityProvider.getCap(pearl);
		}
		return null;
	}

	@Override
	public void update() {
		super.update();

		if (!isBenign)
			for (BlockPos pearlHolders : getNearestSuckables(TilePearlHolder.class, getWorld(), getPos())) {
				TileEntity tile = getWorld().getTileEntity(pearlHolders);
				if (tile != null && tile instanceof TilePearlHolder && !((TilePearlHolder) tile).isBenign) {
					if (structurePos != null && ((TilePearlHolder) tile).structurePos != null && !structurePos.equals(((TilePearlHolder) tile).structurePos))
						suckManaFrom(getWorld(), getPos(), getCap(), pearlHolders, 100, true);
				}
			}

		primary:
		for (BlockPos target : getNearestSuckables(TileManaBattery.class, getWorld(), getPos())) {
			for (BlockPos relative : TileManaBattery.poses)
				if (getPos().subtract(relative).equals(target)) {
					break primary;
				}
			suckManaFrom(getWorld(), getPos(), getCap(), target, 1, false);
		}

		if (isBenign && new CapManager(getCap()).isManaEmpty()) {
			pearl = ItemStack.EMPTY;
			markDirty();

			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 0.5, 0.5, 50, 50, 10, true),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
		}

		if (world.isRemote) return;

		// TODO: support for this again
		if (pearl.getItem() == ModItems.PEARL_NACRE) {
			updateCooldown(pearl);

			if (isCoolingDown(pearl)) return;

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
				Vec3d direction = new Vec3d(closestMagnet)
						.addVector(0.5, 0, 0.5)
						.subtract(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5))
						.normalize();
				SpellData spell = new SpellData(getWorld());
				spell.addData(LOOK, direction);
				spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 1.5, 0.5));
				spell.addData(CAPABILITY, cap);
				SpellUtils.runSpell(pearl, spell);
				setCooldown(world, null, null, pearl, spell);
			}
		}
	}
}
