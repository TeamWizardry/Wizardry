package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class EntityRenderShadowAndFireEvent extends Event {

	public Entity entity;
	public boolean override = false;

	public EntityRenderShadowAndFireEvent(Entity entity) {
		this.entity = entity;
	}
}
