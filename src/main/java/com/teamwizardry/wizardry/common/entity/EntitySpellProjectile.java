package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCast;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCollideBlock;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCollideEntity;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeProjectile;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends Entity {

	@Nullable
	private EntityLivingBase caster;
	private Vec3d slope;
	private Module spell;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;
	}

	public EntitySpellProjectile(World world, @Nullable EntityLivingBase caster, Vec3d slope, Module spell) {
		super(world);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.caster = caster;
		this.slope = slope;
		this.spell = spell;

		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null && spell.nextModule.getModuleType() == ModuleType.EVENT) {
				Module nextModule = spell.nextModule;
				if (nextModule instanceof ModuleEventCast) {
					nextModule.run(world, caster);
					if (nextModule instanceof ITargettable)
						((ITargettable) nextModule).run(world, caster, getPositionVector());
				}
			}
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		LibParticles.FAIRY_TRAIL(world, getPositionVector(), Color.RED, false, 50);
		if (ticksExisted > 50) setDead();
		if (world.isRemote) return;
		if (spell == null) return;

		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null && spell.nextModule.getModuleType() == ModuleType.EVENT) {
				Module nextModule = spell.nextModule;
				if (nextModule instanceof ModuleEventAlongPath) {
					nextModule.run(world, caster);
					if (nextModule instanceof ITargettable)
						((ITargettable) nextModule).run(world, caster, getPositionVector());
				}

				RayTraceResult cast = RaycastUtils.raycast(world, getPositionVector(), new Vec3d(motionX, motionY, motionZ), 1);

				if (cast != null) {
					if (cast.typeOfHit == RayTraceResult.Type.ENTITY && nextModule instanceof ModuleEventCollideEntity) {
						nextModule.run(world, caster);
						((ITargettable) nextModule).run(world, caster, cast.entityHit);
						setDead();
					} else if (cast.typeOfHit == RayTraceResult.Type.BLOCK && nextModule instanceof ModuleEventCollideBlock) {
						nextModule.run(world, caster);
						((ITargettable) nextModule).run(world, caster, getPositionVector());
						setDead();
					}
				}
					/*if (isCollided && nextModule instanceof ModuleEventCollideBlock) {
						nextModule.run(world, caster);
						((ITargettable) nextModule).run(world, caster, getPositionVector());
					}
					List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPosition()));
					if (!entities.isEmpty() && nextModule instanceof ModuleEventCollideEntity) {
						nextModule.run(world, caster);
						for (Entity entity : entities)
							((ITargettable) nextModule).run(world, caster, entity);
					}*/
			}
		}

		posX += motionX * 4;
		posY += motionY * 4;
		posZ += motionZ * 4;
		setPosition(posX, posY, posZ);
	}

	public void setDirection(float yaw, float pitch) {
		double speed = 0.3;
		Vec3d dir = getVectorForRotation(pitch, yaw);
		setVelocity(dir.xCoord * speed, dir.yCoord * speed, dir.zCoord * speed);
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
		Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
		if (module == null) return;
		module.deserializeNBT(compound);
		Module.process(module);
		spell = module;
	}

	@Override
	protected void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		compound.setTag("spell", spell.serializeNBT());
	}
}
