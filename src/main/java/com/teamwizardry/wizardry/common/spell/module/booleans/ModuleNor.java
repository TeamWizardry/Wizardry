package com.teamwizardry.wizardry.common.spell.module.booleans;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.HashMap;

public class ModuleNor extends Module {
    public ModuleNor(ItemStack stack) {
        super(stack);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public String getDescription() {
        return "Will pass conditions if any are false.";
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        boolean cast = false;
        HashMap<Module, NBTTagCompound> conditionals = new HashMap<Module, NBTTagCompound>();
        NBTTagList children = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
        for (int i = 0; i < children.tagCount(); i++) {
            NBTTagCompound child = children.getCompoundTagAt(i);
            Module module = ModuleRegistry.getInstance().getModuleByLocation(child.getString(SHAPE));
            if (module.getType() == ModuleType.BOOLEAN || module.getType() == ModuleType.EVENT)
                conditionals.put(module, child);
        }
        for (Module module : conditionals.keySet()) {
            boolean eval = !module.cast(player, caster, conditionals.get(module), stack);
            if (eval) cast = true;
        }
        if (!cast) return false;
        stack.castEffects(caster);
        return cast;
    }

    @Override
    public String getDisplayName() {
        return "Nor";
    }
}
