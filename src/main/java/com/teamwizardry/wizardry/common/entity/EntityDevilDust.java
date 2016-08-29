package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class EntityDevilDust extends Entity {

	private EntityItem redstone;
	private int expiry = 0;
	private int stackSize = 1;
	private boolean consumed = false;

	public EntityDevilDust(World world, EntityItem redstone) {
		super(world);
		this.redstone = redstone;
		posX = redstone.posX;
		posY = redstone.posY;
		posZ = redstone.posZ;
		expiry = 100;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote) return;

		if (!consumed) {
			BlockPos fire = PosUtils.checkNeighbor(worldObj, redstone.getPosition(), Blocks.FIRE);
			if (worldObj.getBlockState(fire).getBlock() == Blocks.FIRE
					&& worldObj.isMaterialInBB(redstone.getEntityBoundingBox().expand(0.1, 0.1, 0.1), Material.FIRE)) {
				stackSize = redstone.getEntityItem().stackSize;
				if (!redstone.isDead) redstone.setDead();
				consumed = true;
				setPosition(fire.getX() + 0.5, fire.getY(), fire.getZ() + 0.5);
			} else {
				posX = redstone.posX;
				posY = redstone.posY;
				posZ = redstone.posZ;
			}
		} else {
			if (expiry > 0) {
				expiry--;

				LibParticles.DEVIL_DUST_BIG_CRACKLES(getPositionVector());
				LibParticles.DEVIL_DUST_SMALL_CRACKLES(getPositionVector());

				if (expiry % 5 == 0)
					worldObj.playSound(null, posX, posY, posZ, ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));

			} else {
				EntityItem devilDust = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(ModItems.DEVIL_DUST, stackSize));
				devilDust.setPickupDelay(5);
				devilDust.motionY = 0.8;
				devilDust.forceSpawn = true;
				worldObj.spawnEntityInWorld(devilDust);
				worldObj.removeEntity(this);
				return;
			}
		}

		if (redstone.isDead && !consumed) worldObj.removeEntity(this);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("expiry")) expiry = compound.getInteger("expiry");
		if (compound.hasKey("consumed")) consumed = compound.getBoolean("consumed");
		if (compound.hasKey("stack_size")) stackSize = compound.getInteger("stack_size");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("expiry", expiry);
		compound.setInteger("stack_size", stackSize);
		compound.setBoolean("consumed", consumed);
	}
}
