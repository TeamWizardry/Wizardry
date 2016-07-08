package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SpellEntity extends Entity
{
	public SpellEntity(World world)
	{
		super(world);
	}
	
	public SpellEntity(World world, double posX, double posY, double posZ)
	{
		super(world);
		setPosition(posX, posY, posZ);
	}

	@Override
	protected void entityInit()
	{
		
	}
	
	@Override
	public boolean canBeAttackedWithItem()
	{
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
	}
}
