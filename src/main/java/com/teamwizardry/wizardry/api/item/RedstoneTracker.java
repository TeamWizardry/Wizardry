package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketDevilDustFizzle;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;

public class RedstoneTracker {

	public static RedstoneTracker INSTANCE = new RedstoneTracker();
	private ArrayList<RedstoneBluetooth> blueteeth = new ArrayList<>();

	private RedstoneTracker() {
	}

	public void addRedstone(EntityItem redstone, World world) {
		if (redstone.getItem().getItem() == Items.REDSTONE) {
			blueteeth.add(new RedstoneBluetooth(redstone, world));
		}
	}

	public void tick() {
		if (blueteeth.isEmpty()) return;

		ArrayList<RedstoneBluetooth> temp = new ArrayList<>(blueteeth);
		temp.removeIf(bluetooth -> {
			bluetooth.tick();
			if (bluetooth.isExpired()) {
				blueteeth.remove(bluetooth);
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
		private BlockPos position;
		private int count;

		RedstoneBluetooth(EntityItem redstone, World world) {
			this.redstone = redstone;
			this.world = world;
			this.count = redstone.getItem().getCount();
		}

		public void tick() {
			if (redstone == null || world == null) return;
			if (!expired && !cooking) {
				if (redstone.isDead || redstone.isBurning() || redstone.isInLava()) {
					cooking = true;

					BlockPos fire = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.FIRE);
					BlockPos lava = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.LAVA);

					if (fire != null && world.isMaterialInBB(redstone.getEntityBoundingBox().grow(0.1, 0.1, 0.1), Material.FIRE)) {
						position = fire;
					} else if (lava != null && world.isMaterialInBB(redstone.getEntityBoundingBox().grow(0.1, 0.1, 0.1), Material.LAVA)) {
						position = lava;
					}
					world.removeEntity(redstone);

					if (position != null)
						PacketHandler.NETWORK.sendToAllAround(new PacketDevilDustFizzle(new Vec3d(position).addVector(0.5, 0.5, 0.5), cookTime), new NetworkRegistry.TargetPoint(world.provider.getDimension(), position.getX(), position.getY(), position.getZ(), 30));
				}

				if (redstone.isInsideOfMaterial(Material.FIRE) || redstone.isInsideOfMaterial(Material.LAVA)) {
					cooking = true;

					BlockPos fire = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.FIRE);
					BlockPos lava = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.LAVA);

					if (fire != null && world.isMaterialInBB(redstone.getEntityBoundingBox().grow(0.1, 0.1, 0.1), Material.FIRE)) {
						position = fire;
					} else if (lava != null && world.isMaterialInBB(redstone.getEntityBoundingBox().grow(0.1, 0.1, 0.1), Material.LAVA)) {
						position = lava;
					}
					world.removeEntity(redstone);

					if (position != null)
						PacketHandler.NETWORK.sendToAllAround(new PacketDevilDustFizzle(new Vec3d(position).addVector(0.5, 0.5, 0.5), cookTime), new NetworkRegistry.TargetPoint(world.provider.getDimension(), position.getX(), position.getY(), position.getZ(), 30));
				}

			} else if (!expired && position != null) {
				if (--cookTime <= 0) {
					expired = true;

					EntityItem devilDust = new EntityItem(world, position.getX() + 0.5, position.getY() + 0.75, position.getZ() + 0.5, new ItemStack(ModItems.DEVIL_DUST, count));
					devilDust.setPickupDelay(5);
					devilDust.motionY = 0.8;
					devilDust.forceSpawn = true;
					devilDust.setEntityInvulnerable(true);
					world.spawnEntity(devilDust);

				} else if (world.getBlockState(position).getBlock() == Blocks.FIRE) {
					if ((cookTime % 10) == 0)
						world.playSound(null, position.getX(), position.getY(), position.getZ(), ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) RandUtil.nextDouble(0.8, 1.3));
				} else expired = true;
			}
		}

		public boolean isExpired() {
			return expired;
		}
	}
}
