package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/21/2016.
 */
public class EntityFairy extends EntityCreature {

	private int changeCourseCounter = 0;
	private Color color;

	public EntityFairy(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 0.5F);
		this.isAirBorne = true;
		this.experienceValue = 5;
		color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	protected void initEntityAI() {
		//this.tasks.addTask(7, new EntityAIWander(this, 0.46D));
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
		if (!worldObj.isRemote) return;

		for (int i = 0; i < 10; i++) {
			Vec3d headCenter = new Vec3d(posX + width + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), getEyeHeight() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), posZ + width + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
			SparkleFX headLight = GlitterFactory.getInstance().createSparkle(worldObj, headCenter, 10);
			headLight.setColor(color);
			headLight.setAlpha(1f);
			headLight.setScale(0.5f);
			headLight.setBlurred();
			headLight.setFadeIn();
			headLight.setFadeOut();
		}

		if (motionX == 0 && motionY == 0 && motionZ == 0) newPosition();

		if (changeCourseCounter > 0) {
			changeCourseCounter--;
			return;
		} else {
			changeCourseCounter = ThreadLocalRandom.current().nextInt(100, 500);
		}

		newPosition();
	}

	private void newPosition() {
		int x = getPosition().getX();
		int y = getPosition().getY();
		int z = getPosition().getZ();

		if (y > ThreadLocalRandom.current().nextInt(100, 200)) y -= ThreadLocalRandom.current().nextInt(50, 100);
		else y += ThreadLocalRandom.current().nextInt(-30, 30);
		x += ThreadLocalRandom.current().nextInt(-60, 60);
		z += ThreadLocalRandom.current().nextInt(-60, 60);
		while (worldObj.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR) y++;

		Vec3i destDist = getPosition().subtract(new Vec3i(x, y, z));
		int addX = 0, addY = 0, addZ = 0;
		if (destDist.getX() == 0) addX = ThreadLocalRandom.current().nextInt(10, 20);
		if (destDist.getY() == 0) addY = ThreadLocalRandom.current().nextInt(10, 20);
		if (destDist.getZ() == 0) addZ = ThreadLocalRandom.current().nextInt(10, 20);
		motionX = 1 / (destDist.getX() + addX);
		motionY = 1 / (destDist.getY() + addY);
		motionZ = 1 / (destDist.getZ() + addZ);
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
		if (compound.hasKey("color")) color = Color.rgba(compound.getInteger("color"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", color.hexRGBA());
	}
}
