package com.teamwizardry.wizardry.common.core;

import net.minecraft.util.DamageSource;

/**
 * @author WireSegal
 * Created at 3:17 PM on 6/18/17.
 */
public class DamageSourceMana extends DamageSource {
	public static DamageSourceMana INSTANCE = new DamageSourceMana();

	public DamageSourceMana() {
		super("wizardry.mana");
		setDamageBypassesArmor();
		setDifficultyScaled();
		setMagicDamage();
	}
}
