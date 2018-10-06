package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public class SlipperinessEvent extends Event {

	private final Entity entity;

	private float slipperiness;

	public SlipperinessEvent(Entity entity, float slipperiness) {
		this.entity = entity;
		this.slipperiness = slipperiness;
	}

	@Nullable
	public Entity getEntity() {
		return entity;
	}

	public float getSlipperiness() {
		return slipperiness;
	}

	public void setSlipperiness(float slipperiness) {
		this.slipperiness = slipperiness;
	}
}
