package com.teamwizardry.wizardry.common.module;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;

public interface IModuleOverrides {
	@ModuleOverrideInterface("generic_chargeup_time")
	int modifyChargeupTime(int originalTime);
}
