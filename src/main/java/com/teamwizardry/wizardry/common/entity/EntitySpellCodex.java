package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.common.fluid.FluidMana;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

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
		super.onUpdate();
		if (world.isRemote) return;

		posX = book.posX;
		posY = book.posY;
		posZ = book.posZ;

		if (world.getBlockState(getPosition()).getBlock() == FluidMana.instance.getBlock()) {
			if (expiry > 0) {
				expiry--;

				if ((expiry % 5) == 0)
					world.playSound(null, posX, posY, posZ, ModSounds.FIZZING_LOOP, SoundCategory.AMBIENT, 0.7F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);

			} else {
				EntityItem codex = new EntityItem(world, posX, posY, posZ, new ItemStack(ModItems.BOOK, 1));
				codex.setPickupDelay(0);
				codex.motionY = 0;
				codex.motionX = 0;
				codex.motionZ = 0;
				codex.forceSpawn = true;
				book.getEntityItem().setCount(book.getEntityItem().getCount() - 1);
				world.spawnEntity(codex);
				world.removeEntity(this);

				world.playSound(null, posX, posY, posZ, ModSounds.HARP1, SoundCategory.AMBIENT, 0.3F, 1.0F);
				return;
			}
		} else {
			expiry = 200;
			return;
		}

		if (book.isDead) world.removeEntity(this);
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
