package com.teamwizardry.wizardry.spells.modules.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleArea extends Module implements IModifier, IRuntimeModifier {
    public ModuleArea() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public NBTTagCompound saveToNBT() {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.RADIUS, new AttributeModifier(Operation.ADD, 1));

        map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 1.5));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5));
    }
}