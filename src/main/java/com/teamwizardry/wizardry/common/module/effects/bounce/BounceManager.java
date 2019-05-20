package com.teamwizardry.wizardry.common.module.effects.bounce;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class BounceManager {

	public static final BounceManager INSTANCE = new BounceManager();

	private final Set<BouncyBlock> blocks = new HashSet<>();

	private BounceManager() {
	}

	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;

		if (!shouldBounce(entity)) return;

		INSTANCE.blocks.removeIf(bouncyBlock -> entity.world.getTotalWorldTime() - bouncyBlock.tickSpawned > bouncyBlock.expiry);

		boolean isClient = entity.getEntityWorld().isRemote;
		if (event.getDistance() > 0.5) {
			event.setDamageMultiplier(0);
			entity.fallDistance = 0;

			if (isClient) {
				entity.motionY *= -0.9;
				entity.isAirBorne = true;
				entity.onGround = false;
				double f = 0.95;
				entity.motionX /= f;
				entity.motionZ /= f;
				PacketHandler.NETWORK.sendToServer(new PacketBounce());
			} else {
				event.setCanceled(true);
			}
			entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
			BounceHandler.addBounceHandler(entity, entity.motionY);

		}
	}

	@SubscribeEvent
	public static void onFlyableFall(PlayerFlyableFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;

		if (!shouldBounce(entity)) return;

		INSTANCE.blocks.removeIf(bouncyBlock -> entity.world.getTotalWorldTime() - bouncyBlock.tickSpawned > bouncyBlock.expiry);

		boolean isClient = entity.getEntityWorld().isRemote;
		if (event.getDistance() > 0.5) {
			entity.fallDistance = 0;

			if (isClient) {
				entity.motionY *= -0.9;
				entity.isAirBorne = true;
				entity.onGround = false;
				double f = 0.95;
				entity.motionX /= f;
				entity.motionZ /= f;
				PacketHandler.NETWORK.sendToServer(new PacketBounce());
			}
			entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
			BounceHandler.addBounceHandler(entity, entity.motionY);

		}
	}

	private static boolean shouldBounce(EntityLivingBase entity) {
		if (entity.isPotionActive(ModPotions.BOUNCING)) {
			return true;
		} else {
			for (BouncyBlock block : INSTANCE.blocks) {
				if (block.doesMatch(entity)) {
					return true;
				}
			}
		}
		return false;
	}

	public void forEntity(EntityLivingBase entity, int time) {
		entity.addPotionEffect(new PotionEffect(ModPotions.BOUNCING, time, 0, true, false));
	}

	public void forBlock(World world, BlockPos pos, int time) {
		for (BouncyBlock block : blocks) {
			if (block.doesMatch(world, pos)) {
				block.resetTimer(world, time);
				return;
			}
		}
		blocks.add(new BouncyBlock(world, pos, time));
	}

	private static class BouncyBlock {

		public final int dim;
		public final BlockPos pos;
		public long tickSpawned;
		public int expiry;

		public BouncyBlock(@Nonnull World world, @Nonnull BlockPos pos, int expiry) {
			this.dim = world.provider.getDimension();
			this.pos = pos;
			this.expiry = expiry;
			tickSpawned = world.getTotalWorldTime();
		}

		public boolean doesMatch(World world, BlockPos pos) {
			return this.dim == world.provider.getDimension() && this.pos.equals(pos);
		}

		public boolean doesMatch(Entity entity) {
			Vec3d vec = entity.getPositionVector();
			if (vec.x > pos.getX() && vec.x < pos.getX() + 1 && vec.z > pos.getZ() && vec.z < pos.getZ() + 1)
				return this.dim == entity.world.provider.getDimension();
			return false;
		}

		public void resetTimer(World world, int expiry) {
			tickSpawned = world.getTotalWorldTime();
			this.expiry = expiry;
		}
	}
}
