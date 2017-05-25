package com.teamwizardry.wizardry.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/29/2016.
 */
public class EntitySpellCodex extends Entity {

	public int expiry;
	private EntityItem book;

	public EntitySpellCodex(World worldIn) {
		super(worldIn);
	}

	public EntitySpellCodex(World world, EntityItem book) {
		super(world);
		this.book = book;
		posX = book.posX;
		posY = book.posY;
		posZ = book.posZ;
		expiry = 200;
	}

	@Override
	public void onUpdate() {
		world.removeEntity(this);
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		expiry = compound.getInteger("expiry");
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		compound.setInteger("expiry", expiry);
	}
}
