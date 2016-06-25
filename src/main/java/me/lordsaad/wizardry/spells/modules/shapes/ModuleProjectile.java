package me.lordsaad.wizardry.spells.modules.shapes;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleProjectile extends Module {
    public ModuleProjectile() {
    	attributes.addAttribute(Attribute.SPEED);
    	attributes.addAttribute(Attribute.PIERCE);
    	attributes.addAttribute(Attribute.SCATTER);
    	attributes.addAttribute(Attribute.PROJ_COUNT);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }
}