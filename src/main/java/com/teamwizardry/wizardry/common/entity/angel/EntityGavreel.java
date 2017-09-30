package com.teamwizardry.wizardry.common.entity.angel;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class EntityGavreel extends EntityAngel {

	public EntityGavreel(World worldIn) {
		super(worldIn);
		setCustomNameTag("Gavreel");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;

		fallDistance = 0;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
}
