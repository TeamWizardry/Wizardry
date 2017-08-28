package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.structure.Structure;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public interface IStructure {

	Structure getStructure();

	Vec3i offsetToCenter();

	default boolean tickStructure(World world, EntityPlayer player, BlockPos pos) {
		if (player.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND) {
			boolean complete = true;
			for (Template.BlockInfo info : getStructure().blockInfos()) {
				BlockPos newPos = info.pos.add(pos).subtract(offsetToCenter());
				if (info.blockState == null) continue;

				IBlockState state = world.getBlockState(newPos);
				if (state.getBlock() == info.blockState.getBlock()) continue;

				if (newPos.toLong() != pos.toLong() && state.getBlock() != Blocks.AIR && info.blockState.getBlock() != state.getBlock()) {
					if (world.isRemote) {
						LibParticles.STRUCTURE_FLAIR(world, new Vec3d(newPos).addVector(0.5, 0.5, 0.5), Color.RED);
						LibParticles.STRUCTURE_BEACON(world, new Vec3d(newPos).addVector(0.5, 0.5, 0.5), Color.RED);
						LibParticles.BLOCK_HIGHLIGHT(world, newPos, Color.RED);
					}
					return false;
				}

				boolean blockAvailable = false;
				if (!player.isCreative())
					for (ItemStack invStack : player.inventory.mainInventory)
						if (invStack != null
								&& invStack.isItemEqual(new ItemStack(info.blockState.getBlock()))) {
							blockAvailable = true;
							invStack.setCount(invStack.getCount() - 1);
							break;
						}
				if (!blockAvailable && !player.isCreative()) continue;

				if (newPos.toLong() == pos.toLong()) continue;

				world.setBlockState(newPos, info.blockState);
				if (world.isRemote)
					LibParticles.STRUCTURE_FLAIR(world, new Vec3d(newPos).addVector(0.5, 0.5, 0.5), Color.BLUE);
				complete = false;
				break;
			}
			if (complete) {
				if (world.isRemote)
					LibParticles.STRUCTURE_FLAIR(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.GREEN);
				return true;
			}

		} else {
			for (Template.BlockInfo info : getStructure().blockInfos()) {
				BlockPos newPos = info.pos.add(pos).subtract(offsetToCenter());
				if (info.blockState == null) continue;
				if (world.getBlockState(newPos).getBlock() != info.blockState.getBlock()) {
					if (world.isRemote) {
						LibParticles.STRUCTURE_BEACON(world, new Vec3d(newPos).addVector(0.5, 0.5, 0.5), Color.RED);
						LibParticles.STRUCTURE_FLAIR(world, new Vec3d(newPos).addVector(0.5, 0.5, 0.5), Color.RED);
						LibParticles.BLOCK_HIGHLIGHT(world, newPos, Color.RED);
					}
					return false;
				} else if (world.getBlockState(newPos) != info.blockState.getBlock())
					world.setBlockState(newPos, info.blockState);
			}
			return true;
		}
		return false;
	}

	default Set<BlockPos> getStructureBoundries() {
		Set<BlockPos> boundries = new HashSet<>();
		BlockPos center = BlockPos.ORIGIN;
		BlockPos min = center.subtract(offsetToCenter()).subtract(new Vec3i(1, 1, 1));
		BlockPos max = center.add(offsetToCenter()).add(new Vec3i(1, 2, 1));

		for (int i = min.getX(); i < max.getX() + 1; i++)
			for (int j = min.getY(); j < max.getY() + 1; j++)
				for (int k = min.getZ(); k < max.getZ() + 1; k++)
					if ((i == min.getX() || i == max.getX())
							|| (j == min.getY() || j == max.getY())
							|| (k == min.getZ() || k == max.getZ()))
						boundries.add(center.add(i, j, k));
		return boundries;
	}

	default boolean isObstructed(World world, BlockPos pos) {
		BlockPos center = BlockPos.ORIGIN;
		BlockPos min = center.subtract(offsetToCenter()).subtract(new Vec3i(1, 1, 1));
		BlockPos max = center.add(offsetToCenter()).add(new Vec3i(1, 2, 1));

		for (int i = min.getX(); i < max.getX() + 1; i++)
			for (int j = min.getY(); j < max.getY() + 1; j++)
				for (int k = min.getZ(); k < max.getZ() + 1; k++)
					if ((i == min.getX() || i == max.getX())
							|| (j == min.getY() || j == max.getY())
							|| (k == min.getZ() || k == max.getZ())) {
						if (!world.isAirBlock(pos.add(center.add(i, j, k)))) {
							return true;
						}
					}
		return false;
	}

	@SideOnly(Side.CLIENT)
	default boolean renderBoundries(World world, BlockPos pos) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean isObstructed = isObstructed(world, pos);

		if (player.getDistanceSqToCenter(pos) <= 500)
			if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND) {
				for (BlockPos outline : getStructureBoundries())
					if (RandUtil.nextInt(200) == 0) {
						LibParticles.STRUCTURE_BOUNDS(world, new Vec3d(pos.add(outline)).addVector(0.5, 0.5, 0.5), isObstructed ? Color.RED : Color.GREEN);
					}
				return false;
			}
		return true;
	}
}
