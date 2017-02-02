package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

/**
 * Class provided by MCJty
 */
public class TeleportUtil {

	public static void teleportToDimension(EntityPlayer player, int dimension, double x, double y, double z) {
		if (!(player instanceof EntityPlayerMP)) return;
		int oldDimension = player.world.provider.getDimension();
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
		MinecraftServer server = ((EntityPlayerMP) player).world.getMinecraftServer();
		WorldServer worldServer = server.worldServerForDimension(dimension);
		player.addExperienceLevel(0);


		worldServer.getMinecraftServer().getPlayerList().transferPlayerToDimension(entityPlayerMP, dimension, new CustomTeleporter(worldServer, x, y, z));
		player.setPositionAndUpdate(x, y, z);
		if (oldDimension == 1) {
			// For some reason teleporting out of the end does weird things.
			player.setPositionAndUpdate(x, y, z);
			worldServer.spawnEntity(player);
			worldServer.updateEntityWithOptionalForce(player, false);
		}
	}

	public static void blink(EntityLivingBase entity, double dist) {
		if (entity == null) return;
		Vec3d look = entity.getLookVec();

		double x = entity.posX += look.xCoord * dist;
		double y = entity.posY += Math.max(0, look.yCoord * dist);
		double z = entity.posZ += look.zCoord * dist;

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP mp = (EntityPlayerMP) entity;
			mp.connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
		} else entity.setPosition(x, y, z);
	}


	public static class CustomTeleporter extends Teleporter {
		private final WorldServer worldServer;

		private final double x;
		private final double y;
		private final double z;


		public CustomTeleporter(WorldServer world, double x, double y, double z) {
			super(world);
			worldServer = world;
			this.x = x;
			this.y = y;
			this.z = z;

		}

		@Override
		public void placeInPortal(@NotNull Entity entity, float rotationYaw) {
			worldServer.getBlockState(new BlockPos((int) x, (int) y, (int) z));

			entity.setPosition(x, y, z);
			entity.motionX = 0.0f;
			entity.motionY = 0.0f;
			entity.motionZ = 0.0f;
		}
	}
}
