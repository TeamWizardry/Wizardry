package com.teamwizardry.wizardry.common.module.defaults;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterOverrideDefaults;

@RegisterOverrideDefaults
public class DefaultModuleOverrides {
	
	@ModuleOverride("generic_chargeup_time")
	public int modifyChargeupTime(int originalTime) {
		return originalTime;
	}

}
