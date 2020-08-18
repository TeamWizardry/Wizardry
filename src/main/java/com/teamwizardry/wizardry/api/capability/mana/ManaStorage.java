package com.teamwizardry.wizardry.api.capability.mana;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import static com.teamwizardry.wizardry.api.StringConsts.*;

public class ManaStorage implements Capability.IStorage<IManaCapability> {

	@Nullable
	@Override
	public INBT writeNBT(Capability<IManaCapability> capability, IManaCapability instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putLong(MANA, instance.getMana());
		nbt.putLong(MAX_MANA, instance.getMaxMana());
		nbt.putLong(BURNOUT, instance.getBurnout());
		nbt.putLong(MAX_BURNOUT, instance.getMaxBurnout());
		return nbt;
	}

	@Override
	public void readNBT(Capability<IManaCapability> capability, IManaCapability instance, Direction side, INBT nbt) {
		if (!(nbt instanceof CompoundNBT)) return;

		CompoundNBT compoundNBT = (CompoundNBT) nbt;
		instance.setMana(compoundNBT.getLong(MANA));
		instance.setMaxMana(compoundNBT.getLong(MAX_MANA));
		instance.setBurnout(compoundNBT.getLong(BURNOUT));
		instance.setMaxBurnout(compoundNBT.getLong(MAX_BURNOUT));
	}
}
