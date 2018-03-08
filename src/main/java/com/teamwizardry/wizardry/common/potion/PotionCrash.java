package com.teamwizardry.wizardry.common.potion;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionCrash extends PotionBase {

	public PotionCrash() {
		super("crash", false, 0x8C680f);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	public DamageSource damageSourceEarthquake(EntityLivingBase player) {
		return new EntityDamageSource("wizardry.crash", player);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void fall(LivingFallEvent e) {
		EntityLivingBase entitySource = e.getEntityLiving();
		PotionEffect crash = entitySource.getActivePotionEffect(this);
		if (crash == null) return;

		PotionEffect jump = entitySource.getActivePotionEffect(MobEffects.JUMP_BOOST);
		float f = (jump == null) ? 0.0f : (jump.getAmplifier() + 1);
		float damage = MathHelper.clamp((e.getDistance() - 3.0f - f) * e.getDamageMultiplier() * 2, 0, 10f);

		float range = damage / 10f + Math.max(crash.getAmplifier(), 5);

		if (damage > 0.0f) {
			e.setDamageMultiplier(e.getDamageMultiplier() / (crash.getAmplifier() + 2));
			List<EntityLivingBase> entities = entitySource.world.getEntitiesWithinAABB(EntityLivingBase.class, entitySource.getEntityBoundingBox().grow(range * 2));
			entities.stream().filter(entity -> entity != entitySource && entity.onGround).forEach(entity -> {
				entity.attackEntityFrom(damageSourceEarthquake(entitySource), range);
				entity.motionY = range / 10.0;
				entity.velocityChanged = true;
			});

			if (!entitySource.world.isRemote) for (BlockPos pos : BlockPos.getAllInBoxMutable(
					new BlockPos(entitySource.getPositionVector())
							.add(-range,
									-2,
									-range),
					new BlockPos(entitySource.getPositionVector())
							.add(range,
									0,
									range))) {
				IBlockState state = entitySource.world.getBlockState(pos);
				if (state.isFullCube()) entitySource.world.playEvent(2001, pos.toImmutable(), Block.getStateId(state));
			}
		}
	}
}
