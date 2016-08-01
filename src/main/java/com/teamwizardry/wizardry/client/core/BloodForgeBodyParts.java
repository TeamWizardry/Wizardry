package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.wizardry.api.WizardryClientMethodHandler;
import com.teamwizardry.wizardry.api.save.WizardryDataHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

/**
 * Created by LordSaad44
 */
public class BloodForgeBodyParts extends RenderPlayer {

	public BloodForgeBodyParts(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		if (!WizardryDataHandler.getHasBlood(clientPlayer.getUniqueID())) {
			float f = 1.0F;
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			float f1 = 0.0625F;
			ModelPlayer modelplayer = this.getMainModel();
			WizardryClientMethodHandler.setModelVisibilities(this, clientPlayer);
			GlStateManager.enableBlend();
			modelplayer.swingProgress = 0.0F;
			modelplayer.isSneak = false;
			modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
			modelplayer.bipedRightArm.rotateAngleX = 0.0F;
			modelplayer.bipedRightArm.render(0.0625F);
			modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
			modelplayer.bipedRightArmwear.render(0.0625F);
			GlStateManager.disableBlend();
		}
	}

	@Override
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		if (!WizardryDataHandler.getHasBlood(clientPlayer.getUniqueID())) {
			float f = 1.0F;
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			float f1 = 0.0625F;
			ModelPlayer modelplayer = this.getMainModel();
			WizardryClientMethodHandler.setModelVisibilities(this, clientPlayer);
			GlStateManager.enableBlend();
			modelplayer.isSneak = false;
			modelplayer.swingProgress = 0.0F;
			modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
			modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
			modelplayer.bipedLeftArm.render(0.0625F);
			modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
			modelplayer.bipedLeftArmwear.render(0.0625F);
			GlStateManager.disableBlend();
		}
	}
}
