package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.IRequireItem;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModulePotionEvent extends Module implements IRequireItem {
	public ModulePotionEvent(ItemStack stack) {
		super(stack);
	}

	@Override
	public ModuleType getType() {
		return ModuleType.EVENT;
	}

	@Override
	public String getDescription() {
		return "Called whenever a targetable entity is under the effect of the given potion.";
	}

	@Override
	public String getDisplayName() {
		return "If Target Has Potion Effect";
	}

	@Override
	public void handle(ItemStack stack) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
}
