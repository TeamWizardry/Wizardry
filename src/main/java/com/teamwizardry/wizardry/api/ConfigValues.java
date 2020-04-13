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
	
	@ConfigProperty(category = "general", comment = "If enabled, will print out detailed logging info during startup")
	public static boolean debugInfo = false;

	@ConfigProperty(category = "general", comment = "If enabled, external recipes and modules will be forcibly reset to default.\nDisable to allow custom recipes and module values.")
	public static boolean useInternalValues = false;
	
	@ConfigProperty(category = "world", comment = "If true, mana pool dimension whitelist is instead a blacklist")
	public static boolean isDimBlacklist = false;
	
	@ConfigProperty(category = "world", comment = "Whitelisted dimensions for mana pool generation.")
	public static int[] manaPoolDimWhitelist = {0};

	@ConfigIntRange(min = 0, max = Integer.MAX_VALUE)
	@ConfigProperty(category = "world", comment = "How rare the mana pool is in terms of 1 in X. Set to 0 to disable generation")
	public static int manaPoolRarity = 25;

	@ConfigProperty(category = "world", comment = "If you have a dimension ID conflict with this mod and something else, change this number")
	public static int underworldID = 33;

	@ConfigProperty(category = "world", comment = "If you have a dimension ID conflict with this mod and something else, change this number")
	public static int torikkiID = 34;

	@ConfigProperty(category = "world", comment = "Minimum fall speed required to have to smack a block into to teleport to the underworld (in blocks). Positive values disable teleporting")
	public static double underworldFallSpeed = -2.7;

	@ConfigProperty(category = "world", comment = "The maximum possible distance required for 2 mana interacting blocks to link to each other")
	public static int networkLinkDistance = 32;

	@ConfigProperty(category = "items", comment = "The buffer size a crude halo will give to a player.")
	public static double crudeHaloBufferSize = 1000;

	@ConfigProperty(category = "items", comment = "The buffer size a real halo will give to a player.")
	public static double realHaloBufferSize = 5000;

	@ConfigProperty(category = "items", comment = "The buffer size a creative halo will give to a player.")
	public static double creativeHaloBufferSize = 50000;

	@ConfigProperty(category = "items", comment = "Halo mana regeneration and burnout degeneration per tick")
	public static double haloGenSpeed = 0.001;

	@ConfigIntRange(min = 1, max = 20)
	@ConfigProperty(category = "items", comment = "Pearl belt inventory size")
	public static int pearlBeltInvSize = 8;

	@ConfigDoubleRange(min = 1, max = 16)
	@ConfigProperty(category = "entity", comment = "Defines the reach distance of fairies in terms of blocks")
	public static double fairyReach = 3;

	@ConfigDoubleRange(min = 0, max = 1000)
	@ConfigProperty(category = "entity", comment = "The maximum number of zombies a player can have active at once via the reinforcements spell")
	public static int maxZombies = 100;

	@ConfigDoubleRange(min = 1, max = 2)
	@ConfigProperty(category = "spells", comment = "The multiplier a spellData gets for a perfect or ancient quality output. [1,2]\n" +
			"This will be multiplied by the quality value of the output, which is 1.0 for apex pearls and greater for ancient pearls.")
	public static double perfectPearlMultiplier = 1.2;

	@ConfigDoubleRange(min = 0.001, max = 0.1)
	@ConfigProperty(category = "spells", comment = "The multiplier a spellData gets, as a flat rate, for a depleted quality output. [0.001,0.1]")
	public static double damagedPearlMultiplier = 0.05;
	
	@ConfigIntRange(min = 1, max = Integer.MAX_VALUE)
	@ConfigProperty(category = "spells", comment = "Maximum number of ticks between Zone activations. Minimum of 1.")
	public static int zoneTimer = 20;
	
	@ConfigIntRange(min = 1, max = Integer.MAX_VALUE)
	@ConfigProperty(category = "spells", comment = "Maximum number of ticks between Beam activations. Minimum of 1.")
	public static int beamTimer = 10;
}
