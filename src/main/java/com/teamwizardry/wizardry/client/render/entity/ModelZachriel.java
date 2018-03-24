package com.teamwizardry.wizardry.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Demoniaque on 8/21/2016.
 */
@SideOnly(Side.CLIENT)
public class ModelZachriel extends ModelBase {

	public ModelRenderer capeBack;
	public ModelRenderer capeSideRight;
	public ModelRenderer capeSideLeft;
	public ModelRenderer legLeft;
	public ModelRenderer legRight;
	public ModelRenderer torso;
	public ModelRenderer shoulderRight;
	public ModelRenderer shoulderLeft;
	public ModelRenderer upperBack;
	public ModelRenderer neck;
	public ModelRenderer head;
	public ModelRenderer armRight;
	public ModelRenderer armLeft;
	public ModelRenderer footRight;
	public ModelRenderer footLeft;
	public ModelRenderer forarmLeft;
	public ModelRenderer forarmRight;

	public ModelZachriel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.legLeft = new ModelRenderer(this, 0, 0);
		this.legLeft.setRotationPoint(2.2F, 0.0F, -2.0F);
		this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 19, 2, 0.0F);
		this.setRotateAngle(legLeft, 0.22759093446006054F, 0.0F, 0.0F);
		this.shoulderLeft = new ModelRenderer(this, 0, 0);
		this.shoulderLeft.setRotationPoint(2.5F, -15.0F, 0.0F);
		this.shoulderLeft.addBox(-1.5F, 1.0F, -3.5F, 3, 14, 7, 0.0F);
		this.setRotateAngle(shoulderLeft, 0.0F, 0.0F, 0.136659280431156F);
		this.footLeft = new ModelRenderer(this, 0, 0);
		this.footLeft.setRotationPoint(2.2F, 22.8F, -0.9F);
		this.footLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 7, 0.0F);
		this.setRotateAngle(footLeft, 1.1838568316277536F, 0.0F, 0.0F);
		this.forarmRight = new ModelRenderer(this, 0, 0);
		this.forarmRight.setRotationPoint(-6.8F, -2.0F, 0.0F);
		this.forarmRight.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		this.setRotateAngle(forarmRight, -0.6829473363053812F, 0.0F, 0.9105382707654417F);
		this.forarmLeft = new ModelRenderer(this, 0, 0);
		this.forarmLeft.setRotationPoint(6.8F, -2.0F, 0.0F);
		this.forarmLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		this.setRotateAngle(forarmLeft, -0.7285004297824331F, 0.0F, -2.41309222380736F);
		this.capeBack = new ModelRenderer(this, 0, 0);
		this.capeBack.setRotationPoint(-0.9F, 5.0F, 0.0F);
		this.capeBack.addBox(-3.0F, -9.0F, 1.7F, 8, 29, 3, 0.0F);
		this.setRotateAngle(capeBack, 0.22759093446006054F, 0.0F, 0.0F);
		this.legRight = new ModelRenderer(this, 0, 0);
		this.legRight.setRotationPoint(-2.2F, 0.0F, -2.0F);
		this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 19, 2, 0.0F);
		this.setRotateAngle(legRight, 0.22759093446006054F, 0.0F, 0.0F);
		this.footRight = new ModelRenderer(this, 0, 0);
		this.footRight.setRotationPoint(-2.2F, 22.8F, -0.9F);
		this.footRight.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 7, 0.0F);
		this.setRotateAngle(footRight, 1.1838568316277536F, 0.0F, 0.0F);
		this.shoulderRight = new ModelRenderer(this, 0, 0);
		this.shoulderRight.setRotationPoint(-2.5F, -15.0F, 0.0F);
		this.shoulderRight.addBox(-1.5F, 1.0F, -3.5F, 3, 14, 7, 0.0F);
		this.setRotateAngle(shoulderRight, 0.0F, 0.0F, -0.136659280431156F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, -26.5F, 0.0F);
		this.head.addBox(-3.5F, 0.0F, -2.5F, 7, 8, 5, 0.0F);
		this.capeSideRight = new ModelRenderer(this, 0, 0);
		this.capeSideRight.setRotationPoint(4.2F, 5.0F, 0.0F);
		this.capeSideRight.addBox(-5.0F, -10.0F, -1.5F, 9, 30, 3, 0.0F);
		this.setRotateAngle(capeSideRight, 0.0F, -1.5707963267948966F, -0.1881464933649887F);
		this.neck = new ModelRenderer(this, 0, 0);
		this.neck.setRotationPoint(0.0F, -18.4F, 0.0F);
		this.neck.addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3, 0.0F);
		this.capeSideLeft = new ModelRenderer(this, 0, 0);
		this.capeSideLeft.setRotationPoint(-4.2F, 5.0F, 0.0F);
		this.capeSideLeft.addBox(-5.0F, -10.0F, -1.5F, 8, 30, 3, 0.0F);
		this.setRotateAngle(capeSideLeft, 0.0F, -1.5707963267948966F, 0.1881464933649887F);
		this.armRight = new ModelRenderer(this, 0, 0);
		this.armRight.setRotationPoint(-2.7F, -13.0F, 0.0F);
		this.armRight.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		this.setRotateAngle(armRight, 0.0F, 0.0F, 0.36425021489121656F);
		this.torso = new ModelRenderer(this, 0, 0);
		this.torso.setRotationPoint(0.0F, -6.5F, -1.0F);
		this.torso.addBox(-3.0F, -8.0F, -3.0F, 6, 15, 6, 0.0F);
		this.armLeft = new ModelRenderer(this, 0, 0);
		this.armLeft.setRotationPoint(2.7F, -13.0F, 0.0F);
		this.armLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		this.setRotateAngle(armLeft, 0.0F, 0.0F, -0.36425021489121656F);
		this.upperBack = new ModelRenderer(this, 0, 0);
		this.upperBack.setRotationPoint(0.0F, -13.7F, 3.3F);
		this.upperBack.addBox(-3.5F, 0.0F, -1.5F, 7, 15, 3, 0.0F);
		this.setRotateAngle(upperBack, -0.136659280431156F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.legLeft.render(f5);
		this.shoulderLeft.render(f5);
		this.footLeft.render(f5);
		this.forarmRight.render(f5);
		this.forarmLeft.render(f5);
		this.capeBack.render(f5);
		this.legRight.render(f5);
		this.footRight.render(f5);
		this.shoulderRight.render(f5);
		this.head.render(f5);
		this.capeSideRight.render(f5);
		this.neck.render(f5);
		this.capeSideLeft.render(f5);
		this.armRight.render(f5);
		this.torso.render(f5);
		this.armLeft.render(f5);
		this.upperBack.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
