package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class EntityMoveWithHeadingEvent extends Event {

	public EntityLivingBase entity;
	public float strafe;
	public float forward;
	public float vertical;
	public boolean override = false;

	public EntityMoveWithHeadingEvent(EntityLivingBase entity, float strafe, float vertical,float forward) {
		this.entity = entity;
		this.strafe = strafe;
		this.vertical = vertical;
		this.forward = forward;
	}
}
