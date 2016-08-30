package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
	private boolean sad = false;
	private int age;

	public EntityFairy(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 0.5F);
		this.isAirBorne = true;
		this.experienceValue = 5;
		color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
		color = color.brighter();
		rotationPitch = (float) ThreadLocalRandom.current().nextDouble(-90, 90);
		rotationYaw = (float) ThreadLocalRandom.current().nextDouble(-180, 180);
		age = ThreadLocalRandom.current().nextInt(1, 100);
	}

	public EntityFairy(World worldIn, Color color, int age) {
		super(worldIn);
		this.setSize(0.5F, 0.5F);
		this.isAirBorne = true;
		this.experienceValue = 5;
		this.color = color;
		rotationPitch = (float) ThreadLocalRandom.current().nextDouble(-90, 90);
		rotationYaw = (float) ThreadLocalRandom.current().nextDouble(-180, 180);
		this.age = age;
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.1D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (this.getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY + 0.25, posZ)), ThreadLocalRandom.current().nextInt(5, 10), 0, (i, build) -> {
			glitter.setMotion(new Vec3d(motionX + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), motionY + ThreadLocalRandom.current().nextDouble(0.1, 0.2), motionZ + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
			if (!sad) glitter.disableMotion();
		});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) return;

		if (age <= 0) age = 2;
		if (ticksExisted % ThreadLocalRandom.current().nextInt(200, 400) == 0 && age < 100) age++;

		LibParticles.FAIRY_TRAIL(worldObj, getPositionVector().addVector(0, 0.25, 0), color, sad, age);

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
		EntityPlayer player = worldObj.getNearestPlayerNotCreative(this, 2);
		if (player != null) {
			if (pitchAmount < 90) {
				dirPitchAdd = false;
				pitchAmount += 0.2;
				readjustingComplete = false;
			}
			match = false;
		}

		if (match) {
			if (readjustingComplete) {
				if (ThreadLocalRandom.current().nextInt(0, 20) == 0) {
					boolean prevDirYawAdd = dirYawAdd;
					boolean prevDirPitchAdd = dirPitchAdd;

					dirYawAdd = ThreadLocalRandom.current().nextBoolean();
					dirPitchAdd = ThreadLocalRandom.current().nextBoolean();

					if (rotationPitch > 89) rotationPitch = -89;
					if (rotationPitch < -89) rotationPitch = 89;
					if (rotationYaw > 179) rotationYaw = -179;
					if (rotationYaw < -179) rotationYaw = 179;

					if (prevDirPitchAdd == dirPitchAdd) pitchAmount += ThreadLocalRandom.current().nextDouble(-4, 4);
					else pitchAmount += -pitchAmount / 5;
					if (prevDirYawAdd == dirYawAdd) yawAmount += ThreadLocalRandom.current().nextDouble(-1, 1);
					else yawAmount += -yawAmount / 5;
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
		motionX = rot.xCoord / ThreadLocalRandom.current().nextDouble(5, 10);
		motionY = rot.yCoord / ThreadLocalRandom.current().nextDouble(5, 10);
		motionZ = rot.zCoord / ThreadLocalRandom.current().nextDouble(5, 10);
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
		if (stack != null && stack.getItem() == ModItems.JAR) {
			ItemNBTHelper.setBoolean(stack, "fairy_inside", true);
			ItemNBTHelper.setInt(stack, "fairy_color", color.getRGB());
			ItemNBTHelper.setInt(stack, "fairy_age", age);
			worldObj.removeEntity(this);
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		super.attackEntityFrom(source, amount);
		LibParticles.FAIRY_EXPLODE(worldObj, getPositionVector().addVector(0, 0.25, 0), color);
		return true;
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("color")) color = new Color(compound.getInteger("color"));
		if (compound.hasKey("sad")) sad = compound.getBoolean("sad");
		if (compound.hasKey("age")) age = compound.getInteger("age");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", color.getRGB());
		compound.setBoolean("sad", sad);
		compound.setInteger("age", age);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isSad() {
		return sad;
	}

	public void setSad(boolean sad) {
		this.sad = sad;
	}

	public int getAge() { return age; }

	public void setAge(int age) { this.age = age; }
}
