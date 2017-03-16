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
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends EntityThrowable {

	private SpellData spell;
	private Module module;

	public EntitySpellProjectile(World worldIn) {
		super(worldIn);
	}

	public EntitySpellProjectile(World world, Module module, SpellData spell) {
		super(world);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;

		this.module = module;
		this.spell = spell;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted > 1000) {
			setDead();
			return;
		}
		if (module == null) return;

		if (module.getColor() != null) {
			// TODO: Particle side
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
			LibParticles.FAIRY_HEAD(world, getPositionVector(), module.getColor());
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

			SpellData newSpell = spell.copy();
			if (result.typeOfHit == RayTraceResult.Type.ENTITY)
				newSpell.crunchData(result.entityHit, false);
			else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				newSpell.addData(SpellData.DefaultKeys.BLOCK_HIT, result.getBlockPos());
				newSpell.addData(SpellData.DefaultKeys.TARGET_HIT, result.hitVec);
			}
			nextModule.run(newSpell);
			setDead();
			LibParticles.FAIRY_EXPLODE(world, result.hitVec, module.getColor() == null ? Color.WHITE : module.getColor());
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		NBTTagCompound moduleCompound = compound.getCompoundTag("module");
		Module module = ModuleRegistry.INSTANCE.getModule(moduleCompound.getString("id"));
		if (module != null) {
			module.deserializeNBT(compound);
			Module.process(module);
			this.module = module;
		}

		spell = new SpellData(world);
		spell.deserializeNBT(compound.getCompoundTag("spell_data"));
	}

	@Override
	public void writeEntityToNBT(@NotNull NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
	}
}
