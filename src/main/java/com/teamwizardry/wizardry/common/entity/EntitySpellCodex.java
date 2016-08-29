package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.common.fluid.FluidMana;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class EntitySpellCodex extends Entity {

	private EntityItem book;
	private int expiry = 0;

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
		super.onUpdate();
		if (worldObj.isRemote) return;

		posX = book.posX;
		posY = book.posY;
		posZ = book.posZ;

		if (worldObj.getBlockState(getPosition()).getBlock() != FluidMana.instance.getBlock()) {
			expiry = 200;
			return;
		} else {
			if (expiry > 0) {
				expiry--;

				LibParticles.BOOK_BEAM_NORMAL(getPositionVector());
				LibParticles.BOOK_BEAM_HELIX(getPositionVector());

				if (expiry % 5 == 0)
					worldObj.playSound(null, posX, posY, posZ, ModSounds.FIZZING_LOOP, SoundCategory.AMBIENT, 0.7F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);

			} else {
				EntityItem codex = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(ModItems.BOOK, 1));
				codex.setPickupDelay(0);
				codex.motionY = 0;
				codex.motionX = 0;
				codex.motionZ = 0;
				codex.forceSpawn = true;
				book.getEntityItem().stackSize--;
				worldObj.spawnEntityInWorld(codex);
				worldObj.removeEntity(this);

				worldObj.playSound(null, posX, posY, posZ, ModSounds.HARP1, SoundCategory.AMBIENT, 0.3F, 1F);

				LibParticles.BOOK_LARGE_EXPLOSION(getPositionVector());
				return;
			}
		}

		if (book.isDead) worldObj.removeEntity(this);
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
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}
}