package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.block.TileManaFaucet;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.fluid.FluidBlockMana;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;

@TileRegister("mana_battery")
public class TileManaBattery extends TileManaFaucet implements ITickable {

	public TileManaBattery() {
		super(100000, 100000);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		int count = 0;
		for (int i = -4; i < 4; i++)
			for (int j = -4; j < 4; j++)
				if (world.getBlockState(getPos().add(i, -3, j)) == FluidBlockMana.instance.getDefaultState())
					count++;

		if (count < 21) return;

		CapManager manager = new CapManager(cap);
		if (manager.isManaFull()) return;

		PosUtils.ManaBatteryPositions positions = new PosUtils.ManaBatteryPositions(world, pos);
		ArrayList<BlockPos> poses = new ArrayList<>(positions.takenPoses);
		if (poses.isEmpty()) return;
		for (BlockPos target : poses) {
			if (!world.isBlockLoaded(target)) continue;
			IBlockState state = world.getBlockState(target);

			if (state.getBlock() == ModBlocks.PEARL_HOLDER) {
				TileEntity tile = world.getTileEntity(target);
				if (tile == null) continue;
				if (!(tile instanceof TilePearlHolder)) continue;
				TilePearlHolder holder = (TilePearlHolder) world.getTileEntity(target);

				if (holder == null || holder.pearl == null || holder.pearl.isEmpty() || holder.pearl.getItem() != ModItems.MANA_ORB)
					continue;

				CapManager orbManager = new CapManager(holder.pearl);
				if (orbManager.getMana() <= 0) {
					holder.pearl = ItemStack.EMPTY;
					holder.markDirty();

					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(target).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 0.5, 0.5, 50, 50, 10),
							new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
				} else {
					if (!addMana(10)) break;
					else orbManager.removeMana(10);
				}
			}
		}
	}
}
