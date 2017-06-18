package com.teamwizardry.wizardry.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;

/**
 * Created by LordSaad.
 */
public class ConfigValues {

	@ConfigProperty(category = "general", comment = "If enabled, will inform you of new updates to the mod.")
	public static boolean versionCheckerEnabled = true;

	@ConfigProperty(category = "world", comment = "How rare the mana pool is in terms of 1 in X")
	public static int manaPoolRarity = 75;

	@ConfigProperty(category = "world", comment = "If you have a dimension ID conflict with this mod and something else, change this number")
	public static int underworldID = 42;

	@ConfigProperty(category = "world", comment = "The maximum possible distance required to link a battery with a mana consuming block")
	public static int manaBatteryLinkDistance = 64;

	@ConfigProperty(category = "items", comment = "The maximum limit a cape can give a player in terms of mana/burnout buffers")
	public static int maxCapeCap = 5000;

	@ConfigProperty(category = "spells", comment = "The multiplier a spell gets for a perfect or ancient quality pearl.\n" +
			"This will be multiplied by the quality value of the pearl, which is 1.0 for perfect pearls and greater for ancient pearls.")
	public static double perfectPearlMultiplier = 1.2;

	@ConfigProperty(category = "spells", comment = "The multiplier a spell gets, as a flat rate, for a depleted quality pearl.")
	public static double damagedPearlMultiplier = 0.05;
}
