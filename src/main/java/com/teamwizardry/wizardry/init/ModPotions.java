package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.PotionNullGrav;
import com.teamwizardry.wizardry.common.potion.PotionSteroid;

/**
 * Created by LordSaad.
 */
public class ModPotions {

	public static PotionNullGrav NULLIFY_GRAVITY;
	public static PotionSteroid STEROID;

	public static void init() {
		NULLIFY_GRAVITY = new PotionNullGrav();
		STEROID = new PotionSteroid();
	}
}
