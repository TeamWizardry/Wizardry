package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class ProcessData {

	public interface Process<T extends NBTBase, E> {
		@NotNull
		T serialize(@Nullable E object);

		@Nullable
		E deserialize(@NotNull World world, @NotNull T object);
	}
}
