package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.common.base.PotionMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionPush extends PotionMod {

	public PotionPush() {
		super("push", false, 0xA9F3A9);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		Vec3d dist = entityLivingBaseIn.getLook(0).scale(amplifier);
		entityLivingBaseIn.motionX += dist.xCoord;
		entityLivingBaseIn.motionY += dist.yCoord;
		entityLivingBaseIn.motionZ += dist.zCoord;
		entityLivingBaseIn.velocityChanged = true;
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}
}
