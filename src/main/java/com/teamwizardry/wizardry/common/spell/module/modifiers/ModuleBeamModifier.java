package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleBeamModifier extends Module implements IModifier {
    public ModuleBeamModifier() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.DISTANCE, new AttributeModifier(Operation.ADD, 1));
    }
}