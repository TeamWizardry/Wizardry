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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class EntitySpellCodex extends Entity {

	private EntityItem book;
	private int expiry;

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

                LibParticles.BOOK_BEAM_NORMAL(world, getPositionVector());
                LibParticles.BOOK_BEAM_HELIX(world, getPositionVector());

				if ((expiry % 5) == 0)
                    world.playSound(null, posX, posY, posZ, ModSounds.FIZZING_LOOP, SoundCategory.AMBIENT, 0.7F, (ThreadLocalRandom.current().nextFloat() * 0.4F) + 0.8F);

			} else {
                EntityItem codex = new EntityItem(world, posX, posY, posZ, new ItemStack(ModItems.BOOK, 1));
                codex.setPickupDelay(0);
				codex.motionY = 0;
				codex.motionX = 0;
				codex.motionZ = 0;
				codex.forceSpawn = true;
				book.getEntityItem().stackSize--;
                world.spawnEntity(codex);
                world.removeEntity(this);

                world.playSound(null, posX, posY, posZ, ModSounds.HARP1, SoundCategory.AMBIENT, 0.3F, 1.0F);

                LibParticles.BOOK_LARGE_EXPLOSION(world, getPositionVector());
                return;
			}
		} else {
			expiry = 200;
			return;
		}

        if (book.isDead) world.removeEntity(this);
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
    }

	@Override
    protected void writeEntityToNBT(@NotNull NBTTagCompound compound) {
    }
}
