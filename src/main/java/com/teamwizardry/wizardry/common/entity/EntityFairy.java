package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.base.entity.FlyingEntityMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Created by Demoniaque on 8/21/2016.
 */
@SaveInPlace
public class EntityFairy extends FlyingEntityMod {

	public boolean ambush = false;
	private Color color;
	private boolean sad;
	private int age;
	private boolean changingCourse = false;
	private int changeCourseTick = 0;
	private float tickPitch = 0;
	private float tickYaw = 0;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1F, 1F);
		isAirBorne = true;
		experienceValue = 5;
		color = new Color(RandUtil.nextFloat(), RandUtil.nextFloat(), RandUtil.nextFloat());
		color = color.brighter();
		age = RandUtil.nextInt(1, 100);
	}

	public EntityFairy(World worldIn, Color color, int age) {
		super(worldIn);
		setSize(1F, 1F);
		isAirBorne = true;
		experienceValue = 5;
		this.color = color;
		this.age = age;
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
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

		//if (entity.world.isRemote)
		//	LibParticles.AIR_THROTTLE(world, getPositionVector(), entity, color, color.brighter(), -1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;
		if (isAIDisabled()) return;

		if (ambush) {
			List<Entity> entities = world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getPosition()).grow(64, 64, 64), null);
			for (Entity entity : entities)
				if (entity instanceof EntityPlayer) {

					double dist = entity.getPositionVector().distanceTo(getPositionVector());
					Vec3d sub = entity.getPositionVector().addVector(0, entity.height / 2, 0).subtract(getPositionVector()).normalize().scale(dist / 3.0);

					motionX = sub.x;
					motionY = sub.y;
					motionZ = sub.z;
					velocityChanged = true;

					if ((int) dist <= 0 || RandUtil.nextInt((int) (dist * 20.0)) == 0)
						ambush = false;

					if (entity instanceof EntityPlayerMP)
						((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(this));
				}
			return;
		}

		if (!getNavigator().noPath()) return;

		boolean nopeOut = false;
		List<Entity> entities = world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getPosition()).grow(5, 5, 5), null);
		for (Entity entity : entities)
			if (entity instanceof EntityLivingBase) {
				if (entity.isSneaking()) continue;
				nopeOut = true;

				Vec3d sub = getPositionVector().subtract(entity.getPositionVector().addVector(0, entity.height / 2, 0)).normalize();

				Random rand = new Random(hashCode());
				double speed = rand.nextInt(9) + 1;
				motionX += sub.x / speed;
				motionY += sub.y / speed;
				motionZ += sub.z / speed;
			}

		if (nopeOut) {
			for (int i = -2; i < 2; i++)
				for (int k = -2; k < 2; k++)
					for (int j = -2; j < 2; j++) {
						BlockPos pos = new BlockPos(getPositionVector()).add(i, j, k);
						if (!world.isAirBlock(pos)) {
							Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
							Vec3d sub = getPositionVector().addVector(0, height / 2, 0).subtract(center).normalize();

							Random rand = new Random(hashCode());
							double speed = rand.nextInt(9) + 1;
							motionX += sub.x / speed;
							motionY += sub.y / speed;
							motionZ += sub.z / speed;
						}
					}
		}

		if (!nopeOut) {
			int r = Math.abs(new Random(getPosition().toLong()).nextInt(20)) + 1;
			if (RandUtil.nextInt(r) == 0) {
				changingCourse = true;
				changeCourseTick = RandUtil.nextInt(50);
				tickPitch = (float) RandUtil.nextDouble(-10, 10);
				tickYaw = (float) RandUtil.nextDouble(-10, 10);
			}
			if (changingCourse) {
				if (changeCourseTick > 0) {
					changeCourseTick--;
					Vec3d dir = getVectorForRotation(rotationPitch += tickPitch, rotationYaw += tickYaw).normalize();
					Random rand = new Random(hashCode());
					double speed = rand.nextInt(9) + 1;
					motionX = dir.x / speed;
					motionY = dir.y / speed;
					motionZ = dir.z / speed;
				} else changingCourse = false;
			}
		}
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		if (getHealth() <= 0)
			PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector().addVector(0, 0.25, 0), color, color, 0.5, 0.5, RandUtil.nextInt(100, 200), 75, 25, true),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 256));
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		//super.dropLoot(wasRecentlyHit, lootingModifier, source);
		ItemStack fairyWings = new ItemStack(ModItems.FAIRY_WINGS);
		ItemStack fairyDust = new ItemStack(ModItems.FAIRY_DUST);
		ItemNBTHelper.setInt(fairyWings, NBT.FAIRY_COLOR, color.getRGB());
		entityDropItem(fairyDust, RandUtil.nextFloat());
		entityDropItem(fairyWings, RandUtil.nextFloat());
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("save"), true);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
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
