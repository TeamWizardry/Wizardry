package com.teamwizardry.wizardry.spells.modules.events;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.spells.modules.ModuleType;

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