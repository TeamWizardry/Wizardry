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
import com.teamwizardry.wizardry.api.spell.Spell;
import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
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

	private Spell spell;
	private Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
	}

	public EntitySpellProjectile(World world, Module module, Spell spell) {
		super(world);
		this.spell = spell;
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.module = module;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 500) setDead();
		if (module == null) return;
		List<EntitySpellProjectile> projectiles = world.getEntitiesWithinAABB(EntitySpellProjectile.class, new AxisAlignedBB(getPosition()).expand(0.5, 0.5, 0.5));
		if (!projectiles.isEmpty())
			for (EntitySpellProjectile projectile : projectiles)
				if (projectile != null && projectile.module != null && module != null)
					if (projectile.module.equals(module) && getEntityId() % 2 == 0) setDead();

		if (module.getColor() != null) {
			ParticleBuilder glitter = new ParticleBuilder(10);
			glitter.setColor(new Color(1.0f, 1.0f, 1.0f, 0.1f));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColor(module.getColor());

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 10, 0, (aFloat, particleBuilder) -> {
				glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, getLook(0), 0.3f, 0.3f, 1F, ThreadLocalRandom.current().nextFloat()));
			});
		}

		if (module.nextModule != null) {
			Module nextModule = module.nextModule;
			if (nextModule instanceof ModuleEventAlongPath) {
				nextModule.run(spell);
			}
		}
	}

	@Override
	protected void onImpact(@NotNull RayTraceResult result) {
		if (module != null && module.nextModule != null) {
			Module nextModule = module.nextModule;

			Spell newSpell = new Spell(world);
			newSpell.addData(Spell.DefaultKeys.ORIGIN, spell.getData(Spell.DefaultKeys.ORIGIN));
			newSpell.addData(Spell.DefaultKeys.CASTER, spell.getData(Spell.DefaultKeys.CASTER));
			if (result.typeOfHit == RayTraceResult.Type.ENTITY)
				newSpell.crunchData(result.entityHit, false);
			else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				newSpell.addData(Spell.DefaultKeys.BLOCK_HIT, result.getBlockPos());
				newSpell.addData(Spell.DefaultKeys.TARGET_HIT, result.hitVec);
			}
			nextModule.run(newSpell);
			setDead();
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
		if (module == null) return;
		module.deserializeNBT(compound);
		Module.process(module);
		this.module = module;
	}

	@Override
	public void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("module", module.serializeNBT());
	}
}
