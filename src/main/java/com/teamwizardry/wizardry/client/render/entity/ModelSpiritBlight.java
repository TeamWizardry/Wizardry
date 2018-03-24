package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.common.entity.EntitySpiritBlight.DATA_SHIFT_SEED;

/**
 * Created by Demoniaque on 8/21/2016.
 */
@SideOnly(Side.CLIENT)
public class ModelSpiritBlight extends ModelBiped {

	public ModelSpiritBlight() {
		textureWidth = 64;
		textureHeight = 64;
		bipedHead.showModel = false;
		bipedHeadwear.showModel = false;
		bipedLeftLeg.showModel = false;
		bipedRightLeg.showModel = false;
	}

	@Override
	public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);

		RandUtilSeed seed = new RandUtilSeed(entityIn.getDataManager().get(DATA_SHIFT_SEED));
		GlStateManager.translate(seed.nextDouble(-2, 2), seed.nextDouble(-2, 2), seed.nextDouble(-2, 2));

		double multiplier = 0.05;
		GlStateManager.translate(RandUtil.nextFloat() * multiplier, RandUtil.nextFloat() * multiplier, RandUtil.nextFloat() * multiplier);

		GlStateManager.rotate(entityIn.rotationYaw, 0, 1, 0);

		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		GlStateManager.disableBlend();
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
