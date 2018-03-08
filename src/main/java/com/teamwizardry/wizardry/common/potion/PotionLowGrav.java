package com.teamwizardry.wizardry.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionLowGrav extends PotionBase {

	public PotionLowGrav() {
		super("low_gravity", false, 0x469CD6);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
		if (!entity.isPotionActive(this)) return;

		double dist = -0.05;
		double shift = 0.175;

		World world = entity.world;
		if (world.containsAnyLiquid(entity.getEntityBoundingBox().offset(0.0, dist + shift, 0.0)) && entity.motionY < 0.5) {
			entity.motionY += 0.15;
			entity.fallDistance = 0f;
		} else if (world.containsAnyLiquid(entity.getEntityBoundingBox().offset(0.0, dist, 0.0)) && entity.motionY < 0.0) {
			entity.motionY = 0.0;
			entity.fallDistance = 0f;
			entity.onGround = true;
		} else if (world.containsAnyLiquid(entity.getEntityBoundingBox().offset(0.0, dist + entity.motionY - 0.05, 0.0)) && entity.motionY < 0.0) {
			entity.setPosition(entity.posX, Math.floor(entity.posY), entity.posZ);
			entity.motionY /= 5;
			entity.fallDistance = 0f;
			entity.onGround = true;
		}
	}

	@SubscribeEvent
	public void jump(LivingEvent.LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isPotionActive(this)) return;

		PotionEffect effect = entity.getActivePotionEffect(this);
		if (effect == null) return;

		entity.motionY = effect.getAmplifier() / 3.0;
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
