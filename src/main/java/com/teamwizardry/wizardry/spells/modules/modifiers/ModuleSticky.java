package com.teamwizardry.wizardry.spells.modules.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSticky extends Module implements IModifier, IRuntimeModifier {
    private int baseCost = 5;
    private int baseBurnout = 5;

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
        map.putModifier(Attribute.COST, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.2));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.2));
        map.putModifier(Attribute.COST, new AttributeModifier(AttributeModifier.Operation.ADD, attributes.apply(Attribute.COST, baseCost), AttributeModifier.Priority.HIGH));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.ADD, attributes.apply(Attribute.BURNOUT, baseBurnout), AttributeModifier.Priority.HIGH));
    }
}