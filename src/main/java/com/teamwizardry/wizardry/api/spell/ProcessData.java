package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public class ProcessData {

	public interface Process<T extends NBTBase, E> {
		@Nonnull
		T serialize(@Nullable E object);

		@Nullable
		E deserialize(@Nullable World world, @Nonnull T object);
	}
}
