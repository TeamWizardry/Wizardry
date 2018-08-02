package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.block.TileManaInteractor;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.client.render.block.TileManaBatteryRenderer;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;

@TileRegister("mana_battery")
@TileRenderer(TileManaBatteryRenderer.class)
public class TileManaBattery extends TileManaInteractor {

	public static final HashSet<BlockPos> poses = new HashSet<>();

	static {
		poses.add(new BlockPos(2, 1, 2));
		poses.add(new BlockPos(-2, 1, 2));
		poses.add(new BlockPos(2, 1, -2));
		poses.add(new BlockPos(-2, 1, -2));

		poses.add(new BlockPos(4, 2, 4));
		poses.add(new BlockPos(-4, 2, 4));
		poses.add(new BlockPos(4, 2, -4));
		poses.add(new BlockPos(-4, 2, -4));
	}

	@Save
	public boolean revealStructure = false;


	public TileManaBattery() {
		super(1000, 1000);
		setAllowOutsideSucking(false);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void onSuckFrom(TileManaInteractor from) {
		super.onSuckFrom(from);

		if (from instanceof TilePearlHolder && CapManager.isManaEmpty(from.getWizardryCap())) {

			((TilePearlHolder) from).setItemStack(ItemStack.EMPTY);
			from.markDirty();

			if (!world.isRemote) {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);

				ClientRunnable.run(new ClientRunnable() {
					@Override
					@SideOnly(Side.CLIENT)
					public void runIfClient() {
						LibParticles.EXPLODE(world, new Vec3d(from.getPos()).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 0.5, 0.5, 50, 50, 10, true);
					}
				});
			}
		}
	}

	@Override
	public void update() {
		super.update();

		if (getBlockType() == ModBlocks.MANA_BATTERY && !((BlockManaBattery) getBlockType()).isStructureComplete(getWorld(), getPos()))
			return;

		if (getBlockType() != ModBlocks.CREATIVE_MANA_BATTERY) {
			for (BlockPos relative : poses) {
				BlockPos target = getPos().add(relative);
				TileEntity tile = world.getTileEntity(target);
				if (tile instanceof TilePearlHolder) {
					if (!((TilePearlHolder) tile).isPartOfStructure) {
						((TilePearlHolder) tile).isPartOfStructure = true;
						((TilePearlHolder) tile).structurePos = getPos();
						((TilePearlHolder) tile).setAllowOutsideSucking(false);
						tile.markDirty();
					}
				}
			}

		} else {
			CapManager.forObject(getWizardryCap())
					.setMana(CapManager.getMaxMana(getWizardryCap()))
					.setBurnout(0)
					.close();
		}
	}
}
