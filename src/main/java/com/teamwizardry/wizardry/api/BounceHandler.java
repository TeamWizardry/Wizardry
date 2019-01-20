package com.teamwizardry.wizardry.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Code "borrowed" from Tinker's Construct BounceHandler
 * https://github.com/SlimeKnights/TinkersConstruct/blob/23034cb63e98bba06faf1cdc4074009daf93be1f/src/main/java/slimeknights/tconstruct/library/SlimeBounceHandler.java
 * <p>
 * I don't feel like re-inventing the wheel. Shut up.
 */
public class BounceHandler {

	private static final IdentityHashMap<Entity, BounceHandler> bouncingEntities = new IdentityHashMap<>();
	public static Set<BouncyBlock> bouncingBlocks = new HashSet<>();

	public final EntityLivingBase entityLiving;

	private int timer;
	private boolean wasInAir;
	private double bounce;
	private boolean bounced;

	private double lastMovX;
	private double lastMovZ;

	public BounceHandler(EntityLivingBase entityLiving, double bounce) {
		this.entityLiving = entityLiving;
		timer = 0;
		wasInAir = false;
		this.bounce = bounce;
		bounced = bounce == 0;

		bouncingEntities.put(entityLiving, this);
	}

	public static void addBounceHandler(World world, BlockPos pos, int duration) {
		bouncingBlocks.add(new BouncyBlock(world, pos, duration));
	}

	public static void addBounceHandler(EntityLivingBase entity) {
		addBounceHandler(entity, 0d);
	}

	public static void addBounceHandler(EntityLivingBase entity, double bounce) {
		if (!(entity instanceof EntityPlayer) || entity instanceof FakePlayer) {
			return;
		}
		BounceHandler handler = bouncingEntities.get(entity);
		if (handler == null) {
			MinecraftForge.EVENT_BUS.register(new BounceHandler(entity, bounce));
		} else if (bounce != 0) {
			handler.bounce = bounce;
			handler.bounced = false;
		}
	}

	@SubscribeEvent
	public void playerTickPost(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.player == entityLiving) {
			if (!bounced) {
				event.player.motionY = bounce;
				bounced = true;
			}

			// preserve motion
			if (!entityLiving.onGround) {
				if (lastMovX != entityLiving.motionX || lastMovZ != entityLiving.motionZ) {
					double f = 0.91d + 0.025d;
					entityLiving.motionX /= f;
					entityLiving.motionZ /= f;
					entityLiving.isAirBorne = true;
					lastMovX = entityLiving.motionX;
					lastMovZ = entityLiving.motionZ;
				}
			}

			// timing the effect out
			if (wasInAir && entityLiving.onGround) {
				if (timer == 0) {
					timer = entityLiving.ticksExisted;
				} else if (entityLiving.ticksExisted - timer > 5) {
					MinecraftForge.EVENT_BUS.unregister(this);
					bouncingEntities.remove(entityLiving);
				}
			} else {
				timer = 0;
				wasInAir = true;
			}
		}
	}

	public static class BouncyBlock {

		private final int world;
		@Nonnull
		private final BlockPos pos;
		private final long time;
		private final int expiry;

		BouncyBlock(World world, @NotNull BlockPos pos, int expiry) {
			this.world = world.provider.getDimension();
			this.pos = pos;
			this.time = world.getTotalWorldTime();
			this.expiry = expiry;
		}

		public int getWorld() {
			return world;
		}

		@NotNull
		public BlockPos getPos() {
			return pos;
		}

		public long getTime() {
			return time;
		}

		public int getExpiry() {
			return expiry;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			BouncyBlock that = (BouncyBlock) o;
			return world == that.world &&
					Objects.equals(pos, that.pos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(world, pos);
		}
	}
}