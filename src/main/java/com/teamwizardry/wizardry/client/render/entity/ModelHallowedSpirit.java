package com.teamwizardry.wizardry.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 8/21/2016.
 */
public class ModelHallowedSpirit extends ModelBase {

	public ModelRenderer arm_right;
	public ModelRenderer chest;
	public ModelRenderer arm_left;

	public ModelHallowedSpirit() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.arm_right = new ModelRenderer(this, 40, 16);
		this.arm_right.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.arm_right.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.setRotateAngle(arm_right, 0.0F, 0.0F, 0.10000736613927509F);
		this.chest = new ModelRenderer(this, 16, 16);
		this.chest.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.chest.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.arm_left = new ModelRenderer(this, 32, 48);
		this.arm_left.setRotationPoint(5.0F, 2.0F, -0.0F);
		this.arm_left.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
		this.setRotateAngle(arm_left, 0.0F, 0.0F, -0.10000736613927509F);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		float speed = (float) ((new Vec3d(entityIn.motionX, 0, entityIn.motionZ)).lengthVector() * 3.0f);

		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
		this.chest.render(scale);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
		GlStateManager.rotate(speed * 60f * (float) Math.sin(Math.toRadians(ageInTicks % 360) * 24F), 1, 0, 0);
		this.arm_right.render(scale);

		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
		this.arm_left.render(scale);
		GlStateManager.disableBlend();
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
