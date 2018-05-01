package com.teamwizardry.wizardry.utils;

import java.util.List;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeTouch;
import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class SpellCasting {
	
	public static boolean touchInteract(ItemStack stack, List<SpellRing> spells, EntityPlayer player, Entity target, EnumHand hand) {
		Item item = stack.getItem();
		ICooldown cooldown = (item instanceof ICooldown)? (ICooldown)item : null; 
		if (cooldown != null && cooldown.isCoolingDown(stack))
			return false;

		if (BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return false;

		boolean touch = false;
		for (SpellRing ring : spells) {
			if (ring.getModule() instanceof ModuleShapeTouch) {
				touch = true;
				break;
			}
		}

		if (!touch) return false;

		SpellData spell = new SpellData(player.world);
		spell.processEntity(player, true);
		spell.processEntity(target, false);
		SpellUtils.runSpell(spells, spell);

		if (cooldown != null )
			cooldown.setCooldown(player.world, player, hand, stack, spell);
		return true;
	}
}
