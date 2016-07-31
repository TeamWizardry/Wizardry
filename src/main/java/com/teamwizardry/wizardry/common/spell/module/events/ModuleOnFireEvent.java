package com.teamwizardry.wizardry.common.spell.module.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;

public class ModuleOnFireEvent extends Module {
    public ModuleOnFireEvent(ItemStack stack) {
        super(stack);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }

    @Override
    public String getDescription() {
        return "Called whenever a targetable entity is lit on fire.";
    }

    @Override
    public String getDisplayName() {
        return "If Target Is On Fire";
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{
		// TODO Auto-generated method stub
		return false;
	}
}