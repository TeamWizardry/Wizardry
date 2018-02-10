package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.api.block.TileManaInteracter;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.client.render.block.TileManaBatteryRenderer;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;

@TileRegister("mana_battery")
@TileRenderer(TileManaBatteryRenderer.class)
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

	@Save
	public boolean revealStructure = false;


	public TileManaBattery() {
		super(1000, 1000);
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

		if (getBlockType() == ModBlocks.MANA_BATTERY && !((BlockManaBattery) getBlockType()).isStructureComplete(getWorld(), getPos()))
			return;

		if (getBlockType() != ModBlocks.CREATIVE_MANA_BATTERY) {
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
						((TilePearlHolder) tile).suckManaFrom(getWorld(), getPos(), getCap(), target, 1, false);
					}
				}
			}
		} else {
			CapManager manager = new CapManager(getCap());
			manager.setMana(manager.getMaxMana());
			manager.setBurnout(0);
		}
	}
}
