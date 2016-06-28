package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleProjectileCount extends Module implements IModifier {
    public ModuleProjectileCount() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.PROJ_COUNT, new AttributeModifier(AttributeModifier.Operation.ADD, 1));

        map.putModifier(Attribute.COST, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.8));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.8));
    }
}