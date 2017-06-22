package com.teamwizardry.wizardry.api.events;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class EntityRenderToPlayerEvent extends Event {

	public RenderLivingBase renderLivingBase;
	public EntityLivingBase entity;
	public float limbSwing;
	public float limbSwingAmount;
	public float ageInTicks;
	public float netHeadYaw;
	public float headPitch;
	public float scaleFactor;
	public boolean override = false;

	public EntityRenderToPlayerEvent(RenderLivingBase renderLivingBase, EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		this.renderLivingBase = renderLivingBase;
		this.entity = entity;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.ageInTicks = ageInTicks;
		this.netHeadYaw = netHeadYaw;
		this.headPitch = headPitch;
		this.scaleFactor = scaleFactor;
	}
}
