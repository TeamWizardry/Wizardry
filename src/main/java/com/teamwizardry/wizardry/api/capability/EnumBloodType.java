package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
public enum EnumBloodType {

	NONE("none", Color.WHITE), DECAY("decay", Color.CYAN), DESTRUCTION("destruction", Color.RED), TRICKERY("trickery", Color.MAGENTA);

	public Color color;
	public String id;

	EnumBloodType(String id, Color color) {
		this.id = id;
		this.color = color;
	}

	public static ResourceLocation getResourceLocation(EnumBloodType type) {
		return new ResourceLocation(Wizardry.MODID, "textures/model/" + type.id + ".png");
	}

	public static EnumBloodType getType(String id) {
		switch (id) {
			case "decay":
				return DECAY;
			case "destruction":
				return DESTRUCTION;
			case "trickery":
				return TRICKERY;
			default:
				return NONE;
		}
	}
}
