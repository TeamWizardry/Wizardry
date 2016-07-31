package com.teamwizardry.wizardry.common.spell.module.booleans;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;

public class ModuleNot extends Module
{
	public ModuleNot(ItemStack stack) {
		super(stack);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.BOOLEAN;
	}
	
	@Override
	public String getDescription()
	{
		return "Will pass condition if it is false.";
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{
		boolean cast = false;
		HashMap<Module, NBTTagCompound> conditionals = new HashMap<Module, NBTTagCompound>();
		NBTTagList children = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
		for (int i = 0; i < children.tagCount(); i++)
		{
			NBTTagCompound child = children.getCompoundTagAt(i);
			Module module = ModuleRegistry.getInstance().getModuleById(child.getInteger(SHAPE));
			if (module.getType() == ModuleType.BOOLEAN || module.getType() == ModuleType.EVENT)
				conditionals.put(module, child);
		}
		for (Module module : conditionals.keySet()) //todo intellij warns about for loop that doesn't loop
		{
			cast = module.cast(player, caster, conditionals.get(module), stack);
			if (!cast) break;
			return false;
		}
		stack.castEffects(caster);
		return cast;
	}

	@Override
	public String getDisplayName() {
		return "Not";
	}
}
