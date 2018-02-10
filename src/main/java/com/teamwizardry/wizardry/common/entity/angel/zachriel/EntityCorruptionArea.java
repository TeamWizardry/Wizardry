package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class EntityCorruptionArea extends EntityMod {
	private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(EntityCorruptionArea.class, DataSerializers.FLOAT);

	@Save
	public final HashMap<Entity, Integer> reapplicationDelayMap;
	@Save
	public final int reapplicationDelay;

	@Save
	public int duration;
	@Save
	public float radiusPerTick;

	public EntityCorruptionArea(World world) {
		super(world);
		this.reapplicationDelayMap = new HashMap<>();
		this.duration = 600;
		this.reapplicationDelay = 20;
		this.noClip = true;
		this.setNoGravity(true);
		this.setEntityInvulnerable(true);
		this.isImmuneToFire = true;
	}

	public EntityCorruptionArea(World world, double x, double y, double z) {
		this(world);
		this.setPosition(x, y, z);
	}

	public EntityCorruptionArea(World world, float radius) {
		this(world);
		this.setRadius(radius);
	}

	public EntityCorruptionArea(World world, float radius, double x, double y, double z) {
		this(world, x, y, z);
		this.setRadius(radius);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		while (y > 0 && world.isAirBlock(new BlockPos(x, y - 1, z)))
			y -= 1;
		super.setPosition(x, y, z);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(RADIUS, Float.valueOf(3));
		this.setSize(getRadius() * 2, 1);
	}

	public float getRadius() {
		return this.getDataManager().get(RADIUS);
	}

	public void setRadius(float radius) {
		if (radius < 0) radius = 0;
		this.setSize(radius * 2, 0.5F);
		if (!world.isRemote)
			this.getDataManager().set(RADIUS, radius);
		this.setSize(radius * 2, 1);
	}

	public void onUpdate() {
		super.onUpdate();
		float radius = this.getRadius();
		if (duration < 0) setDead();
		duration--;

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
					double r = getRadius() * RandUtil.nextFloat();
					double x = r * MathHelper.cos((float) theta);
					double z = r * MathHelper.sin((float) theta);
					build.setPositionOffset(new Vec3d(x, 0, z));
					build.addMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.15), 0));
				});
			}
		});

		radius += radiusPerTick;
		setRadius(radius);
		Iterator<Entry<Entity, Integer>> iter = this.reapplicationDelayMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Entity, Integer> entry = iter.next();
			int timeLeft = entry.getValue();
			if (timeLeft > 0)
				entry.setValue(timeLeft - 1);
			else
				iter.remove();
		}

		List<EntityLivingBase> entityList = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());
		for (EntityLivingBase entity : entityList) {
			if (entity instanceof EntityZachriel)
				continue;

			if (this.reapplicationDelayMap.containsKey(entity))
				continue;

			double xDiff = entity.posX - this.posX;
			double zDiff = entity.posY - this.posY;
			if (xDiff * xDiff + zDiff * zDiff <= radius * radius)
				affectEntity(entity);
		}
	}

	public void affectEntity(EntityLivingBase entity) {
		this.reapplicationDelayMap.put(entity, reapplicationDelay);
		PotionEffect effect = entity.getActivePotionEffect(ModPotions.ZACH_CORRUPTION);
		if (effect == null)
			entity.addPotionEffect(new PotionEffect(ModPotions.ZACH_CORRUPTION, 100, 0, true, false));
		else
			entity.addPotionEffect(new PotionEffect(ModPotions.ZACH_CORRUPTION, 100, effect.getAmplifier() + 1, true, false));
	}

	@Override
	public EnumPushReaction getPushReaction() {
		return EnumPushReaction.IGNORE;
	}
}
