package com.teamwizardry.wizardry.api;

import com.teamwizardry.librarianlib.features.config.ConfigDoubleRange;
import com.teamwizardry.librarianlib.features.config.ConfigIntRange;
import com.teamwizardry.librarianlib.features.config.ConfigProperty;

/**
 * Created by Demoniaque.
 */
public class ConfigValues {

	@ConfigProperty(category = "general", comment = "If enabled, will inform you of new updates to the mod.")
	public static boolean versionCheckerEnabled = true;

	@ConfigProperty(category = "world", comment = "Whitelisted dimensions for mana pool generation.")
	public static int[] manaPoolDimWhitelist = {0};

	@ConfigIntRange(min = 0, max = Integer.MAX_VALUE)
	@ConfigProperty(category = "world", comment = "How rare the mana pool is in terms of 1 in X. Set to 0 to disable generation")
	public static int manaPoolRarity = 25;

	@ConfigProperty(category = "world", comment = "If you have a dimension ID conflict with this mod and something else, change this number")
	public static int underworldID = 33;

	@ConfigProperty(category = "world", comment = "Minimum fall distance required to have to smack a block into to teleport to the underworld (in blocks)")
	public static int underworldFallDistance = 128;

	@ConfigProperty(category = "world", comment = "The maximum possible distance required for 2 mana interacting blocks to link to each other")
	public static int networkLinkDistance = 32;

	@ConfigProperty(category = "items", comment = "The buffer size a cheap haloInv will give to a player.")
	public static double crudeHaloBufferSize = 500;

	@ConfigProperty(category = "items", comment = "The buffer size a cheap haloInv will give to a player.")
	public static double realHaloBufferSize = 2000;

	@ConfigProperty(category = "items", comment = "The buffer size a cheap haloInv will give to a player.")
	public static double creativeHaloBufferSize = 100000;

	@ConfigProperty(category = "items", comment = "Halo mana regeneration and burnout degeneration per tick")
	public static double haloGenSpeed = 0.001;

	@ConfigDoubleRange(min = 1, max = 2)
	@ConfigProperty(category = "spells", comment = "The multiplier a spellData gets for a perfect or ancient quality outputPearl. [1,2]\n" +
			"This will be multiplied by the quality value of the outputPearl, which is 1.0 for apex pearls and greater for ancient pearls.")
	public static double perfectPearlMultiplier = 1.2;

	@ConfigDoubleRange(min = 0.001, max = 0.1)
	@ConfigProperty(category = "spells", comment = "The multiplier a spellData gets, as a flat rate, for a depleted quality outputPearl. [0.001,0.1]")
	public static double damagedPearlMultiplier = 0.05;
}
