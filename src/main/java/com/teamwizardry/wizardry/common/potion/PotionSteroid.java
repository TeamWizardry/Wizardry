package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.common.base.PotionMod;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

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
	public void performEffect(@NotNull EntityLivingBase entityLivingBaseIn, int p_76394_2_) {
		if (!hasEffect(entityLivingBaseIn)) return;

		if (entityLivingBaseIn instanceof EntityPlayer) {
			IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) entityLivingBaseIn);
			cap.setMana(cap.getMaxMana(), (EntityPlayer) entityLivingBaseIn);
			cap.setBurnout(0, (EntityPlayer) entityLivingBaseIn);
		}
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @NotNull AbstractAttributeMap attributeMapIn, int amplifier) {
		if (entityLivingBaseIn instanceof EntityPlayer) {
			IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) entityLivingBaseIn);
			cap.setMana(0, (EntityPlayer) entityLivingBaseIn);
			cap.setBurnout(cap.getMaxBurnout(), (EntityPlayer) entityLivingBaseIn);
		}
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 4, true, false));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 4, true, false));
		entityLivingBaseIn.setHealth(0.5f);

		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
