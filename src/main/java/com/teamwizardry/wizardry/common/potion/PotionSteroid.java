package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public class PotionSteroid extends PotionBase {

	public PotionSteroid() {
		super("steroid", false, 0xDD5B23);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int p_76394_2_) {
		if (!hasEffect(entityLivingBaseIn)) return;
		ManaManager.forObject(entityLivingBaseIn)
				.setMana(ManaManager.getMaxMana(entityLivingBaseIn))
				.setBurnout(0)
				.close();
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		ManaManager.forObject(entityLivingBaseIn)
				.setMana(0)
				.setBurnout(ManaManager.getMaxBurnout(entityLivingBaseIn))
				.close();
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 3, true, true));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 3, true, true));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 3, true, true));
		entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 3, true, true));
		if (!(entityLivingBaseIn instanceof EntityPlayer) || !((EntityPlayer) entityLivingBaseIn).capabilities.isCreativeMode)
			entityLivingBaseIn.setHealth(0.5f);

		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
