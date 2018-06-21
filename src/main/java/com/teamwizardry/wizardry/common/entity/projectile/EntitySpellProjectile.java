package com.teamwizardry.wizardry.common.entity.projectile;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
public class EntitySpellProjectile extends EntityMod {

	public static final DataParameter<NBTTagCompound> SPELL_DATA = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.COMPOUND_TAG);
	public static final DataParameter<NBTTagCompound> SPELL_RING = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.COMPOUND_TAG);
	public static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> GRAVITY = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> DIST = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.FLOAT);

	public EntitySpellProjectile(World world) {
		super(world);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		if (world.isRemote)
			setRenderDistanceWeight(30);
	}

	public EntitySpellProjectile(World world, SpellRing spellRing, SpellData spellData, float dist, float speed, float gravity) {
		super(world);
		setSize(0.3F, 0.3F);
		isImmuneToFire = true;

		setSpellData(spellData);
		setSpellRing(spellRing);
		setSpeed(speed);
		setDistance(dist);
		setGravity(gravity);

		if (world.isRemote)
			setRenderDistanceWeight(30);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return getEntityBoundingBox();
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(SPELL_DATA, new NBTTagCompound());
		this.getDataManager().register(SPELL_RING, new NBTTagCompound());
		this.getDataManager().register(SPEED, 0f);
		this.getDataManager().register(DIST, 0f);
		this.getDataManager().register(GRAVITY, 0f);
	}

	private SpellData getSpellData() {
		NBTTagCompound compound = getDataManager().get(SPELL_DATA);
		return SpellData.deserializeData(world, compound);
	}

	private void setSpellData(SpellData data) {
		getDataManager().set(SPELL_DATA, data.serializeNBT());
		getDataManager().setDirty(SPELL_DATA);
	}

	private SpellRing getSpellRing() {
		NBTTagCompound compound = getDataManager().get(SPELL_RING);
		return SpellRing.deserializeRing(compound);
	}

	private void setSpellRing(SpellRing ring) {
		getDataManager().set(SPELL_RING, ring.serializeNBT());
		getDataManager().setDirty(SPELL_RING);
	}

	private float getSpeed() {
		return getDataManager().get(SPEED);
	}

	private void setSpeed(float speed) {
		getDataManager().set(SPEED, speed);
		getDataManager().setDirty(SPEED);
	}

	private float getGravity() {
		return getDataManager().get(GRAVITY);
	}

	private void setGravity(float gravity) {
		getDataManager().set(GRAVITY, gravity);
		getDataManager().setDirty(GRAVITY);
	}

	private float getDistance() {
		return getDataManager().get(DIST);
	}

	private void setDistance(float dist) {
		getDataManager().set(DIST, dist);
		getDataManager().setDirty(DIST);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		SpellRing spellRing = getSpellRing();
		SpellData spellData = getSpellData();

		if (spellRing == null || spellData == null) {
			setDead();
			world.removeEntity(this);
			return;
		}


		if (world.isRemote && !isDead) {
			ClientRunnable.run(() -> {

				if (spellRing.getModule() instanceof ModuleShape)
					if (((ModuleShape) spellRing.getModule()).runRenderOverrides(spellData, spellRing))
						return;

				ParticleBuilder glitter = new ParticleBuilder(10);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
				glitter.enableMotionCalculation();
				glitter.setCollision(true);
				glitter.setCanBounce(true);
				glitter.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
				glitter.setAcceleration(new Vec3d(0, -0.015, 0));
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector().add(new Vec3d(motionX, motionY, motionZ))), 5, 0, (aFloat, particleBuilder) -> {
					particleBuilder.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(0.3, 0.8), 0));
					particleBuilder.setLifetime(RandUtil.nextInt(40, 60));
					particleBuilder.addMotion(new Vec3d(
							RandUtil.nextDouble(-0.03, 0.03),
							RandUtil.nextDouble(-0.01, 0.05),
							RandUtil.nextDouble(-0.03, 0.03)
					));
				});

				glitter.disableMotionCalculation();
				glitter.setMotion(Vec3d.ZERO);
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 5, 0, (aFloat, particleBuilder) -> {
					particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(1f, 2), 0));
					particleBuilder.setLifetime(RandUtil.nextInt(5, 10));
				});
			});
			return;
		}

		Vec3d origin = spellData.getOrigin();

		rotationPitch = spellData.getData(PITCH, 0F);
		rotationYaw = spellData.getData(YAW, 0F);
		Vec3d look = spellData.getData(LOOK);
		if (look == null) {
			setDead();
			world.removeEntity(this);
			return;
		}

		if (origin == null || getDistance() < getDistance(origin.x, origin.y, origin.z)) {
			spellData.processBlock(getPosition(), EnumFacing.getFacingFromVector((float) look.x, (float) look.y, (float) look.z), getPositionVector());
			goBoom(spellRing, spellData);
			return;
		}

		if (isDead) return;

		if (!collided) {

			float speed = getSpeed();
			// MOVE //
			motionX += ((look.x * speed) - motionX);
			motionY += ((look.y * speed) - motionY);
			motionZ += ((look.z * speed) - motionZ);

			// GRAVITY
			//if (getDistanceSq(origin.x, origin.y, origin.z) > 4)
			//motionY -= gravity;

			move(MoverType.SELF, motionX, motionY, motionZ);
		} else {

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 5).setSkipEntity(this).trace();
			spellData.processTrace(result, getPositionVector());
			goBoom(spellRing, spellData);
			return;
		}

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
		if (!entities.isEmpty()) {
			Entity caster = spellData.getCaster();

			// Don't collide with other spell projectiles
			for (Entity entity : entities) {
				if (entity == caster) return;
				if (entity instanceof EntitySpellProjectile) return;
			}

			RayTraceResult result = new RayTrace(world, look, getPositionVector(), 1).setSkipEntity(this).trace();
			spellData.processTrace(result, getPositionVector());

			goBoom(spellRing, spellData);
		}
	}

	/**
	 * Called when the projectile entity hits another block or entity, or reaches the end of its path.
	 * @param data The {@link SpellData} attached to the spell.
	 */
	protected void goBoom(SpellRing spellRing, SpellData data) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;

		if (spellRing.getChildRing() != null) {
			spellRing.getChildRing().runSpellRing(data);
		}

		PacketHandler.NETWORK.sendToAllAround(new PacketExplode(getPositionVector(), spellRing.getPrimaryColor(), spellRing.getSecondaryColor(), 0.3, 0.3, RandUtil.nextInt(30, 50), 10, 25, true),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));

		setDead();
		world.removeEntity(this);
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return super.isInRangeToRender3d(x, y, z);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		if (compound.hasKey("spell_ring")) {
			setSpellRing(SpellRing.deserializeRing(compound.getCompoundTag("spell_ring")));
		}

		if (compound.hasKey("spell_data")) {
			setSpellData(SpellData.deserializeData(world, compound.getCompoundTag("spell_data")));
		}

		if (compound.hasKey("gravity")) {
			setGravity(compound.getFloat("gravity"));
		}

		if (compound.hasKey("speed")) {
			setSpeed(compound.getFloat("speed"));
		}

		if (compound.hasKey("distance")) {
			setDistance(compound.getFloat("distance"));
		}
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {

		// Stupid Wawla, refusing to fix their problems...
		// https://github.com/micdoodle8/Galacticraft/commit/543e6afad64e51b02252a07489d0832fb93faa8d
		// https://github.com/Darkhax-Minecraft/WAWLA/issues/75
		if (world.isRemote) return;

		compound.setTag("spell_ring", getSpellRing().serializeNBT());
		compound.setTag("spell_data", getSpellRing().serializeNBT());

		compound.setDouble("distance", getDistance());
		compound.setDouble("speed", getSpeed());
		compound.setDouble("gravity", getGravity());
	}
}
