package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/21/2016.
 */
public class EntityFairy extends EntityFlying {

	public boolean ambush = false;
	private Color color;
	private boolean sad;
	private int age;
	private boolean isFlying = false;
	private boolean changingCourse = false;
	private int changeCourseTick = 0;
	private float tickPitch = 0;
	private float tickYaw = 0;

	public EntityFairy(World worldIn) {
		super(worldIn);
		setSize(1F, 1F);
		isAirBorne = true;
		experienceValue = 5;
		color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
		color = color.brighter();
		age = ThreadLocalRandom.current().nextInt(1, 100);
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
		return getEntityWorld().getBlockState(getPosition().down()).getBlock() == ModBlocks.CLOUD;
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

		LibParticles.AIR_THROTTLE(world, getPositionVector(), entity, color, color.brighter(), -1, true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;

		LibParticles.FAIRY_HEAD(world, getPositionVector().addVector(0, 0.25, 0), color);
		LibParticles.FAIRY_TRAIL(world, getPositionVector().addVector(0, 0.25, 0), color, sad, new Random(getUniqueID().hashCode()).nextInt(300));

		if (ambush) {
			List<Entity> entities = world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getPosition()).expand(64, 64, 64), null);
			for (Entity entity : entities)
				if (entity instanceof EntityPlayer) {

					double dist = entity.getPositionVector().distanceTo(getPositionVector());
					Vec3d sub = entity.getPositionVector().addVector(0, entity.height / 2, 0).subtract(getPositionVector()).normalize().scale(dist / 3.0);

					motionX = sub.xCoord;
					motionY = sub.yCoord;
					motionZ = sub.zCoord;
					velocityChanged = true;

					if ((int) dist <= 0 || ThreadLocalRandom.current().nextInt((int) (dist * 20.0)) == 0)
						ambush = false;

					if (entity instanceof EntityPlayerMP)
						((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(this));
				}
			return;
		}

		if (!getNavigator().noPath()) return;

		boolean nopeOut = false;
		List<Entity> entities = world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getPosition()).expand(5, 5, 5), null);
		for (Entity entity : entities)
			if (entity instanceof EntityLivingBase) {
				nopeOut = true;

				Vec3d sub = getPositionVector().subtract(entity.getPositionVector().addVector(0, entity.height / 2, 0)).normalize();

				Random rand = new Random(hashCode());
				double speed = rand.nextInt(9) + 1;
				motionX += sub.xCoord / speed;
				motionY += sub.yCoord / speed;
				motionZ += sub.zCoord / speed;
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
							motionX += sub.xCoord / speed;
							motionY += sub.yCoord / speed;
							motionZ += sub.zCoord / speed;
						}
					}
		}

		if (!nopeOut) {
			int r = Math.abs(new Random(getPosition().toLong()).nextInt(20)) + 1;
			if (ThreadLocalRandom.current().nextInt(r) == 0) {
				changingCourse = true;
				changeCourseTick = ThreadLocalRandom.current().nextInt(50);
				tickPitch = (float) ThreadLocalRandom.current().nextDouble(-10, 10);
				tickYaw = (float) ThreadLocalRandom.current().nextDouble(-10, 10);
			}
			if (changingCourse) {
				if (changeCourseTick > 0) {
					changeCourseTick--;
					Vec3d dir = getVectorForRotation(rotationPitch += tickPitch, rotationYaw += tickYaw).normalize();
					Random rand = new Random(hashCode());
					double speed = rand.nextInt(9) + 1;
					motionX = dir.xCoord / speed;
					motionY = dir.yCoord / speed;
					motionZ = dir.zCoord / speed;
				} else changingCourse = false;
			}
		}
	}

	@NotNull
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
		if ((stack != null) && (stack.getItem() == ModItems.JAR)) {
			ItemNBTHelper.setBoolean(stack, NBT.FAIRY_INSIDE, true);
			ItemNBTHelper.setInt(stack, NBT.FAIRY_COLOR, color.getRGB());
			ItemNBTHelper.setInt(stack, NBT.FAIRY_AGE, age);
			stack.setItemDamage(1);
			world.removeEntity(this);
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		super.attackEntityFrom(source, amount);
		LibParticles.FAIRY_EXPLODE(world, getPositionVector().addVector(0, 0.25, 0), color);
		return true;
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @NotNull DamageSource source) {
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
		color = new Color(compound.getInteger(NBT.COLOR));
		sad = compound.getBoolean("sad");
		age = compound.getInteger("age");
		isFlying = compound.getBoolean("is_flying");
		changingCourse = compound.getBoolean("changing_course");
		ambush = compound.getBoolean("ambush");
		changeCourseTick = compound.getInteger("changing_course_tick");
		tickPitch = compound.getFloat("tick_pitch");
		tickYaw = compound.getFloat("tick_yaw");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(NBT.COLOR, color.getRGB());
		compound.setBoolean("sad", sad);
		compound.setInteger("age", age);
		compound.setBoolean("is_flying", isFlying);
		compound.setBoolean("changing_course", changingCourse);
		compound.setBoolean("ambush", ambush);
		compound.setInteger("changing_course_tick", changeCourseTick);
		compound.setFloat("tick_pitch", tickPitch);
		compound.setFloat("tick_yaw", tickYaw);
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
