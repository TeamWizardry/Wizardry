package com.teamwizardry.wizardry.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Based off the vanilla horse
 */
@SideOnly(Side.CLIENT)
public class ModelUnicorn extends ModelBase {
    public ModelRenderer neck;
    public ModelRenderer head;
    public ModelRenderer head_0;
    public ModelRenderer head_1;
    public ModelRenderer leftEar;
    public ModelRenderer rightEar;
    public ModelRenderer horn;
    public ModelRenderer mane;
    public ModelRenderer frontLeftHip;
    public ModelRenderer frontLeftThigh;
    public ModelRenderer frontLeftHoof;
    public ModelRenderer frontRightHip;
    public ModelRenderer frontRightThigh;
    public ModelRenderer frontRightHoof;
    public ModelRenderer body;
    public ModelRenderer backLeftHip;
    public ModelRenderer backLeftThigh;
    public ModelRenderer backLeftHoof;
    public ModelRenderer backRightHip;
    public ModelRenderer backRightThigh;
    public ModelRenderer backRightHoof;
    public ModelRenderer tail;
    public ModelRenderer tail2;
    public ModelRenderer tail3;

    public ModelUnicorn() {
	this.textureWidth = 128;
	this.textureHeight = 128;

	this.neck = new ModelRenderer(this, 0, 12);
	this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
	this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
	this.setRotationAngles(this.neck, 0.5235987755982988F, 0.0F, 0.0F);
	this.head = new ModelRenderer(this, 0, 0);
	this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
	this.neck.addChild(this.head);
	this.head_0 = new ModelRenderer(this, 24, 18);
	this.head_0.setRotationPoint(0.0F, 4.0F, -10.0F);
	this.head_0.addBox(-2.0F, -14.0F, 2.5F, 4, 3, 6);
	this.head.addChild(this.head_0);
	this.head_1 = new ModelRenderer(this, 24, 27);
	this.head_1.setRotationPoint(0.0F, -7.0F, -1.5F);
	this.head_1.addBox(-2.0F, 0.0F, -5.5F, 4, 2, 5);
	this.head.addChild(this.head_1);
	this.leftEar = new ModelRenderer(this, 0, 0);
	this.leftEar.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.leftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
	this.head.addChild(this.leftEar);
	this.rightEar = new ModelRenderer(this, 0, 0);
	this.rightEar.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.rightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
	this.head.addChild(this.rightEar);
	this.horn = new ModelRenderer(this, 124, 0);
	this.horn.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.horn.addBox(-0.5F, -15.0F, 2.0F, 1, 5, 1);
	this.head.addChild(this.horn);
	this.mane = new ModelRenderer(this, 58, 0);
	this.mane.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
	this.neck.addChild(this.mane);
	this.frontLeftHip = new ModelRenderer(this, 44, 29);
	this.frontLeftHip.setRotationPoint(4.0F, 9.0F, -8.0F);
	this.frontLeftHip.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
	this.frontLeftThigh = new ModelRenderer(this, 44, 41);
	this.frontLeftThigh.setRotationPoint(0.0F, 7.0F, 0.0F);
	this.frontLeftThigh.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
	this.frontLeftHip.addChild(this.frontLeftThigh);
	this.frontLeftHoof = new ModelRenderer(this, 44, 51);
	this.frontLeftHoof.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
	this.frontLeftThigh.addChild(this.frontLeftHoof);
	this.frontRightHip = new ModelRenderer(this, 60, 29);
	this.frontRightHip.setRotationPoint(-4.0F, 9.0F, -8.0F);
	this.frontRightHip.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
	this.frontRightThigh = new ModelRenderer(this, 60, 41);
	this.frontRightThigh.setRotationPoint(0.0F, 7.0F, 0.0F);
	this.frontRightThigh.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
	this.frontRightHip.addChild(this.frontRightThigh);
	this.frontRightHoof = new ModelRenderer(this, 60, 51);
	this.frontRightHoof.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
	this.frontRightThigh.addChild(this.frontRightHoof);
	this.body = new ModelRenderer(this, 0, 34);
	this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
	this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
	this.backLeftHip = new ModelRenderer(this, 78, 29);
	this.backLeftHip.setRotationPoint(4.0F, 9.0F, 11.0F);
	this.backLeftHip.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
	this.backLeftThigh = new ModelRenderer(this, 78, 43);
	this.backLeftThigh.setRotationPoint(0.0F, 7.0F, 0.0F);
	this.backLeftThigh.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
	this.backLeftHip.addChild(this.backLeftThigh);
	this.backLeftHoof = new ModelRenderer(this, 78, 51);
	this.backLeftHoof.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
	this.backLeftThigh.addChild(this.backLeftHoof);
	this.backRightHip = new ModelRenderer(this, 96, 29);
	this.backRightHip.setRotationPoint(-4.0F, 9.0F, 11.0F);
	this.backRightHip.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
	this.backRightThigh = new ModelRenderer(this, 96, 43);
	this.backRightThigh.setRotationPoint(0.0F, 7.0F, 0.0F);
	this.backRightThigh.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
	this.backRightHip.addChild(this.backRightThigh);
	this.backRightHoof = new ModelRenderer(this, 96, 51);
	this.backRightHoof.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
	this.backRightThigh.addChild(this.backRightHoof);
	this.tail = new ModelRenderer(this, 44, 0);
	this.tail.setRotationPoint(0.0F, 3.0F, 14.0F);
	this.tail.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
	this.setRotationAngles(this.tail, -1.0471975511965976F, 0.0F, 0.0F);
	this.tail2 = new ModelRenderer(this, 38, 7);
	this.tail2.setRotationPoint(-0.0F, 0.0F, 2.5F);
	this.tail2.addBox(-1.5F, -2.0F, 0.0F, 3, 4, 7);
	this.setRotationAngles(this.tail2, -0.4363323129985824F, 0.0F, 0.0F);
	this.tail.addChild(this.tail2);
	this.tail3 = new ModelRenderer(this, 24, 3);
	this.tail3.setRotationPoint(0.0F, 0.0F, 0.0F);
	this.tail3.addBox(-1.5F, -2.0F, 7.0F, 3, 4, 7);
	this.setRotationAngles(this.tail3, -0.02792526844802819F, 0.0F, 0.0F);
	this.tail2.addChild(this.tail3);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale)
    {
	this.neck.render(scale);
	this.frontLeftHip.render(scale);
	this.frontRightHip.render(scale);
	this.body.render(scale);
	this.backLeftHip.render(scale);
	this.backRightHip.render(scale);
	this.tail.render(scale);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_,
	    float partialTickTime)
    {
	backLeftHip.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6F) * 1.4F * p_78086_3_;
	backRightHip.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6F) + ((float)Math.PI / 18.0F) * 1.4F * p_78086_3_;
	frontLeftHip.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6F) + ((float)Math.PI / 18.0F) * 1.4F * p_78086_3_;
	frontRightHip.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6F) * 1.4F * p_78086_3_;
    }

    public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
	modelRenderer.rotateAngleX = x;
	modelRenderer.rotateAngleY = y;
	modelRenderer.rotateAngleZ = z;
    }
}
