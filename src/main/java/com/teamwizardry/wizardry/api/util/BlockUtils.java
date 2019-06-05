package com.teamwizardry.wizardry.api.util;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Demoniaque.
 */
public final class BlockUtils {

	private final static UUID uuid = UUID.randomUUID();
	private final static GameProfile breaker = new GameProfile(uuid, "Wizardry Block Breaker");
	private final static GameProfile placer = new GameProfile(uuid, "Wizardry Block Placer");

	private BlockUtils() {
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

	/**
	 * Tries placing a block safely and fires an event for it.
	 *
	 * @return Whether the block was successfully placed
	 */
	public static boolean placeBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable EntityPlayerMP player) {
		if (!world.isBlockLoaded(pos)) return false;

		EntityPlayerMP playerMP;
		if (player == null) {
			playerMP = FakePlayerFactory.get((WorldServer) world, placer);
			playerMP.setPosition(pos.getX(), pos.getY(), pos.getZ());
			playerMP.setSneaking(true);
		} else playerMP = player;

		if (!hasEditPermission(pos, playerMP)) return false;

		BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(BlockSnapshot.getBlockSnapshot(world, pos), Blocks.AIR.getDefaultState(), playerMP, playerMP.getActiveHand());
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			world.setBlockState(pos, state);
			world.notifyBlockUpdate(pos, state, state, 3);
			return true;
		}
		return false;
	}

	public static boolean placeBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, @Nonnull ItemStack stack) {
		if (!world.isBlockLoaded(pos)) return false;

		FakePlayer player = FakePlayerFactory.get((WorldServer) world, placer);
		player.setPosition(pos.getX(), pos.getY(), pos.getZ());
		player.setHeldItem(EnumHand.MAIN_HAND, stack);
		player.setSneaking(true);

		if (!hasEditPermission(pos, player)) return false;
		if (!world.isBlockLoaded(pos)) return false;

		if (world.getTileEntity(pos) == null) {
			if (facing == null) {
				for (EnumFacing enumFacing : EnumFacing.VALUES) {
					EnumActionResult result = player.interactionManager.processRightClickBlock(
							player, world, stack, EnumHand.MAIN_HAND,
							pos, enumFacing, 0, 0, 0);
					if (result != EnumActionResult.FAIL) return true;
				}
			} else {
				EnumActionResult result = player.interactionManager.processRightClickBlock(
						player, world, stack, EnumHand.MAIN_HAND,
						pos, facing, 0, 0, 0);
				return result != EnumActionResult.FAIL;
			}
		}

		return false;
	}

	/**
	 * Tries breaking a block safely and fires an event for it.
	 *
	 * @return Whether the block was successfully broken
	 */
	public static boolean breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nullable IBlockState oldState, @Nullable EntityPlayerMP player, boolean drop) {
		if (!world.isBlockLoaded(pos)) return false;

		EntityPlayerMP playerMP;
		if (player == null) {
			playerMP = FakePlayerFactory.get((WorldServer) world, breaker);
			playerMP.setPosition(pos.getX(), pos.getY(), pos.getZ());
		} else playerMP = player;

		if (player != null && !(player instanceof FakePlayer))
			if (!hasBreakPermission(world, pos, playerMP)) return false;

		if (oldState == null) oldState = world.getBlockState(pos);

		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, oldState, playerMP);

		MinecraftForge.EVENT_BUS.post(event);

		if (!event.isCanceled()) {
			TileEntity tile = world.getTileEntity(pos);
			Block block = oldState.getBlock();

			if (block.removedByPlayer(oldState, world, pos, playerMP, true)) {
				block.onPlayerDestroy(world, pos, oldState);
				block.harvestBlock(world, playerMP, pos, oldState, tile, ItemStack.EMPTY);
			} else
				world.setBlockToAir(pos);

			return true;
		}
		return false;
	}

	public static boolean hasBreakPermission(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayerMP player) {
		return hasEditPermission(pos, player) && !(ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, pos) == -1);
	}

	public static boolean hasEditPermission(@Nonnull BlockPos pos, @Nonnull EntityPlayerMP player) {
		if (FMLCommonHandler.instance().getMinecraftServerInstance().isBlockProtected(player.getEntityWorld(), pos, player))
			return false;
		IBlockState block = player.getEntityWorld().getBlockState(pos);
		if (block.getBlockHardness(player.getEntityWorld(), pos) == -1.0F && !player.capabilities.isCreativeMode)
			return false;
		for (EnumFacing e : EnumFacing.VALUES)
			if (!player.canPlayerEdit(pos, e, player.getHeldItemMainhand()))
				return false;
		return true;
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

	/**
	 * Reimplement ForgeHooks method without tool checking.
	 */
	private static boolean canHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		state = state.getBlock().getActualState(state, world, pos);

		ItemStack stack = player.getHeldItemMainhand();
		String tool = block.getHarvestTool(state);
		if (stack.isEmpty() || tool == null) {
			return true;
		}

		int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
		if (toolLevel < 0) {
			return true;
		}

		return toolLevel >= block.getHarvestLevel(state);
	}

	/**
	 * CODE FROM TINKER'S CONSTRUCT
	 * <p>
	 * Preconditions for {@link #breakExtraBlock(ItemStack, World, EntityPlayer, BlockPos, float)} and {@link #shearExtraBlock(ItemStack, World, EntityPlayer, BlockPos, float)}
	 *
	 * @return true if the extra block can be broken
	 */
	private static boolean canBreakExtraBlock(World world, EntityPlayer player, BlockPos pos, float executionStrength) {
		// prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
		if (world.isAirBlock(pos)) {
			return false;
		}

		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		float strength = ForgeHooks.blockStrength(state, player, world, pos);

		// only harvestable blocks that aren't impossibly slow to harvest
		if (!canHarvestBlock(block, player, world, pos) || strength > executionStrength) {
			return false;
		}

		// From this point on it's clear that the player CAN break the block

		if (player.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, pos, state, player);
			if (block.removedByPlayer(state, world, pos, player, false)) {
				block.onPlayerDestroy(world, pos, state);
			}

			// send update to client
			if (!world.isRemote) {
				if (player instanceof EntityPlayerMP)
					((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
			}
			return false;
		}
		return true;
	}

	public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, float executionStrength) {
		if (!canBreakExtraBlock(world, player, pos, executionStrength)) {
			return;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		// callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
		//	stack.onBlockDestroyed(world, state, pos, player);

		// server sided handling
		if (!world.isRemote) {
			// send the blockbreak event
			int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
			if (xp == -1) {
				return;
			}

			// serverside we reproduce ItemInWorldManager.tryHarvestBlock

			TileEntity tileEntity = world.getTileEntity(pos);
			// ItemInWorldManager.removeBlock
			if (block.removedByPlayer(state, world, pos, player, true)) { // boolean is if block can be harvested, checked above
				block.onPlayerDestroy(world, pos, state);
				block.harvestBlock(world, player, pos, state, tileEntity, stack);
				block.dropXpOnBlockBreak(world, pos, xp);
			}

			// always send block update to client
			((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
		}
		// client sided handling
		else {
			// clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			world.playBroadcastSound(2001, pos, Block.getStateId(state));
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onPlayerDestroy(world, pos, state);
			}
			// callback to the tool
			stack.onBlockDestroyed(world, state, pos, player);

			if (stack.getCount() == 0 && stack == player.getHeldItemMainhand()) {
				ForgeEventFactory.onPlayerDestroyItem(player, stack, EnumHand.MAIN_HAND);
				player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
			}

			// send an update to the server, so we get an update back
			//if(PHConstruct.extraBlockUpdates)
			NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();
			assert netHandlerPlayClient != null;
			netHandlerPlayClient.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft
					.getMinecraft().objectMouseOver.sideHit));
		}
	}

	/**
	 * CODE FROM TINKER'S CONSTRUCT
	 * <p>
	 * Same as {@link #breakExtraBlock(ItemStack, World, EntityPlayer, BlockPos, float)}, but attempts to shear the block first
	 *
	 * @param stack
	 * @param world
	 * @param player
	 * @param pos
	 */
	public static void shearExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, float executionStrength) {
		if (!canBreakExtraBlock(world, player, pos, executionStrength)) {
			return;
		}
		// if we cannot shear the block, just run normal block break code
		if (!shearBlock(stack, world, player, pos)) {
			breakExtraBlock(stack, world, player, pos, executionStrength);
		}
	}

	/**
	 * CODE FROM TINKER'S CONSTRUCT
	 * <p>
	 * Attempts to shear a block using IShearable logic
	 *
	 * @return true if the block was successfully sheared
	 */
	public static boolean shearBlock(ItemStack itemstack, World world, EntityPlayer player, BlockPos pos) {
		// only serverside since it creates entities
		if (world.isRemote) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(itemstack, world, pos)) {
				int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack);
				List<ItemStack> drops = target.onSheared(itemstack, world, pos, fortune);

				for (ItemStack stack : drops) {
					float f = 0.7F;
					double d = RandUtil.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = RandUtil.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = RandUtil.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.getEntityWorld(), pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, stack);
					entityitem.setDefaultPickupDelay();
					world.spawnEntity(entityitem);
				}

				itemstack.onBlockDestroyed(world, state, pos, player);
				//player.addStat(net.minecraft.stats.StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);

				world.setBlockToAir(pos);

				return true;
			}
		}
		return false;
	}

}
