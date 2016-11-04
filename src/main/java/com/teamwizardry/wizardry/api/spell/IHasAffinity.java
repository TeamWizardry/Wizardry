package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;

import java.util.Map;

public interface IHasAffinity {
	public Map<IBloodType, Integer> getAffinityLevels();
}
