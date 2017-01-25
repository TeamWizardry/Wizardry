package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.PotionNullGrav;
import net.minecraft.potion.Potion;

/**
 * Created by LordSaad.
 */
public class ModPotions {

	public static Potion NULLIFY_GRAVITY;

	public static void init() {
		NULLIFY_GRAVITY = new PotionNullGrav(0);
	}
}
