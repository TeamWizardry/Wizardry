package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.client.util.ClientUtilMethods;
import com.teamwizardry.wizardry.api.capability.EnumBloodType;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.client.fx.Shaders;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;

public class BloodRenderLayer implements LayerRenderer<AbstractClientPlayer> {

	private final RenderPlayer render;

	public BloodRenderLayer(RenderPlayer renderIn) {
		render = renderIn;
	}

	@Override
	public void doRenderLayer(@Nonnull AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		EnumBloodType type = WizardryCapabilityProvider.get(entity).getBloodType();
		if (type != null) {
			render.bindTexture(EnumBloodType.getResourceLocation(type));
			ClientUtilMethods.glColor(type.color);
			setModelVisibilities(entity);
			GlStateManager.enableBlendProfile(Profile.PLAYER_SKIN);

			GlStateManager.disableLighting();
			ShaderHelper.INSTANCE.useShader(Shaders.rawColor);

			render.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.enableLighting();
			ShaderHelper.INSTANCE.releaseShader();

			GlStateManager.disableBlendProfile(Profile.PLAYER_SKIN);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
		}
	}

	private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
		ModelPlayer modelplayer = render.getMainModel();

		if (clientPlayer.isSpectator()) {
			modelplayer.setInvisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		} else {
			ItemStack stackMain = clientPlayer.getHeldItemMainhand();
			ItemStack stackOff = clientPlayer.getHeldItemOffhand();
			modelplayer.setInvisible(true);
			modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
			modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
			modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
			modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
			modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
			modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
			modelplayer.isSneak = clientPlayer.isSneaking();
			ArmPose poseMain = ArmPose.EMPTY;

			if (stackMain != null) {
				poseMain = ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0) {
					EnumAction enumaction = stackMain.getItemUseAction();

					if (enumaction == EnumAction.BLOCK) {
						poseMain = ArmPose.BLOCK;
					} else if (enumaction == EnumAction.BOW) {
						poseMain = ArmPose.BOW_AND_ARROW;
					}
				}
			}

			ArmPose poseOff = ArmPose.EMPTY;
			if (stackOff != null) {
				poseOff = ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0) {
					EnumAction enumaction1 = stackOff.getItemUseAction();

					if (enumaction1 == EnumAction.BLOCK) {
						poseOff = ArmPose.BLOCK;
					}
				}
			}

			if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
				modelplayer.rightArmPose = poseMain;
				modelplayer.leftArmPose = poseOff;
			} else {
				modelplayer.rightArmPose = poseOff;
				modelplayer.leftArmPose = poseMain;
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
