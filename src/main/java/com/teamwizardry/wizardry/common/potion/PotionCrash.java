package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Created by LordSaad.
 */
public class PotionCrash extends PotionMod {

	public PotionCrash() {
		super("crash", false, 0x8C680f);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public DamageSource damageSourceEarthquake(EntityLivingBase player) {
		return new EntityDamageSource("wizardry.crash", player);
	}

	public DamageSource damageSourceEarthquake() {
		return new DamageSource("wizardry.crash");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void fall(LivingFallEvent e) {
		EntityLivingBase entitySource = e.getEntityLiving();
		PotionEffect crash = entitySource.getActivePotionEffect(this);
		if (crash == null) return;

		PotionEffect jump = entitySource.getActivePotionEffect(MobEffects.JUMP_BOOST);
		float f = (jump == null) ? 0.0f : (jump.getAmplifier() + 1);
		float damage = MathHelper.clamp((e.getDistance() - 3.0f - f) * e.getDamageMultiplier() * crash.getAmplifier(), 0, 10f);

		if (damage > 0.0f) {
			entitySource.fallDistance /= crash.getAmplifier();
			List<EntityLivingBase> entities = entitySource.world.getEntitiesWithinAABB(EntityLivingBase.class, entitySource.getEntityBoundingBox().expand(crash.getAmplifier() * 2, crash.getAmplifier() * 2, crash.getAmplifier() * 2));
			entities.stream().filter(entity -> entity != entitySource && entity.onGround).forEach(entity -> {
				entity.attackEntityFrom(damageSourceEarthquake(entitySource), damage);
				entity.motionY = damage / 10.0;
				entity.velocityChanged = true;
			});

			for (BlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(entitySource.getPositionVector()).add(-3, -3, -3), new BlockPos(entitySource.getPositionVector()).add(3, -1, 3))) {
				IBlockState state = entitySource.world.getBlockState(pos);
				if (state.isFullCube()) {
					entitySource.world.playEvent(2001, pos, state.getBlock().getMetaFromState(state));
				}
			}
		}
	}
}
