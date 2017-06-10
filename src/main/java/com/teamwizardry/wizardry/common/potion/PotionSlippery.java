package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionSlippery extends PotionMod {

	public PotionSlippery() {
		super("slippery", false, 0xA9F3A9);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@SubscribeEvent
	public void entityMove(LivingEvent.LivingUpdateEvent event) {
	}
}
