package com.teamwizardry.wizardry.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.IdentityHashMap;

/**
 * Code "borrowed" from Tinker's Construct SlimeBounceHandler
 * https://github.com/SlimeKnights/TinkersConstruct/blob/23034cb63e98bba06faf1cdc4074009daf93be1f/src/main/java/slimeknights/tconstruct/library/SlimeBounceHandler.java
 * <p>
 * I don't feel like re-inventing the wheel. Shut up.
 */
public class SlimeBounceHandler {

	private static final IdentityHashMap<Entity, SlimeBounceHandler> bouncingEntities = new IdentityHashMap<>();

	public final EntityLivingBase entityLiving;
	private int timer;
	private boolean wasInAir;
	private double bounce;
	private int bounceTick;

	private double lastMovX;
	private double lastMovZ;

	public SlimeBounceHandler(EntityLivingBase entityLiving, double bounce) {
		this.entityLiving = entityLiving;
		timer = 0;
		wasInAir = false;
		this.bounce = bounce;

		if (bounce != 0) {
			bounceTick = entityLiving.ticksExisted;
		} else {
			bounceTick = 0;
		}

		bouncingEntities.put(entityLiving, this);
	}

	public static void addBounceHandler(EntityLivingBase entity) {
		addBounceHandler(entity, 0d);
	}

	public static void addBounceHandler(EntityLivingBase entity, double bounce) {
		if (!(entity instanceof EntityPlayer) || entity instanceof FakePlayer) {
			return;
		}
		SlimeBounceHandler handler = bouncingEntities.get(entity);
		if (handler == null) {
			MinecraftForge.EVENT_BUS.register(new SlimeBounceHandler(entity, bounce));
		} else if (bounce != 0) {
			handler.bounce = bounce;
			handler.bounceTick = entity.ticksExisted;
		}
	}

	@SubscribeEvent
	public void playerTickPost(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.player == entityLiving && !event.player.isElytraFlying()) {
			if (event.player.ticksExisted == bounceTick) {
				event.player.motionY = bounce;
				bounceTick = 0;
			}

			// preserve motion
			if (!entityLiving.onGround && entityLiving.ticksExisted != bounceTick) {
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
}