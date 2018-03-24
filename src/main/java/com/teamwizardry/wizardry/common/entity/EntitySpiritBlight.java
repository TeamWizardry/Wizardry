package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by Demoniaque on 8/17/2016.
 */
public class EntitySpiritBlight extends EntityMob {

	public static final DataParameter<Integer> DATA_SHIFT_SEED = EntityDataManager.createKey(EntitySpiritBlight.class, DataSerializers.VARINT);

	public EntitySpiritBlight(World worldIn) {
		super(worldIn);
		setSize(0.6F, 1.95F);
		experienceValue = 5;

		setShiftseed(RandUtil.nextInt(100, 100000));
	}

	public void setShiftseed(int seed) {
		this.getDataManager().set(DATA_SHIFT_SEED, seed);
		this.getDataManager().setDirty(DATA_SHIFT_SEED);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		isImmuneToFire = true;
		this.getDataManager().register(DATA_SHIFT_SEED, RandUtil.nextInt(100, 1000000));
	}

	@Override
	protected void initEntityAI() {
		//tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 50.0F));
		//applyEntityAI();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(75.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
		//this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
	}

	protected void applyEntityAI() {
		//targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	public void collideWithEntity(Entity entity) {
		//if (getHealth() > 0) {
		//	if (entity.getName().equals(getName())) return;
		//	((EntityLivingBase) entity).motionY += 0.4;
		//	((EntityLivingBase) entity).attackEntityAsMob(this);
		//	((EntityLivingBase) entity).setRevengeTarget(this);
		//}
		//entity.fallDistance = 0;
//
		//Vec3d normal = new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(0.1, 0.4), RandUtil.nextDouble(-0.01, 0.01));
//
		//ClientRunnable.run(new ClientRunnable() {
		//	@Override
		//	@SideOnly(Side.CLIENT)
		//	public void runIfClient() {
		//		LibParticles.AIR_THROTTLE(world, getPositionVector().addVector(0, getEyeHeight(), 0), normal, Color.WHITE, Color.YELLOW, RandUtil.nextDouble(0.2, 1.0));
		//	}
		//});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;
		if (isAIDisabled()) return;

		if (RandUtil.nextInt(100) == 0) {
			setShiftseed(RandUtil.nextInt(1000, 100000000));
			playSound(ModSounds.ZAP, 0.3f, RandUtil.nextFloat(1.5f, 2f));
		}

		if ((ticksExisted % RandUtil.nextInt(100, 200)) == 0)
			playSound(ModSounds.HALLOWED_SPIRIT, RandUtil.nextFloat(), RandUtil.nextFloat());


		//if (world.getTotalWorldTime() % 30 == 0) {
		//	playSound(ModSounds.ELECTRIC_WHITE_NOISE, RandUtil.nextFloat(0.2f, 0.3f), RandUtil.nextFloat(0.1f, 0.3f));
		//}

		fallDistance = 0;

		EntityPlayer farPlayer = world.getNearestPlayerNotCreative(this, 300);
		setAttackTarget(farPlayer);
		if (getAttackTarget() != null) {
			noClip = true;
			Vec3d direction = getPositionVector().subtract(getAttackTarget().getPositionVector()).normalize();
			motionX = direction.x * -0.05;
			motionY = direction.y * -0.05;
			motionZ = direction.z * -0.05;
			rotationYaw = (float) (((-MathHelper.atan2(direction.x, direction.z) * 180) / Math.PI) - 180) / 2;
		} else {
			if (!collidedVertically) {
				motionY = 0;
			}
			noClip = false;
		}

		EntityPlayer player = getAttackTarget() == null ? null : world.getNearestPlayerNotCreative(this, 2);
		EntityPlayer closePlayer = getAttackTarget() == null ? null : world.getNearestPlayerNotCreative(this, 30);
		boolean angry = player != null;

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder glitter = new ParticleBuilder(30);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
				glitter.setColor(new Color(0xf404d4));

				RandUtilSeed seed = new RandUtilSeed(getDataManager().get(DATA_SHIFT_SEED));
				Matrix4 matrix4 = new Matrix4();
				matrix4.rotate(rotationYaw, new Vec3d(0, 1, 0));

				Vec3d offset = new Vec3d(seed.nextDouble(-2, 2), seed.nextDouble(-2, 2), seed.nextDouble(-2, 2));

				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(
						getPositionVector().addVector(0, getEyeHeight(), 0).add(offset)), 10, 0, (i, build) -> {
					double radius = 0.1;
					double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
					double r = radius * RandUtil.nextFloat();
					double x = r * MathHelper.cos((float) theta);
					double z = r * MathHelper.sin((float) theta);

					glitter.setLifetime(RandUtil.nextInt(10, 40));
					glitter.setScaleFunction(new InterpScale(0, (float) RandUtil.nextDouble(3, 4)));
					glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.2), z));
					if (RandUtil.nextInt(15) == 0)
						glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(0, 0.03),
								RandUtil.nextDouble(-0.01, 0.01)));
				});
			}
		});

		if (angry) {
			player.attackEntityFrom(DamageSource.MAGIC, 0.15f);
			player.hurtResistantTime = 0;
		}
	}

	@Override
	public int getBrightnessForRender() {
		return 255;
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		if (source.isMagicDamage()) {
			super.attackEntityFrom(source, amount);
			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient() {
					LibParticles.SPIRIT_WIGHT_HURT(world, getPositionVector());
				}
			});
			return true;
		} else return false;
	}

	@Override
	public void onDeath(DamageSource cause) {
		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(100, 150));
				glitter.setColor(Color.WHITE);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));
				glitter.setAcceleration(Vec3d.ZERO);

				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector().addVector(0, height, 0)), 1000, 0, (i, build) -> {
					double radius = 0.2;
					build.setDeceleration(new Vec3d(RandUtil.nextDouble(0.8, 0.95), RandUtil.nextDouble(0.8, 0.95), RandUtil.nextDouble(0.8, 0.95)));
					build.addMotion(new Vec3d(RandUtil.nextDouble(-radius, radius), RandUtil.nextDouble(-radius, radius), RandUtil.nextDouble(-radius, radius)));
					build.setLifetime(RandUtil.nextInt(200, 250));
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.6f, 1.5f), 0));
					if (RandUtil.nextBoolean()) build.setColor(Color.WHITE);
					else build.setColor(new Color(0xf404d4));
				});
			}
		});
		playSound(ModSounds.BASS_BOOM, 3, 0.5f);
		playSound(ModSounds.BASS_BOOM, 1, RandUtil.nextFloat(1, 1.5f));
		super.onDeath(cause);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if (compound.hasKey("shift_seed")) {
			this.getDataManager().set(DATA_SHIFT_SEED, compound.getInteger("shift_seed"));
			this.getDataManager().setDirty(DATA_SHIFT_SEED);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setInteger("shift_seed", getDataManager().get(DATA_SHIFT_SEED));
	}
}
