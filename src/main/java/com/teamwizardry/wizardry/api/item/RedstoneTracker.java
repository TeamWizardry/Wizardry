package com.teamwizardry.wizardry.api.item;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketDevilDustFizzle;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class RedstoneTracker {

	public static RedstoneTracker INSTANCE = new RedstoneTracker();
	private HashMultimap<Integer, RedstoneBluetooth> blueteeth = HashMultimap.create();

	private RedstoneTracker() {
	}

	public void addRedstone(EntityItem redstone, World world) {
		if (redstone.getItem().getItem() == Items.REDSTONE) {
			blueteeth.put(world.provider.getDimension(), new RedstoneBluetooth(redstone, world));
		}
	}

	public void tick(World tickedWorld) {
		if (blueteeth.isEmpty()) return;
		int dim = tickedWorld.provider.getDimension();
		Set<RedstoneBluetooth> worldTeeth = blueteeth.get(dim);
		if (worldTeeth.isEmpty()) return;

		ArrayList<RedstoneBluetooth> temp = new ArrayList<>(worldTeeth);
		temp.removeIf(bluetooth -> {
			bluetooth.tick();
			if (bluetooth.isExpired()) {
				blueteeth.remove(dim, bluetooth);
			}
			return false;
		});
	}

	public class RedstoneBluetooth {

		private EntityItem redstone;
		private boolean cooking = false;
		private int cookTime = 1000;
		private boolean expired = false;
		private World world;
		@Nullable
		private BlockPos position = null;
		private int count;

		RedstoneBluetooth(EntityItem redstone, World world) {
			this.redstone = redstone;
			this.world = world;
			this.count = redstone.getItem().getCount();
		}

		public void tick() {
			if (redstone == null || redstone.isDead || world == null || expired) {
				expired = true;
				return;
			}

			if (!world.isBlockLoaded(redstone.getPosition()))
				return;
			
			if (!cooking) {
				if (redstone.isInLava() || redstone.isInsideOfMaterial(Material.FIRE) || redstone.isInsideOfMaterial(Material.LAVA)) {
					position = redstone.getPosition();
				}
				if (position == null) {
					BlockPos fire = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.FIRE, Blocks.LAVA, Blocks.FLOWING_LAVA);
					if (fire != null) {
						position = fire;
					}
				}

				if (position != null) {
					world.removeEntity(redstone);
					cooking = true;
					PacketHandler.NETWORK.sendToAllAround(new PacketDevilDustFizzle(new Vec3d(position).addVector(0.5, 0.5, 0.5), cookTime), new NetworkRegistry.TargetPoint(world.provider.getDimension(), position.getX(), position.getY(), position.getZ(), 30));
				}
			} else {
				if (position == null) {
					expired = true;
					return;
				}
				if (--cookTime <= 0) {
					expired = true;

					EntityItem devilDust = new EntityItem(world, position.getX() + 0.5, position.getY() + 0.75, position.getZ() + 0.5, new ItemStack(ModItems.DEVIL_DUST, count));
					devilDust.setPickupDelay(5);
					devilDust.motionY = 0.8;
					devilDust.forceSpawn = true;
					devilDust.setEntityInvulnerable(true);
					world.spawnEntity(devilDust);

				} else {
					IBlockState state = world.getBlockState(position);
					if (state.getMaterial() == Material.LAVA || state.getBlock() == Blocks.FIRE) {
						if ((cookTime % 10) == 0) {
							PacketHandler.NETWORK.sendToAllAround(new PacketDevilDustFizzle(new Vec3d(position).addVector(0.5, 0.5, 0.5), cookTime), new NetworkRegistry.TargetPoint(world.provider.getDimension(), position.getX(), position.getY(), position.getZ(), 20));
							world.playSound(null, position.getX(), position.getY(), position.getZ(), ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) RandUtil.nextDouble(0.8, 1.3));
						}
					} else expired = true;
				}
			}
		}

		public boolean isExpired() {
			return expired;
		}
	}
}
