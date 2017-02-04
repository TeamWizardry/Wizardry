package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 500) setDead();
		if (spell == null) return;
		List<EntitySpellProjectile> projectiles = world.getEntitiesWithinAABB(EntitySpellProjectile.class, new AxisAlignedBB(getPosition()).expand(0.5, 0.5, 0.5));
		if (!projectiles.isEmpty())
			for (EntitySpellProjectile projectile : projectiles)
				if (projectile.spell.equals(spell) && getEntityId() % 2 == 0) setDead();

		if (spell.getColor() != null) {
			ParticleBuilder glitter = new ParticleBuilder(10);
			glitter.setColor(new Color(1.0f, 1.0f, 1.0f, 0.1f));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColor(spell.getColor());

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 10, 0, (aFloat, particleBuilder) -> {
				glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, getLook(0), 0.3f, 0.3f, 1F, ThreadLocalRandom.current().nextFloat()));
			});
		}

		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null) {
				Module nextModule = spell.nextModule;
				if (nextModule instanceof ModuleEventAlongPath) {
					nextModule.run(world, caster);
					nextModule.run(world, caster, getPositionVector());
				}
			}
		}
	}

	@Override
	protected void onImpact(@NotNull RayTraceResult result) {
		if (spell instanceof ModuleShapeProjectile) {
			if (spell.nextModule != null && spell.nextModule.getModuleType() == ModuleType.EVENT) {
				Module nextModule = spell.nextModule;

				nextModule.run(world, caster);
				if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
					nextModule.run(world, caster, result.entityHit);
				} else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					nextModule.run(world, caster, getPositionVector());
				}
				setDead();
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
