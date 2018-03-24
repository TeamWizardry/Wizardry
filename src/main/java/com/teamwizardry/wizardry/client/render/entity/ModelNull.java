package com.teamwizardry.wizardry.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class ModelNull extends ModelBase {

	public ModelNull() {
		textureWidth = 64;
		textureHeight = 64;
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
	}
}
