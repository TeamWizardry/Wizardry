package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
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

				ParticleBuilder glitter = new ParticleBuilder(30);
				glitter.setScale(ThreadLocalRandom.current().nextFloat());
				glitter.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0));
				glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
				ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(getPositionVector()), 1, 0, (i, builder) -> {
					Vec3d offset = new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
					glitter.setPositionOffset(offset);
					glitter.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
					glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01), ThreadLocalRandom.current().nextDouble(0.04, 0.06), ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
					glitter.disableMotion();
				});

				ParticleBuilder glitter2 = new ParticleBuilder(10);
				glitter2.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0).darker());
				glitter2.setScale((float) ThreadLocalRandom.current().nextDouble(0, 0.5));
				glitter2.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
				ParticleSpawner.spawn(glitter2, worldObj, new StaticInterp<>(getPositionVector()), 5, 0, (i, builder) -> {
					glitter2.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
					glitter2.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03), ThreadLocalRandom.current().nextDouble(0.07, 0.2), ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
					glitter2.disableMotion();
				});

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
