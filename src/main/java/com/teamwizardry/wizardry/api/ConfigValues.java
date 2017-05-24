package com.teamwizardry.wizardry.api;

import com.teamwizardry.librarianlib.features.config.ConfigPropertyBoolean;
import com.teamwizardry.librarianlib.features.config.ConfigPropertyInt;
import com.teamwizardry.wizardry.Wizardry;

/**
 * Created by LordSaad.
 */
public class ConfigValues {

	@ConfigPropertyBoolean(modid = Wizardry.MODID, category = "general", id = "version_checker_enabled", comment = "If enabled, will inform you of new updates to the mod.", defaultValue = true)
	public static boolean versionChecker;

	@ConfigPropertyInt(modid = Wizardry.MODID, category = "world", id = "mana_pool_rarity", comment = "How rare the mana pool is in terms of 1 in X", defaultValue = 75)
	public static int manaPoolRarity;

	@ConfigPropertyInt(modid = Wizardry.MODID, category = "world", id = "underworld_id", comment = "If you have a dimension ID conflict with this mod and something else, change this number", defaultValue = 42)
	public static int underworldID;

	@ConfigPropertyInt(modid = Wizardry.MODID, category = "world", id = "mana_battery_link_distance", comment = "The maximum possible distance required to link a battery with a mana consuming block", defaultValue = 64)
	public static int manaBatteryDistance;
}
