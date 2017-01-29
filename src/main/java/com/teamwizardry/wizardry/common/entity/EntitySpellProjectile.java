package com.teamwizardry.wizardry.common.entity;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends EntityThrowable {

	private EntityLivingBase caster;
	private Module spell;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
	}

	public EntitySpellProjectile(World world, EntityLivingBase caster, Module spell) {
		super(world);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.caster = caster;
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
		if (world.isRemote)
			LibParticles.FAIRY_TRAIL(world, getPositionVector(), Color.RED, false, 50);
		if (ticksExisted > 50) setDead();
		if (spell == null) return;

		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null && spell.nextModule.getModuleType() == ModuleType.EVENT) {
				Module nextModule = spell.nextModule;
				if (nextModule instanceof ModuleEventAlongPath) {
					nextModule.run(world, caster);
					if (nextModule instanceof ITargettable)
						((ITargettable) nextModule).run(world, caster, getPositionVector());
				}
			}
		}
	}

	@Override
	protected void onImpact(@NotNull RayTraceResult result) {
		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null && spell.nextModule.getModuleType() == ModuleType.EVENT) {
				Module nextModule = spell.nextModule;

				if (result.typeOfHit == RayTraceResult.Type.ENTITY && nextModule instanceof ModuleEventCollideEntity) {
					nextModule.run(world, caster);
					((ITargettable) nextModule).run(world, caster, result.entityHit);
					setDead();
				} else if (result.typeOfHit == RayTraceResult.Type.BLOCK && nextModule instanceof ModuleEventCollideBlock) {
					nextModule.run(world, caster);
					((ITargettable) nextModule).run(world, caster, getPositionVector());
					setDead();
				}
			}
		}
	}

	@Override
	public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
		if (module == null) return;
		module.deserializeNBT(compound);
		Module.process(module);
		spell = module;
	}

	@Override
	public void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("spell", spell.serializeNBT());
	}
}
