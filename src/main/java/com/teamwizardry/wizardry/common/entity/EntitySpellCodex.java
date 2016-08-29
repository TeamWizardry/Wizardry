package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.fluid.FluidMana;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
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

				ParticleBuilder glitter = new ParticleBuilder(50);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
				ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(getPositionVector()), 10, 0, (aFloat, particleBuilder) -> {
					glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 1), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
					glitter.disableMotion();
					glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
					glitter.setScale(ThreadLocalRandom.current().nextFloat());
					glitter.setLifetime(ThreadLocalRandom.current().nextInt(0, 50));
				});

				ParticleBuilder helix = new ParticleBuilder(200);
				helix.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
				ParticleSpawner.spawn(helix, worldObj, new StaticInterp<>(getPositionVector()), 30, 0, (aFloat, particleBuilder) -> {
					helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
					helix.setScale(ThreadLocalRandom.current().nextFloat());
					helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1, 255), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
					helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
				});

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

				ParticleBuilder glitter = new ParticleBuilder(1000);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
				ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY, posZ)), 1000, 0, (i, build) -> {

					double radius = 1;
					double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
					double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
					double r = (u > 1) ? 2 - u : u;
					double x = r * Math.cos(t), z = r * Math.sin(t);

					glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 255), 0));
					glitter.setMotion(new Vec3d(x, 0, z));
					glitter.setJitter(10, new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(-0.05, -0.01), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
					glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(70, 170)));
					glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.5));
				});
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