package com.teamwizardry.wizardry.api.spell.attribute;

/**
 * Created by Demoniaque.
 */
public class Attributes {

	public static final String POTENCY = "modifier_increase_strength";
	public static final String DURATION = "modifier_extend_time";
	public static final String RANGE = "modifier_extend_range";
	public static final String AREA = "modifier_increase_aoe";
	public static final String SPEED = "modifier_increase_speed";

	public static final String MANA = "modifier_mana_cost";
	public static final String BURNOUT = "modifier_burnout_value";
	
	public static final String COOLDOWN = "modifier_cooldown_time";
	public static final String CHARGEUP = "modifier_chargeup_time";
	public static final String POWER_MULTI = "power_multiplier";
	public static final String MANA_MULTI = "mana_multiplier";
	public static final String BURNOUT_MULTI = "burnout_multiplier";

	public static String getAttributeFromName(String name) {
		switch (name) {
			case "potency":
				return POTENCY;
			case "duration":
				return DURATION;
			case "range":
				return RANGE;
			case "area":
				return AREA;
			case "speed":
				return SPEED;
			case "mana":
				return MANA;
			case "burnout":
				return BURNOUT;
			case "cooldown":
				return COOLDOWN;
			case "chargeup":
				return CHARGEUP;
			case "power_multiplier":
				return POWER_MULTI;
			case "mana_multiplier":
				return MANA_MULTI;
			case "burnout_multiplier":
				return BURNOUT_MULTI;
			default:
				return null;
		}
	}
}
