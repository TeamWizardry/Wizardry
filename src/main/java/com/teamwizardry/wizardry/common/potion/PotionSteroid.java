package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.common.base.PotionMod;
import com.teamwizardry.wizardry.api.WizardManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
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

		WizardManager.setMana(WizardManager.getMaxMana(entityLivingBaseIn), entityLivingBaseIn);
		WizardManager.setBurnout(0, entityLivingBaseIn);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		WizardManager.setMana(0, entityLivingBaseIn);
		WizardManager.setBurnout(WizardManager.getMaxBurnout(entityLivingBaseIn), entityLivingBaseIn);
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 4, true, false));
		entityLivingBaseIn.setHealth(0.5f);

		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
