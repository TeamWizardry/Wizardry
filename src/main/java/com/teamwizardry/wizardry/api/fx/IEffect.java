package com.teamwizardry.wizardry.api.fx;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by saad4 on 23/7/2016.
 */
public interface IEffect {

	/**
	 * Will spawn the effect
	 *
	 * @param pos The precise position the effect will spawn at
	 */
	void spawn(World world, Vec3d pos);
}
