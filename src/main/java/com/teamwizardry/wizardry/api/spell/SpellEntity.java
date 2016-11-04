package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.NBT;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SpellEntity extends Entity {
	protected NBTTagCompound spell;

	public SpellEntity(World world) {
		super(world);
		setEntityBoundingBox(Block.NULL_AABB);
	}

	public SpellEntity(World world, NBTTagCompound spell) {
		this(world);
		this.spell = spell;
	}

	public SpellEntity(World world, double posX, double posY, double posZ) {
		super(world);
		setPosition(posX, posY, posZ);
	}

	public SpellEntity(World world, double posX, double posY, double posZ, NBTTagCompound spell) {
		this(world, posX, posY, posZ);
		this.spell = spell;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		readFromNBT(compound);
		if (compound.hasKey(NBT.SPELL))
			spell = compound.getCompoundTag(NBT.SPELL);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if (spell != null) compound.setTag(NBT.SPELL, compound);
		writeToNBT(compound);
	}
}
