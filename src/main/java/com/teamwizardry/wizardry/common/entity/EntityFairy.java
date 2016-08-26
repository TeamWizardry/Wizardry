package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/21/2016.
 */
public class EntityFairy extends EntityFlying {

	private boolean readjustingComplete = true;
	private boolean dirYawAdd = false;
	private boolean dirPitchAdd = false;
	private Color color;
	private double pitchAmount = 0, yawAmount = 0;
	private boolean enableNextStep = true;

	public EntityFairy(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 0.5F);
		this.isAirBorne = true;
		this.experienceValue = 5;
		color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
		color = color.brighter();
		rotationPitch = (float) ThreadLocalRandom.current().nextDouble(-90, 90);
		rotationYaw = (float) ThreadLocalRandom.current().nextDouble(-180, 180);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.1D);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (this.getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) return;

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY + 0.25, posZ)), 20, 0, (i, build) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			glitter.disableMotion();
		});

		boolean match = true;
		for (int i = -3; i < 3; i++)
			for (int j = -3; j < 0; j++)
				for (int k = -3; k < 3; k++)
					if (worldObj.getBlockState(new BlockPos(posX + i, posY + j, posZ + k)).getBlock() != Blocks.AIR) {
						if (pitchAmount < 90) {
							dirPitchAdd = false;
							pitchAmount += 0.2;
							readjustingComplete = false;
						}
						match = false;
						break;
					}
		if (match) {
			if (readjustingComplete) {
				if (ThreadLocalRandom.current().nextInt(0, 20) == 0) {
					dirYawAdd = ThreadLocalRandom.current().nextBoolean();
					dirPitchAdd = ThreadLocalRandom.current().nextBoolean();
					if (rotationPitch > 89) rotationPitch = -89;
					if (rotationPitch < -89) rotationPitch = 89;
					if (rotationYaw > 179) rotationYaw = -179;
					if (rotationYaw < -179) rotationYaw = 179;
					pitchAmount += ThreadLocalRandom.current().nextDouble(-4, 4);
					yawAmount += ThreadLocalRandom.current().nextDouble(-1, 1);
				}
			} else {
				if (pitchAmount > ThreadLocalRandom.current().nextInt(-20, 20)) {
					dirPitchAdd = false;
					pitchAmount -= ThreadLocalRandom.current().nextDouble(0.5, 5);
				} else readjustingComplete = true;
			}
		}

		if (dirYawAdd) rotationYaw += yawAmount;
		else rotationYaw -= yawAmount;

		if (dirPitchAdd) rotationPitch += pitchAmount;
		else rotationPitch -= pitchAmount;

		Vec3d rot = getVectorForRotation(rotationPitch, rotationYaw);
		motionX = rot.xCoord / 10;
		motionY = rot.yCoord / 10;
		motionZ = rot.zCoord / 10;
	}

	private void checkForHostiles() {
		//ArrayList<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(-3, -3, -3, 3, 3, 3));
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		if (!getEntityWorld().isRemote) {
			/*for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(2) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(Items.BONE, 1)));
				}
			}
			for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(3) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(MainRegistry.impTallow, 1)));
				}
			}*/
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("color")) color = new Color(compound.getInteger("color"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", color.getRGB());
	}
}
