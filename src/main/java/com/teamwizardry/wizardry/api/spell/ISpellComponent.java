package com.teamwizardry.wizardry.api.spell;

import java.util.List;

import net.minecraft.item.Item;

public interface ISpellComponent
{
	String getName();
	List<Item> getItems();
}
