package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectTimeSlow extends Module implements ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_time_slow";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Time Slow";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will slow down time depending on the strength. Can cause a complete freeze.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		if (targetEntity instanceof EntityLivingBase) {
			double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 2, 20, false, true);
			double duration = getModifierPower(spell, Attributes.EXTEND_TIME, 5, 64, false, true) * 10;
			if (!tax(this, spell)) return false;

			// TODO: readd mobs

			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.TIME_SLOW, (int) duration, (int) strength, true, false));
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		glitter.enableMotionCalculation();
		glitter.setAcceleration(new Vec3d(0, -0.001, 0));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position.addVector(0, 1, 0)), 3, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(30, 40));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, RandUtil.nextFloat()));

			double radius = RandUtil.nextDouble(0, 1);
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, RandUtil.nextDouble(-radius, radius), z);
			glitter.setPositionOffset(dest);

			//glitter.setPositionFunction(new InterpSlowDown(Vec3d.ZERO, new Vec3d(0, RandUtil.nextDouble(-1, 1), 0)));
			//glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, position.subtract(dest), dest.scale(2), new Vec3d(position.xCoord, radius, position.zCoord)));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectTimeSlow());
	}


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

	@SubscribeEvent
	public static void skipPlayerTick(TickEvent.PlayerTickEvent event) {
		//if (event.player.getEntityData().hasKey("skip_tick")
		//		&& event.player.getEntityData().hasKey("skip_tick_interval")
		//		&& event.player.getEntityData().hasKey("skip_tick_interval_save")) {
		//	int tickCountdown = event.player.getEntityData().getInteger("skip_tick");
		//	int tickInterval = event.player.getEntityData().getInteger("skip_tick_interval");
//
		//	event.player.addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, 5, 1, true, false));
		//	event.player.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 1, true, false));
		//	event.player.motionX = 0;
		//	event.player.motionY = 0;
		//	event.player.motionZ = 0;
		//	//event.player.rotationPitch = event.player.getEntityData().getFloat("rot_pitch");
		//	//event.player.rotationYaw = event.player.getEntityData().getFloat("rot_yaw");
		//	event.player.velocityChanged = true;
//
		//	if (tickInterval <= 0) {
		//		event.player.getEntityData().setInteger("skip_tick_interval", event.player.getEntityData().getInteger("skip_tick_interval_save"));
//
		//		if (tickCountdown <= 0) {
		//			event.player.getEntityData().removeTag("skip_tick");
		//			event.player.getEntityData().removeTag("skip_tick_interval");
		//			event.player.getEntityData().removeTag("skip_tick_interval_save");
		//			event.player.getEntityData().removeTag("strength");
		//		} else {
		//			Minecraft.getMinecraft().player.sendChatMessage(tickInterval + " - " + tickCountdown + " -  stop ticking");
		//			event.player.getEntityData().setInteger("skip_tick", --tickCountdown);
		//		}
		//	} else {
		//		event.player.getEntityData().setInteger("skip_tick_interval", --tickInterval);
		//	}
		//}
	}

	@SubscribeEvent
	public static void interact(PlayerInteractEvent event) {
		//if (event.getEntity().getEntityData().hasKey("skip_tick")
		//		&& event.getEntity().getEntityData().hasKey("skip_tick_interval")
		//		&& event.getEntity().getEntityData().hasKey("skip_tick_interval_save")) {
		//	event.setCanceled(true);
		//}
	}
}
