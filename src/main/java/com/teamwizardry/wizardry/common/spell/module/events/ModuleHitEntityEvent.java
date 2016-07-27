package com.teamwizardry.wizardry.common.spell.module.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.IRequireItem;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleHitEntityEvent extends Module implements IRequireItem
{
	public ModuleHitEntityEvent(ItemStack stack) {
		super(stack);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EVENT;
	}
	
	@Override
	public String getDescription()
	{
		return "Called whenever a targetable entity is hit.";
	}

	@Override
	public String getDisplayName() {
		return "If Target Is Hit";
	}
	
	@Override
	public void handle(ItemStack stack)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
