package com.teamwizardry.wizardry.api.entity;

import com.teamwizardry.wizardry.api.StringConsts;
import com.teamwizardry.wizardry.api.utils.ColorUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/// TODO move to common package. We're abstracting the fairy part of this system away and making it more general for reuse potential.
public class FairyData implements INBTSerializable<CompoundNBT>, ICapabilityProvider {

	public FairyState fairyState = FairyState.DEPRESSED;
	public Color primaryColor = ColorUtils.generateRandomColor();
	public Color secondaryColor = ColorUtils.generateRandomColor();

	@Nonnull
	public static FairyData deserialize(CompoundNBT nbt) {
		FairyData data = new FairyData();
		data.deserializeNBT(nbt);
		return data;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return null;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(StringConsts.FAIRY_STATE, fairyState.nbt);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(StringConsts.FAIRY_STATE))
			fairyState = FairyState.fromNbt(nbt.getString(StringConsts.FAIRY_STATE));

		if (nbt.contains(StringConsts.PRIMARY_COLOR))
			primaryColor = new Color(nbt.getInt(StringConsts.PRIMARY_COLOR));

		if (nbt.contains(StringConsts.SECONDARY_COLOR))
			secondaryColor = new Color(nbt.getInt(StringConsts.SECONDARY_COLOR));
	}
}