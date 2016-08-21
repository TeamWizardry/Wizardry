package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.wizardry.client.render.entity.ModelHallowedSpirit;
import net.minecraft.client.model.ModelBase;

import java.util.HashMap;

/**
 * Created by Saad on 8/21/2016.
 */
public class ModelHolder {

	public static HashMap<String, ModelBase> entityModels = new HashMap<>();

	public static void init() {
		entityModels.put("hallowed_spirit", new ModelHallowedSpirit());
	}
}
