package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashSet;

public interface WizardryWorld extends ICapabilitySerializable<NBTTagCompound> {

	void addLingerSpell(SpellRing spellRing, SpellData data, int expiry);

	void addDelayedSpell(Module module, SpellRing spellRing, SpellData data, int expiry);

	HashSet<SpellTicker.LingeringObject> getLingeringObjects();

	HashSet<SpellTicker.DelayedObject> getDelayedObjects();

}
