package com.teamwizardry.wizardry.common.spell.module.booleans;

import java.util.HashMap;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

public class ModuleNand extends Module {
    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public String getDescription() {
        return "Will pass conditions if all are false.";
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		boolean cast = false;
		HashMap<Module, NBTTagCompound> conditionals = new HashMap<Module, NBTTagCompound>();
		HashMap<Module, NBTTagCompound> effects = new HashMap<Module, NBTTagCompound>();
		NBTTagList children = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
		for (int i = 0; i < children.tagCount(); i++)
		{
			NBTTagCompound child = children.getCompoundTagAt(i);
			Module module = ModuleList.INSTANCE.modules.get(child.getTag(CLASS)).construct();
			if (module.getType() == ModuleType.BOOLEAN || module.getType() == ModuleType.EVENT)
				conditionals.put(module, child);
			else effects.put(module, child);
		}
		for (Module module : conditionals.keySet())
		{
			cast = !module.cast(player, caster, conditionals.get(module));
			if (!cast) return false;
		}
		for (Module module : effects.keySet())
		{
			SpellCastEvent event = new SpellCastEvent(effects.get(module), caster, player);
			MinecraftForge.EVENT_BUS.post(event);
		}
		return cast;
	}

    @Override
    public String getDisplayName() {
        return "Nand";
    }
}