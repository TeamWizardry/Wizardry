package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.forgeevents.EntityUpdateEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.events.EntityMoveEvent;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class PotionTimeSlow extends PotionBase {

	public PotionTimeSlow() {
		super("time_slow", false, 0xE8CA0D);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_IN, SoundCategory.NEUTRAL, 1f, 1);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_OUT, SoundCategory.NEUTRAL, 1f, 1);
	}

	public static float timeScale(Entity entity) {
		// TODO: 10/6/18 make this apply to more than just the potion?

		if (entity instanceof EntityLivingBase) {
			PotionEffect effect = ModPotions.TIME_SLOW.getEffect((EntityLivingBase) entity);
			if (effect != null)
				return 1f / (effect.getAmplifier() + 1.5f);
		}

		return -1f;
	}

	@SubscribeEvent
	public static void fall(LivingFallEvent event) {
		float scale = timeScale(event.getEntity());

		if (scale > 0)
			event.setDistance(event.getDistance() * scale);
	}

	@SubscribeEvent
	public static void entityPreUpdate(EntityUpdateEvent event) {
		Entity entity = event.getEntity();
		float scale = timeScale(entity);

		if (!entity.hasNoGravity() && scale > 0) {
			double gravity = entity instanceof EntityLivingBase ? -0.08 : -0.04;

			entity.motionY -= gravity * (1 - scale);
		}
	}
	
	@SubscribeEvent
	public static void entityMove(EntityMoveEvent event) {
		float scale = timeScale(event.entity);

		if (scale > 0) {
			event.x *= scale;
			event.y *= scale;
			event.z *= scale;
		}
	}
}
