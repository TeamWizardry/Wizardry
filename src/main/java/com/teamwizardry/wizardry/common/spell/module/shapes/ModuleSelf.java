package com.teamwizardry.wizardry.common.spell.module.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ModuleSelf extends Module {
    public ModuleSelf() {
    	attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription() {
        return "Casts the spell on the caster. IE: You";
    }

    @Override
    public String getDisplayName() {
        return "Self";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 5));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 5));
    	return null;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
		for (int i = 0; i < modules.tagCount(); i++)
		{
			SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), caster, player);
			MinecraftForge.EVENT_BUS.post(event);
		}
		return true;
	}
}