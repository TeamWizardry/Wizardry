package com.teamwizardry.wizardry.api;

/**
 * Created by Demoniaque on 6/14/2016.
 */
public class Constants {

	public static class MISC {
		public static final String DIAMOND = "particles/diamond";
		public static final String SPARKLE_BLURRED = "particles/sparkle_blurred";
		public static final String SMOKE = "particles/smoke";
	}

	public static class Data {
		public static final String BLOOD_TYPE = "blood_type";
		public static final String MAX_MANA = "maxMana";
		public static final String MAX_BURNOUT = "max_burnout";
		public static final String MANA = "mana";
		public static final String BURNOUT = "burnout";
		public static final String BLOOD_LEVELS = "blood_levels";
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
