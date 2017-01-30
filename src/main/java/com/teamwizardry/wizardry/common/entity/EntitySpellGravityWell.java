package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class EntitySpellGravityWell extends Entity {

	@Nullable
	private EntityLivingBase caster;
	private boolean antigrav;
	private Vec3d pos;
	private int maxTicks;
	private double range;

	public EntitySpellGravityWell(World worldIn) {
		super(worldIn);
	}

	public EntitySpellGravityWell(World world, @Nullable EntityLivingBase caster, Vec3d pos, int maxTicks, double range, boolean antigrav) {
		super(world);
		this.pos = pos;
		this.maxTicks = maxTicks;
		this.range = range;
		this.caster = caster;
		this.antigrav = antigrav;

		setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (pos == null) {
			setDead();
			return;
		}

		LibParticles.EFFECT_NULL_GRAV(world, pos, null, antigrav ? ModuleRegistry.INSTANCE.getModule("effect_anti_gravity_well").getColor() : ModuleRegistry.INSTANCE.getModule("effect_gravity_well").getColor());
		if (ticksExisted > maxTicks) setDead();
		if (world.isRemote) return;

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(pos)).expand(range, range, range))) {
			if (entity == null) continue;
			if (entity.getDistanceToEntity(this) > range) continue;

			if (!antigrav) {
				Vec3d dir = pos.subtract(entity.getPositionVector());
				entity.motionX += (dir.xCoord) / 50.0;
				entity.motionY += (dir.yCoord) / 50.0;
				entity.motionZ += (dir.zCoord) / 50.0;
				entity.fallDistance = 0;
				entity.velocityChanged = true;
			} else {
				Vec3d dir = pos.subtract(entity.getPositionVector());
				entity.motionX += (range - dir.xCoord) / 50.0;
				entity.motionY += (range - dir.yCoord) / 50.0;
				entity.motionZ += (range - dir.zCoord) / 50.0;
				entity.fallDistance = 0;
				entity.velocityChanged = true;
			}
		}
	}

	@Override
	public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(@NotNull NBTTagCompound compound) {
		pos = new Vec3d(compound.getDouble("x_coord"), compound.getDouble("y_coord"), compound.getDouble("z_coord"));
		maxTicks = compound.getInteger("max_ticks");
		range = compound.getDouble("range");
		antigrav = compound.getBoolean("anti_grav");

		if (compound.hasKey("caster_id"))
			caster = (EntityLivingBase) world.getEntityByID(compound.getInteger("caster_id"));
	}

	@Override
	protected void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		compound.setDouble("x_coord", pos.xCoord);
		compound.setDouble("y_coord", pos.yCoord);
		compound.setDouble("z_coord", pos.zCoord);
		compound.setInteger("max_ticks", maxTicks);
		compound.setDouble("range", range);
		compound.setBoolean("anti_grav", antigrav);
		if (caster != null) compound.setInteger("caster_id", caster.getEntityId());
	}
}
