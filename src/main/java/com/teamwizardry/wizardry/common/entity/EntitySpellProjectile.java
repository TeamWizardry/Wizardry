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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class EntitySpellProjectile extends EntityLiving {

	public SpellData spell;
	public Module module;

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

		if (module.nextModule != null) {
			Module nextModule = module.nextModule;
			if (nextModule instanceof ModuleEventAlongPath) {
				nextModule.run(spell);
			}
		}
	}

	//@Override
	//protected void onImpact(@Nonnull RayTraceResult result) {
	//	if (module != null && module.nextModule != null) {
	//		Module nextModule = module.nextModule;
//
	//		SpellData newSpell = spell.copy();
	//		if (result.typeOfHit == RayTraceResult.Type.ENTITY)
	//			newSpell.crunchData(result.entityHit, false);
	//		else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
	//			newSpell.addData(SpellData.DefaultKeys.BLOCK_HIT, result.getBlockPos());
	//			newSpell.addData(SpellData.DefaultKeys.TARGET_HIT, result.hitVec);
	//		}
	//		nextModule.run(newSpell);
	//		setDead();
	//		LibParticles.FAIRY_EXPLODE(world, result.hitVec, module.getColor() == null ? Color.WHITE : module.getColor());
	//	}
	//}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
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
	public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setTag("module", module.serializeNBT());
		compound.setTag("spell_data", spell.serializeNBT());
	}
}
