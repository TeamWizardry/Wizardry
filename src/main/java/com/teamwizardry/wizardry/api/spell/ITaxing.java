package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Demoniaque.
 */
public interface ITaxing {

	/**
	 * Will do the necessary costs to drain mana and fill burnout based on the multipliers passed
	 *
	 * @param module    The module running the spellData.
	 * @param data      The spellData data passed to the running spellData.
	 * @param spellRing The spellData ring passed to the running spellData.
	 * @return If the tax was successfully deducted. If false, the spellData needs to fail.
	 */
	default boolean tax(Module module, SpellData data, SpellRing spellRing) {
		double manaCost = module.getManaDrain() * module.getManaMultiplier();
		double burnoutCost = module.getBurnoutFill() * module.getBurnoutMultiplier();

		Entity caster = data.getData(SpellData.DefaultKeys.CASTER);

		if (caster != null && caster instanceof EntityLivingBase) {
			float reduction = spellRing.getReductionMultiplier((EntityLivingBase) caster);
			manaCost *= reduction;
			burnoutCost *= reduction;
		}

		CapManager manager;
		if (caster == null) manager = new CapManager(data.getData(SpellData.DefaultKeys.CAPABILITY));
		else manager = new CapManager(caster);

		manager.setEntity(caster);
		manager.setManualSync(true);

		boolean fail = false;
		if (manager.getMana() < manaCost) fail = true;

		manager.removeMana(manaCost);
		manager.addBurnout(burnoutCost);

		manager.sync();

		return !fail;
	}
}
