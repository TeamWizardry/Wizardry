package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class EntityZachrielCorruption extends EntityLiving {

	public EntityZachrielCorruption(World worldIn) {
		super(worldIn);
		setSize(6, 0.1f);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100D);
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {

	}

	@Override
	public void collideWithEntity(Entity entity) {
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entityIn) {
		entityIn.addPotionEffect(new PotionEffect(ModPotions.ZACH_CORRUPTION, 100, 1, true, false));
	}

	@Override
	protected void collideWithNearbyEntities() {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 100) setDead();

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(30, 50));
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.enableMotionCalculation();
				glitter.setCollision(true);
				glitter.setCanBounce(true);
				glitter.setAcceleration(new Vec3d(0, -0.035, 0));
				glitter.setColor(new Color(255, 0, 206));
				glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 0f));
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 1, 1, (i, build) -> {
					double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
					double r = 3.3 * RandUtil.nextFloat();
					double x = r * MathHelper.cos((float) theta);
					double z = r * MathHelper.sin((float) theta);
					build.setPositionOffset(new Vec3d(x, 0, z));
					build.addMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.15), 0));
				});
			}
		});
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
