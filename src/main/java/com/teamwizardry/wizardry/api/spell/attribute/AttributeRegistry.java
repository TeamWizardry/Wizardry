package com.teamwizardry.wizardry.api.spell.attribute;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
public class AttributeRegistry {

	public static final Attribute POTENCY = new Attribute("modifier_increase_strength", "potency");
	public static final Attribute DURATION = new Attribute("modifier_extend_time", "duration");
	public static final Attribute RANGE = new Attribute("modifier_extend_range", "range");
	public static final Attribute AREA = new Attribute("modifier_increase_aoe", "area");
	public static final Attribute SPEED = new Attribute("modifier_increase_speed", "speed");

	public static final Attribute MANA = new Attribute("modifier_mana_cost", "mana");
	public static final Attribute BURNOUT = new Attribute("modifier_burnout_value", "burnout");

	private static final Set<Attribute> attributes = new HashSet<>();

	static {
		addAttribute(POTENCY);
		addAttribute(DURATION);
		addAttribute(RANGE);
		addAttribute(AREA);
		addAttribute(SPEED);
		addAttribute(MANA);
		addAttribute(BURNOUT);
	}

	public static void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	@Nullable
	public static Attribute getAttributeFromName(String name) {
		for (Attribute attribute : attributes) {
			if (attribute.getNbtName().equals(name) || attribute.getShortName().equals(name)) return attribute;
		}
		return null;
	}

	public static class Attribute {

		private final String nbtName;
		private final String shortName;

		public Attribute(String nbtName, String shortName) {
			this.nbtName = nbtName;
			this.shortName = shortName;
		}

		public String getShortName() {
			return shortName;
		}

		public String getNbtName() {
			return nbtName;
		}
	}
}
