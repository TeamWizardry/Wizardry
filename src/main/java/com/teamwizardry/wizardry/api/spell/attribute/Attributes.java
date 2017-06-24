package com.teamwizardry.wizardry.api.spell.attribute;

import com.google.common.collect.HashBiMap;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtendRange;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtendTime;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;

/**
 * Created by LordSaad.
 */
public class Attributes {

	public static HashBiMap<ModuleModifier, String> map = HashBiMap.create();

	public static final String POTENCY = "modifier_increase_strength";
	public static final String DURATION = "modifier_extend_time";
	public static final String RANGE = "modifier_extend_range";
	public static final String AREA = "modifier_increase_aoe";
	public static final String SPEED = "modifier_increase_speed";
	
	public static final String MANA = "modifier_mana_cost";
	public static final String BURNOUT = "modifier_burnout_value";

	static {
		map.put(new ModuleModifierIncreasePotency(), POTENCY);
		map.put(new ModuleModifierExtendTime(), DURATION);
		map.put(new ModuleModifierExtendRange(), RANGE);
		map.put(new ModuleModifierIncreaseAOE(), AREA);
	}
}
