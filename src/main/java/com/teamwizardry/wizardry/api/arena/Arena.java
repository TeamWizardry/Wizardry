package com.teamwizardry.wizardry.api.arena;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

public abstract class Arena implements INBTSerializable<NBTTagCompound> {

	private final World world;
	private final BlockPos center;
	private final double radius;
	private final double height;
	private final int bossID;
	private final Set<UUID> players;
	private boolean isActive = false;
	private boolean hasEnded = false;
	private long startTick = 0;

	public Arena(World world, BlockPos center, double radius, double height, int bossID, Set<UUID> players) {
		this.world = world;
		this.center = center;
		this.radius = radius;
		this.height = height;
		this.bossID = bossID;
		this.players = players;
	}

	@SubscribeEvent
	public void tickBoss(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().getEntityId() != bossID) return;
		if (event.getEntityLiving().getDistance(center.getX() + 0.5, center.getY(), center.getZ() + 0.5) > radius) {
			end();
		}
	}

	@SubscribeEvent
	public void tickPlayer(TickEvent.PlayerTickEvent event) {
		if (!players.contains(event.player.getUniqueID())) {
			return;
		}
		if (event.player.getDistance(center.getX() + 0.5, center.getY(), center.getZ() + 0.5) > radius) {
			end();
		}
		if (event.player.capabilities.allowFlying) {
			event.player.capabilities.allowFlying = false;
		}
		if (event.player.capabilities.isFlying) {
			event.player.capabilities.isFlying = false;
		}
		if (event.player.capabilities.allowEdit) {
			event.player.capabilities.allowEdit = false;
		}
	}

	public boolean sanityCheck() {
		for (double x = -radius; x < radius; x++)
			for (double z = -radius; z < radius; z++)
				for (int y = 0; y < height; y++) {
					BlockPos pos = center.add(x, y, z);
					if (pos.getDistance(center.getX(), center.getY(), center.getZ()) > radius) {
						return false;
					}
					if (!world.canBlockSeeSky(pos)) {
						return false;
					}
					if (!world.isAirBlock(pos)) {
						return false;
					}
				}
		return true;
	}

	public abstract void dealWithStructureConflict();

	public void begin() {
		isActive = true;
		hasEnded = false;
		startTick = System.currentTimeMillis();
	}

	public abstract void tick(long timeMillis);

	public void end() {
		hasEnded = true;
		isActive = false;
		for (UUID uuid : players) {
			EntityPlayer player = world.getPlayerEntityByUUID(uuid);
			if (player == null) continue;
			if (player.capabilities.allowEdit) {
				player.capabilities.allowEdit = true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void highlightBorders(Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, world, new InterpCircle(new Vec3d(center).addVector(0.5, 0, 0.5), new Vec3d(0, 1, 0), (float) radius, 1, RandUtil.nextFloat()), (int) (radius * 5), 0, (aFloat, particleBuilder) -> {
			particleBuilder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			particleBuilder.setLifetime(RandUtil.nextInt(10, 20));
			particleBuilder.setColor(color);
			particleBuilder.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.001, 0.001),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.001, 0.001)
			));
		});
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public long getStartTick() {
		return startTick;
	}

	public boolean hasEnded() {
		return hasEnded;
	}

	public void setHasEnded(boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		// TODO

		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// TODO

	}
}
