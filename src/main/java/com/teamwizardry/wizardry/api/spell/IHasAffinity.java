package com.teamwizardry.wizardry.api.spell;

import java.util.Map;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;

public interface IHasAffinity
{
	public Map<IBloodType, Integer> getAffinityLevels();
}
