package com.teamwizardry.wizardry.common.spell.module.booleans;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.HashMap;

public class ModuleOr extends Module {
	public ModuleOr(ItemStack stack) {
		super(stack);
	}

	@Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public String getDescription() {
        return "Will pass conditions if any are true.";
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
			Module module = ModuleRegistry.getInstance().getModuleById(child.getInteger(CLASS));;
			if (module.getType() == ModuleType.BOOLEAN || module.getType() == ModuleType.EVENT)
				conditionals.put(module, child);
			else effects.put(module, child);
		}
		for (Module module : conditionals.keySet())
		{
			boolean eval = module.cast(player, caster, conditionals.get(module));
			if (eval) cast = true;
		}
		if (!cast) return false;
		for (Module module : effects.keySet())
		{
			SpellCastEvent event = new SpellCastEvent(effects.get(module), caster, player);
			MinecraftForge.EVENT_BUS.post(event);
		}
		return cast;
	}

    @Override
    public String getDisplayName() {
        return "Or";
    }
}