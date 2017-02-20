package com.teamwizardry.wizardry.api.spell;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class SpellCastEvent extends Event {

	public ItemStack stack;
	public Module module;
	public SpellData spell;

	public SpellCastEvent(ItemStack stack, Module module, SpellData spell) {
		this.stack = stack;
		this.module = module;
		this.spell = spell;
	}
}
