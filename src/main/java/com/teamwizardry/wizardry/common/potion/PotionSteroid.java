package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.wizardry.api.capability.CapManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionSteroid extends PotionMod {

	public PotionSteroid() {
		super("steroid", false, 0xFFFFFF);
		setBeneficial();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int p_76394_2_) {
		if (!hasEffect(entityLivingBaseIn)) return;

		CapManager manager = new CapManager(entityLivingBaseIn);

		manager.setMana(manager.getMaxMana());
		manager.setBurnout(0);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		CapManager manager = new CapManager(entityLivingBaseIn);
		manager.setMana(0);
		manager.setBurnout(manager.getMaxBurnout());
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 4, true, false));
		if (!(entityLivingBaseIn instanceof EntityPlayer) || !((EntityPlayer) entityLivingBaseIn).capabilities.isCreativeMode)
			entityLivingBaseIn.setHealth(0.5f);

		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
