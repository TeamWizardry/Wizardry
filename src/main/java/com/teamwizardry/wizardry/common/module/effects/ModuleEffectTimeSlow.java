package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_time_slow")
public class ModuleEffectTimeSlow implements IModuleEffect, ILingeringModule {

	@SubscribeEvent
	public static void skipTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity().getEntityData().hasKey("skip_tick")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval_save")) {
			int tickCountdown = event.getEntity().getEntityData().getInteger("skip_tick");
			int tickInterval = event.getEntity().getEntityData().getInteger("skip_tick_interval");

			if (tickInterval <= 0) {
				event.getEntity().getEntityData().setInteger("skip_tick_interval", event.getEntity().getEntityData().getInteger("skip_tick_interval_save"));

				if (tickCountdown <= 0) {
					event.getEntity().getEntityData().removeTag("skip_tick");
					event.getEntity().getEntityData().removeTag("skip_tick_interval");
					event.getEntity().getEntityData().removeTag("skip_tick_interval_save");
				} else {
					event.getEntity().getEntityData().setInteger("skip_tick", --tickCountdown);
					event.setCanceled(true);
				}
			} else {
				event.getEntity().getEntityData().setInteger("skip_tick_interval", --tickInterval);
			}
		}
	}

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_extend_time"};
	}

	@ModuleOverride("shape_zone_run")
	public boolean onRunZone(World world, SpellData data, SpellRing ring, @ContextRing SpellRing childRing) {
		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);
		double range = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		Vec3d targetPos = data.getTarget(world);

		if (targetPos == null) return false;

		Vec3d min = targetPos.subtract(aoe, range, aoe);
		Vec3d max = targetPos.add(aoe, range, aoe);

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(min, max));
		for (Entity entity : entities) {
			if (entity instanceof EntityLivingBase) {
				if (!((EntityLivingBase) entity).isPotionActive(ModPotions.TIME_SLOW) && entity.getDistanceSq(targetPos.x, targetPos.y, targetPos.z) <= aoe * aoe) {
					data.processEntity(entity, false);
					runOnStart(world, data, childRing);
				}
			}
		}
		return true;
	}


	@Override
	@SuppressWarnings("unused")
	public boolean runOnStart(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		BlockPos targetPos = spell.getTargetPos();
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);

		if (targetEntity instanceof EntityLivingBase && !((EntityLivingBase) targetEntity).isPotionActive(ModPotions.TIME_SLOW)) {
			double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
			double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
			if (!spellRing.taxCaster(world, spell, true)) return false;

			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.TIME_SLOW, (int) duration, (int) potency, false, false));
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, (int) duration, (int) potency, false, false));
		}
		return true;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity victim = spell.getVictim(world);

		if (victim == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		glitter.enableMotionCalculation();
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextBoolean() ? -0.0001 : 0.0001, 0));
		glitter.disableRandom();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(victim.getPositionVector().add(0, victim.height / 2, 0)), 5, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(40, 80));
			glitter.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1f), 0f));
			glitter.setAlphaFunction(new InterpFloatInOut(0.5f, 0.5f));

			double radius = RandUtil.nextDouble(0, 2);
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, RandUtil.nextDouble(-radius, radius), z);
			glitter.setPositionOffset(dest);
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.001, 0.001), 0, RandUtil.nextDouble(-0.001, 0.001)));

			//glitter.setPositionFunction(new InterpSlowDown(Vec3d.ZERO, new Vec3d(0, RandUtil.nextDouble(-1, 1), 0)));
			//glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, position.subtract(dest), dest.scale(2), new Vec3d(position.x, radius, position.z)));
		});
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
		return (int) duration;
	}
}
