package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class ProcessData {

	public interface Process<T extends NBTBase, E> {
		T serialize(E object);

		E deserialize(World world, T object);
	}
}
