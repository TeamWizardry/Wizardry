package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.wizardry.api.block.TileManaInteracter;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;

@TileRegister("mana_battery")
public class TileManaBattery extends TileManaInteracter {

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


	public TileManaBattery() {
		super(1000000, 1000000);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();

		if (!((BlockManaBattery) getBlockType()).isStructureComplete(getWorld(), getPos())) return;

		if (!new CapManager(getCap()).isManaFull()) {
			for (BlockPos relative : poses) {
				BlockPos target = getPos().add(relative);
				TileEntity tile = world.getTileEntity(target);
				if (tile != null && tile instanceof TilePearlHolder) {
					if (!((TilePearlHolder) tile).isBenign) {
						((TilePearlHolder) tile).isBenign = true;
						((TilePearlHolder) tile).structurePos = getPos();
						tile.markDirty();
						world.notifyBlockUpdate(target, world.getBlockState(target), world.getBlockState(target), 3);
					}
					((TilePearlHolder) tile).suckManaFrom(getWorld(), getPos(), getCap(), target, 10, false);
				}
			}
		}
	}
}
