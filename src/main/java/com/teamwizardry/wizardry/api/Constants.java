package com.teamwizardry.wizardry.api;

/**
 * Created by Demoniaque on 6/14/2016.
 */
public class Constants {

	public static class Module {

		public static final String SHAPE = "Shape";
		public static final String TYPE = "Type";
		public static final String MODULES = "Modules";
		public static final String POWER = "Power";
		public static final String DURATION = "Duration";
		public static final String RADIUS = "Radius";
		public static final String PIERCE = "Pierce";
		public static final String SILENT = "Silent";
		public static final String SPEED = "Speed";
		public static final String KNOCKBACK = "Knockback";
		public static final String PROJ_COUNT = "Projectile Count";
		public static final String SCATTER = "Scatter";
		public static final String CRIT_CHANCE = "Crit Chance";
		public static final String CRIT_DAMAGE = "Crit Damage";
		public static final String DISTANCE = "Distance";
		public static final String DAMAGE = "Damage";
		public static final String MANA = "Mana";
		public static final String BURNOUT = "Burnout";
		public static final String COLOR = "Color";
	}

	public static class MISC {
		public static final String SPARKLE_BLURRED = "particles/sparkle_blurred";
		public static final String SPARKLE = "particles/sparkle";
	}

	public static class Data {
		public static final String BLOOD_TYPE = "blood_type";
		public static final String MAX_MANA = "maxMana";
		public static final String MAX_BURNOUT = "max_burnout";
		public static final String MANA = "mana";
		public static final String BURNOUT = "burnout";
		public static final String BLOOD_LEVELS = "blood_levels";
	}

	public static class GuiButtons {
		public static final int NAV_BAR_NEXT = 1;
		public static final int NAV_BAR_BACK = 2;
		public static final int NAV_BAR_INDEX = 3;
		public static final int BOOKMARK = 4;
		public static final int SCHEMATIC_UP_LAYER = 5;
		public static final int SCHEMATIC_DOWN_LAYER = 6;
	}

	public static class WorkTable {
		public static final int DONE_BUTTON = 0;
		public static final int CONFIRM_BUTTON = 1;
	}

	public static class NBT {
		public static final String SPELL = "spellData";
		public static final String TAG_OVERLAY = "overlay";
		public static final String FAIRY_INSIDE = "fairy_inside";
		public static final String FAIRY_COLOR = "fairy_color";
		public static final String FAIRY_AGE = "fairy_age";
		public static final String COLOR = "color";
		public static final String RAND = "rand";
		public static final String LAST_CAST = "last_cast";
		public static final String LAST_COOLDOWN = "last_cooldown";
		public static final String PURITY = "purity";
		public static final String PURITY_OVERRIDE = "purity_override";
		public static final String COMPLETE = "complete";
		public static final int NACRE_PURITY_CONVERSION = 30 * 20; // 30 seconds for max purity, 0/60 for no purity
	}
}
