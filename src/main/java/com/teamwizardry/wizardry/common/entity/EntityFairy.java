package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.NBT;
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
	private boolean dirYawAdd;
	private boolean dirPitchAdd;
	private Color color;
	private double pitchAmount;
	private double yawAmount;
	private boolean sad;
	private int age;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(0.5F, 0.5F);
		isAirBorne = true;
		experienceValue = 5;
		color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
		color = color.brighter();
		rotationPitch = (float) ThreadLocalRandom.current().nextDouble(-90, 90);
		rotationYaw = (float) ThreadLocalRandom.current().nextDouble(-180, 180);
		age = ThreadLocalRandom.current().nextInt(1, 100);
	}

	public EntityFairy(World worldIn, Color color, int age) {
		super(worldIn);
		setSize(0.5F, 0.5F);
		isAirBorne = true;
		experienceValue = 5;
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
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.1D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (getHealth() > 0) {
			if (entity.getName().equals(getName())) return;
			((EntityLivingBase) entity).motionY += 0.3;
			((EntityLivingBase) entity).attackEntityAsMob(this);
			((EntityLivingBase) entity).setRevengeTarget(this);
		}
		entity.fallDistance = 0;

		LibParticles.AIR_THROTTLE(worldObj, getPositionVector(), entity, color, color.brighter());
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) return;

		if (age <= 0) age = 2;
		if (((ticksExisted % ThreadLocalRandom.current().nextInt(200, 400)) == 0) && (age < 100)) age++;

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

					pitchAmount += (prevDirPitchAdd == dirPitchAdd) ? ThreadLocalRandom.current().nextDouble(-4, 4) : (-pitchAmount / 5);
					yawAmount += (prevDirYawAdd == dirYawAdd) ? ThreadLocalRandom.current().nextDouble(-1, 1) : (-yawAmount / 5);
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
		if ((stack != null) && (stack.getItem() == ModItems.JAR)) {
			ItemNBTHelper.setBoolean(stack, NBT.FAIRY_INSIDE, true);
			ItemNBTHelper.setInt(stack, NBT.FAIRY_COLOR, color.getRGB());
			ItemNBTHelper.setInt(stack, NBT.FAIRY_AGE, age);
			stack.setItemDamage(1);
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
		//super.dropLoot(wasRecentlyHit, lootingModifier, source);
		ItemStack fairyWings = new ItemStack(ModItems.FAIRY_WINGS);
		ItemStack fairyDust = new ItemStack(ModItems.FAIRY_DUST);
		ItemNBTHelper.setInt(fairyWings, NBT.FAIRY_COLOR, color.getRGB());
		entityDropItem(fairyDust, ThreadLocalRandom.current().nextFloat());
		entityDropItem(fairyWings, ThreadLocalRandom.current().nextFloat());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey(NBT.COLOR)) color = new Color(compound.getInteger(NBT.COLOR));
		if (compound.hasKey("sad")) sad = compound.getBoolean("sad");
		if (compound.hasKey("age")) age = compound.getInteger("age");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(NBT.COLOR, color.getRGB());
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
