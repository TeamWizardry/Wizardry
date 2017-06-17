package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by LordSaad.
 */
public class PotionLowGrav extends PotionMod {

	public PotionLowGrav() {
		super("low_gravity", false, 0xFFFFFF);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void entityTick(LivingEvent.LivingUpdateEvent event) {

	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (!player.isPotionActive(this)) return;

		double dist = -0.05;
		double shift = 0.175;

		World world = event.player.world;
		if (world.containsAnyLiquid(player.getEntityBoundingBox().offset(0.0, dist + shift, 0.0)) && player.motionY < 0.5) {
			player.motionY += 0.15;
			player.fallDistance = 0f;
		} else if (world.containsAnyLiquid(player.getEntityBoundingBox().offset(0.0, dist, 0.0)) && player.motionY < 0.0) {
			player.motionY = 0.0;
			player.fallDistance = 0f;
			player.onGround = true;
		} else if (world.containsAnyLiquid(player.getEntityBoundingBox().offset(0.0, dist + player.motionY - 0.05, 0.0)) && player.motionY < 0.0) {
			player.setPosition(player.posX, Math.floor(player.posY), player.posZ);
			player.motionY /= 5;
			player.fallDistance = 0f;
			player.onGround = true;
		}
	}

	@SubscribeEvent
	public void jump(LivingEvent.LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isPotionActive(this)) return;

		PotionEffect effect = entity.getActivePotionEffect(this);
		if (effect == null) return;

		entity.motionY *= effect.getAmplifier() + 0.5;
	}

	@SubscribeEvent
	public void fall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isPotionActive(this)) return;

		PotionEffect effect = entity.getActivePotionEffect(this);
		if (effect == null) return;

		event.setDistance((float) (event.getDistance() / (effect.getAmplifier() + 0.5)));
	}
}
