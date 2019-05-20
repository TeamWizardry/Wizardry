package com.teamwizardry.wizardry.common.entity.projectile;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectLightning;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

public class EntityLightningProjectile extends EntitySpellProjectile {
	public static final DataParameter<NBTTagCompound> CHILD_RING = EntityDataManager.createKey(EntityLightningProjectile.class, DataSerializers.COMPOUND_TAG);

	public EntityLightningProjectile(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(CHILD_RING, new NBTTagCompound());
	}

	public EntityLightningProjectile(World world, SpellRing spellRing, SpellRing childRing, SpellData spellData, float dist, float speed, float gravity) {
		super(world, spellRing, spellData, dist, speed, gravity, true);
		setChildRing(childRing);
	}

	protected SpellRing getChildRing() {
		NBTTagCompound compound = getDataManager().get(CHILD_RING);
		return SpellRing.deserializeRing(compound);
	}

	protected void setChildRing(SpellRing ring) {
		getDataManager().set(CHILD_RING, ring.serializeNBT());
		getDataManager().setDirty(CHILD_RING);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isDead) return;

		SpellData data = getSpellData();
		SpellRing spellRing = getSpellRing();
		SpellRing childRing = getChildRing();

		double range = childRing.getAttributeValue(world, AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(world, AttributeRegistry.POTENCY, data);
		double duration = childRing.getAttributeValue(world, AttributeRegistry.DURATION, data);
		double maxPotency = childRing.getModule() != null ? childRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY).max : 0;

		if (data == null || spellRing == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		if (world.isRemote) {
			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient() {

					ParticleBuilder glitter = new ParticleBuilder(10);
					glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
					glitter.enableMotionCalculation();
					glitter.setCollision(true);
					glitter.setCanBounce(true);
					glitter.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
					ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector().add(new Vec3d(motionX, motionY, motionZ))), 10, 0, (aFloat, particleBuilder) -> {
						particleBuilder.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(0.3, 0.8), 0));
						particleBuilder.setLifetime(RandUtil.nextInt(30, 40));
						particleBuilder.setMotion(new Vec3d(
								RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(-0.01, 0.01)));
						particleBuilder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(0.0005, 0.003), 0));
					});

					glitter.disableMotionCalculation();
					glitter.setMotion(Vec3d.ZERO);
					ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 2, 0, (aFloat, particleBuilder) -> {
						particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(2f, 3), 0));
						particleBuilder.setLifetime(RandUtil.nextInt(5, 10));
					});
				}
			});
		} else {
			Vec3d dir = data.getData(LOOK);
			if (dir == null) return;

			RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));

			float u = rand.nextFloat();
			float v = rand.nextFloat();
			float pitch = (float) (180 * Math.acos(2 * u - 1) / Math.PI);
			float yaw = (float) (2 * Math.PI * v);

			Vec3d to = dir.rotatePitch(pitch).rotateYaw(yaw).normalize().scale(rand.nextDouble(maxPotency * 5) < potency ? range : 1.0 / 2.0).add(getPositionVector());

			ModuleEffectLightning.doLightning(rand.nextLong(100, 100000), world, data.getCaster(world), getPositionVector(), to, range, potency, duration);
		}
	}

	@Override
	protected void goBoom(SpellRing spellRing, SpellData data) {
		SpellRing childRing = getChildRing();
		if (childRing == null || childRing.getModule() == null) {
			return;
		}

		double range = childRing.getAttributeValue(world, AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(world, AttributeRegistry.POTENCY, data);
		double duration = childRing.getAttributeValue(world, AttributeRegistry.DURATION, data);
		AttributeRange potencyRange = childRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY);
		Vec3d origin = this.getPositionVector();
		Entity caster = data.getCaster(world);

		for (int i = 0; i < potency; i += ((int) potencyRange.min >> 2)) {
			RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
			Vec3d dir = PosUtils.vecFromRotations(rand.nextFloat(0, 180), rand.nextFloat(0, 360));
			Vec3d pos = dir.scale(range).add(origin);

			ModuleEffectLightning.doLightning(rand.nextLong(100, 100000), world, caster, origin, pos, range, potency, duration);
		}

		super.goBoom(spellRing, data);
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);

		if (compound.hasKey("child_ring")) {
			setChildRing(SpellRing.deserializeRing(compound.getCompoundTag("child_ring")));
		}
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		super.writeCustomNBT(compound);

		// Stupid Wawla, refusing to fix their problems...
		// https://github.com/micdoodle8/Galacticraft/commit/543e6afad64e51b02252a07489d0832fb93faa8d
		// https://github.com/Darkhax-Minecraft/WAWLA/issues/75
		if (world.isRemote) return;

		compound.setTag("child_ring", getChildRing().serializeNBT());
	}
}
