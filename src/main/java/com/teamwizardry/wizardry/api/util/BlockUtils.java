package com.teamwizardry.wizardry.api.util;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Demoniaque.
 */
public final class BlockUtils {

	private final static UUID ID = UUID.nameUUIDFromBytes(Wizardry.MODID.getBytes(StandardCharsets.UTF_8));
	private final static GameProfile BREAKER = new GameProfile(ID, "Wizardry Block Breaker");
	private final static GameProfile PLACER = new GameProfile(ID, "Wizardry Block Placer");

	private BlockUtils() {
	}

	public static boolean isBlockBlacklistedInPhaseEffect(Block block) {
		ResourceLocation registry = block.getRegistryName();

		if (registry == null) {
			return false;
		}

		String name = registry.toString();

		for (String regName : ConfigValues.phaseBlocksBlackList) {
			if (name.equals(regName)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAnyAir(IBlockState state) {
		return state.getBlock() == Blocks.AIR || state.getBlock() == ModBlocks.FAKE_AIR;
	}

	public static boolean isAnyAir(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == Blocks.AIR || state.getBlock() == ModBlocks.FAKE_AIR;
	}

	public static boolean isAnyAir(Block block) {
		return block == Blocks.AIR || block == ModBlocks.FAKE_AIR;
	}

	public static EntityPlayerMP makePlacer(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			return (EntityPlayerMP) entity;
		}
		EntityPlayerMP bro = FakePlayerFactory.get((WorldServer) world, PLACER);
		bro.moveToBlockPosAndAngles(pos, 0, -90);
		bro.setSneaking(true);
		return bro;
	}

	/**
	 * Places the specified block into the world at the specified position if it is possible to do so without violating permission restrictions.
	 *
	 * @return <tt>true</tt> if the specified block was successfully placed into the world
	 */
	public static boolean placeBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayerMP player) {
		if (!world.isBlockLoaded(pos)) return false;

		if (!hasEditPermission(pos, player)) return false;

		BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(BlockSnapshot.getBlockSnapshot(world, pos), Blocks.AIR.getDefaultState(), player, player.getActiveHand());
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) return false;

		world.setBlockState(pos, state);
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	public static boolean placeBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull ItemStack stack) {
		if (!world.isBlockLoaded(pos)) return false;

		FakePlayer player = FakePlayerFactory.get((WorldServer) world, PLACER);
		player.moveToBlockPosAndAngles(pos, 0, -90);
		player.setHeldItem(EnumHand.MAIN_HAND, stack);
		player.setSneaking(true);

		if (!hasEditPermission(pos, player)) return false;

		EnumActionResult result = player.interactionManager.processRightClickBlock(
				player, world, stack, EnumHand.MAIN_HAND,
				pos, facing, 0, 0, 0);
		return result != EnumActionResult.FAIL;
	}

	public static EntityPlayerMP makeBreaker(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			return (EntityPlayerMP) entity;
		}
		EntityPlayerMP bro = FakePlayerFactory.get((WorldServer) world, BREAKER);
		bro.moveToBlockPosAndAngles(pos, 0, -90);
		return bro;
	}

	/**
	 * Tries breaking a block safely and fires an event for it.
	 *
	 * @return Whether the block was successfully broken
	 */
	public static boolean breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nullable IBlockState oldState, @Nonnull EntityPlayerMP player) {
		if (!world.isBlockLoaded(pos)) return false;

		if (!(player instanceof FakePlayer) && !hasEditPermission(pos, player)) return false;

		if (oldState == null) oldState = world.getBlockState(pos);

		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, oldState, player);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled()) return false;

		TileEntity tile = world.getTileEntity(pos);
		Block block = oldState.getBlock();
		if (block.removedByPlayer(oldState, world, pos, player, true)) {
			block.onPlayerDestroy(world, pos, oldState);
			block.harvestBlock(world, player, pos, oldState, tile, player.getHeldItemMainhand());
			world.notifyBlockUpdate(pos, oldState, Blocks.AIR.getDefaultState(), 3);
		} else return false;

		return true;
	}

	public static boolean hasEditPermission(@Nonnull BlockPos pos, @Nonnull EntityPlayerMP player) {
		World world = player.getEntityWorld();

		if (!world.isBlockModifiable(player, pos)) return false;

		if (world.getBlockState(pos).getBlockHardness(world, pos) == -1.0F && !player.capabilities.isCreativeMode) return false;

		return player.isAllowEdit();
	}

	public static Set<BlockPos> blocksInSquare(BlockPos center, Axis axis, int maxBlocks, int maxRange, Predicate<BlockPos> ignore) {
		Set<BlockPos> blocks = new HashSet<>();
		if (ignore.test(center)) return blocks;
		blocks.add(center);
		if (blocks.size() >= maxBlocks) return blocks;

		Queue<BlockPos> blockQueue = new LinkedList<>();
		blockQueue.add(center);

		while (!blockQueue.isEmpty()) {
			BlockPos pos = blockQueue.remove();

			for (EnumFacing facing : EnumFacing.VALUES) {
				if (facing.getAxis() == axis)
					continue;
				BlockPos shift = pos.offset(facing);
				if (shift.getX() - center.getX() > maxRange || center.getX() - shift.getX() > maxRange)
					continue;
				if (shift.getY() - center.getY() > maxRange || center.getY() - shift.getY() > maxRange)
					continue;
				if (shift.getZ() - center.getZ() > maxRange || center.getZ() - shift.getZ() > maxRange)
					continue;
				if (blocks.contains(shift))
					continue;
				if (ignore.test(shift))
					continue;
				blocks.add(shift);
				blockQueue.add(shift);
				if (blocks.size() >= maxBlocks)
					break;
			}

			if (blocks.size() >= maxBlocks)
				break;
		}

		return blocks;
	}

	public static Set<BlockPos> blocksInSquare(BlockPos center, EnumFacing facing, int maxBlocks, int maxRange, Predicate<BlockPos> ignore) {
		return blocksInSquare(center, facing.getAxis(), maxBlocks, maxRange, ignore);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> T getTileEntity(IBlockAccess world, BlockPos pos, Class<T> clazz) {
		TileEntity te = world.getTileEntity(pos);
		if (!clazz.isInstance(te))
			return null;
		return (T) te;
	}
}
