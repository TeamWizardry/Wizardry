package com.teamwizardry.wizardry.api.spell.attribute;

/**
 * Created by LordSaad.
 */
public class Attributes {

	public static final String POTENCY = "modifier_increase_strength";
	public static final String DURATION = "modifier_extend_time";
	public static final String RANGE = "modifier_extend_range";
	public static final String AREA = "modifier_increase_aoe";
	public static final String SPEED = "modifier_increase_speed";
	
	public static final String MANA = "modifier_mana_cost";
	public static final String BURNOUT = "modifier_burnout_value";
	
	public static String getAttributeFromName(String name)
	{
		switch(name)
		{
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
			default:
				return null;
		}
	}
}
