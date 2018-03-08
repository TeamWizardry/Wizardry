package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class EntityMoveEvent extends Event {

	public Entity entity;
	public MoverType type;
	public double x;
	public double y;
	public double z;
	public boolean override = false;

	public EntityMoveEvent(Entity entity, MoverType type, double x, double y, double z) {
		this.entity = entity;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
