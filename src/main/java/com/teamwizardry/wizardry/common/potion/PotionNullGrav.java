package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionNullGrav extends PotionMod {

	public PotionNullGrav() {
		super("nullify_gravity", false, 0xFFFFFF);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		entityLivingBaseIn.setNoGravity(true);
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		entityLivingBaseIn.setNoGravity(false);
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
