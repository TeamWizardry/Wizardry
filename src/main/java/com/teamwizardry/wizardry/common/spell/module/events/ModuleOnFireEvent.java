package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleOnFireEvent extends Module {
    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Called whenever a targetable entity is lit on fire.";
    }
}