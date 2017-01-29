package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.PotionNullGrav;

/**
 * Created by LordSaad.
 */
public class ModPotions {

	public static PotionNullGrav NULLIFY_GRAVITY;

	public static void init() {
		NULLIFY_GRAVITY = new PotionNullGrav();
	}
}
