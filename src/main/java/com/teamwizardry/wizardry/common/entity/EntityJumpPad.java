package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
public class EntityJumpPad extends EntityLiving {

	public EntityJumpPad(World worldIn) {
		super(worldIn);
		setSize(1.1F, 1.1F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (!(entity instanceof EntityLivingBase)) return;
		if (entity instanceof EntityJumpPad) return;

		((EntityLivingBase) entity).motionY += 0.35;
		entity.fallDistance = 0;
		Color color1 = new Color(
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255));
		Color color2 = new Color(
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255),
				RandUtil.nextInt(100, 255));
		Vec3d normal = new Vec3d(entity.motionX, entity.motionY, entity.motionZ).normalize().scale(1 / 2.0);

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				LibParticles.AIR_THROTTLE(world, entity.getPositionVector(), normal, color1, color2, 0.5);
			}
		});
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isDead) return;
		if (ticksExisted > 100) setDead();

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(30, 50));
				glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
				glitter.setAlphaFunction(new InterpFloatInOut(0.9f, 0.9f));
				glitter.disableMotionCalculation();
				Color color1 = new Color(
						RandUtil.nextInt(70, 255),
						RandUtil.nextInt(70, 255),
						RandUtil.nextInt(70, 255),
						RandUtil.nextInt(70, 255));
				glitter.setCollision(true);
				glitter.setColor(color1);
				glitter.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.8f, 1f), 0f));

				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 1, 1, (i, build) -> {
					double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
					double r = 1 * RandUtil.nextFloat();
					double x = r * MathHelper.cos((float) theta);
					double z = r * MathHelper.sin((float) theta);
					glitter.setPositionOffset(new Vec3d(x, 0.1, z));
					if (RandUtil.nextBoolean())
						glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.05), 0));
				});
			}
		});
	}

	@Override
	protected void playHurtSound(DamageSource source) {
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	protected void damageShield(float damage) {
	}

	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage) {
		return 0f;
	}

	@Override
	protected void damageArmor(float damage) {
	}

	@Override
	protected void dealFireDamage(int amount) {
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return false;
	}

	@Override
	public boolean canPickUpLoot() {
		return false;
	}

	@Override
	protected boolean canEquipItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
}
