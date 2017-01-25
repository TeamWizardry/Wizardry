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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class EntityDevilDust extends Entity {

	public static final String EXPIRY = "expiry";
	public static final String STACK_SIZE = "stack_size";
	public static final String CONSUMED = "consumed";
	private EntityItem redstone;
	private int expiry;
	private int stackSize = 1;
	private boolean consumed;

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

		if (world.isRemote) return;

		if (consumed) {
			if (expiry > 0) {
				expiry--;

				LibParticles.DEVIL_DUST_BIG_CRACKLES(world, getPositionVector());
				LibParticles.DEVIL_DUST_SMALL_CRACKLES(world, getPositionVector());

				if ((expiry % 5) == 0)
					world.playSound(null, posX, posY, posZ, ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));

			} else {
				EntityItem devilDust = new EntityItem(world, posX, posY, posZ, new ItemStack(ModItems.DEVIL_DUST, stackSize));
				devilDust.setPickupDelay(5);
				devilDust.motionY = 0.8;
				devilDust.forceSpawn = true;
				world.spawnEntity(devilDust);
				world.removeEntity(this);
				return;
			}
		} else {
			BlockPos fire = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.FIRE);
			if ((world.getBlockState(fire).getBlock() == Blocks.FIRE)
					&& world.isMaterialInBB(redstone.getEntityBoundingBox().expand(0.1, 0.1, 0.1), Material.FIRE)) {
				stackSize = redstone.getEntityItem().stackSize;
				if (!redstone.isDead) redstone.setDead();
				consumed = true;
				setPosition(fire.getX() + 0.5, fire.getY(), fire.getZ() + 0.5);
			} else {
				posX = redstone.posX;
				posY = redstone.posY;
				posZ = redstone.posZ;
			}
		}

		if (redstone.isDead && !consumed) world.removeEntity(this);
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(@NotNull NBTTagCompound compound) {
		if (compound.hasKey(EXPIRY)) expiry = compound.getInteger(EXPIRY);
		if (compound.hasKey(CONSUMED)) consumed = compound.getBoolean(CONSUMED);
		if (compound.hasKey(STACK_SIZE)) stackSize = compound.getInteger(STACK_SIZE);
	}

	@Override
	protected void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		compound.setInteger(EXPIRY, expiry);
		compound.setInteger(STACK_SIZE, stackSize);
		compound.setBoolean(CONSUMED, consumed);
	}
}
