package com.teamwizardry.wizardry.api.entity;

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

public class FairyData implements INBTSerializable<CompoundNBT>, ICapabilityProvider {

	private static final String FAIRY_STATE = "fairy_state";
	private static final String SECONDARY_COLOR = "secondary_color";
	private static final String PRIMARY_COLOR = "primary_color";

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
		nbt.putString(FAIRY_STATE, fairyState.nbt);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(FAIRY_STATE)) fairyState = FairyState.fromNbt(nbt.getString(FAIRY_STATE));

		if (nbt.contains(PRIMARY_COLOR))
			primaryColor = new Color(nbt.getInt(PRIMARY_COLOR));

		if (nbt.contains(SECONDARY_COLOR))
			secondaryColor = new Color(nbt.getInt(SECONDARY_COLOR));
	}
}