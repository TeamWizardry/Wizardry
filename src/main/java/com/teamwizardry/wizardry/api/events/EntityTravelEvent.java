package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
@Cancelable
public class EntityTravelEvent extends Event {

	public final EntityLivingBase entity;
	public float strafe;
	public float forward;
	public float vertical;

	public EntityTravelEvent(EntityLivingBase entity, float strafe, float vertical, float forward) {
		this.entity = entity;
		this.strafe = strafe;
		this.vertical = vertical;
		this.forward = forward;
	}
}
