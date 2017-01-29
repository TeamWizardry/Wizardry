package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public interface ITargettable {

	boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target);

	boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target);
}
