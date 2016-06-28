package com.teamwizardry.wizardry.common.spell.module.booleans;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleNand extends Module {
    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public String getDescription() {
        return "Will pass conditions if all are false.";
    }
}