package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by LordSaad.
 */
public interface ITaxing {

	/**
	 * Will do the necessary costs to drain mana and fill burnout based on the multipliers passed
	 *
	 * @param module The module running the spell.
	 * @param data   The spell data passed to the running spell.
	 * @return If the tax was successfully deducted. If false, the spell needs to fail.
	 */
	default boolean tax(Module module, SpellData data) {
		double multiplier = module.getMultiplier();
		double manaCost = module.getManaDrain() * multiplier;
		double burnoutCost = module.getBurnoutFill() * multiplier;

		Entity caster = data.getData(SpellData.DefaultKeys.CASTER);

		if (caster != null && caster instanceof EntityLivingBase) {
			float reduction = module.getReductionMultiplier((EntityLivingBase) caster);
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

		Minecraft.getMinecraft().player.sendChatMessage(manager.getMana() + "/" + manager.getMaxMana());

		return !fail;
	}
}
