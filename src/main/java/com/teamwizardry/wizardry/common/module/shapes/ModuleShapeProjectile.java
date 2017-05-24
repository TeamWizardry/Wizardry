package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeProjectile extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Projectile";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will launch the spell as a projectile in the direction the caster is looking.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		if (nextModule == null) return true;

		World world = spell.world;
		if (world.isRemote) return false;

		Entity caster = spell.getData(CASTER);
		if (caster == null) return false;

		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - caster.rotationYaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - caster.rotationYaw));
		Vec3d origin = new Vec3d(offX, caster.getEyeHeight() - 0.3, offZ).add(caster.getPositionVector());

		if (!processCost(spell)) return false;

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell);
		proj.setPosition(origin.xCoord, origin.yCoord, origin.zCoord);
		proj.velocityChanged = true;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, caster.getPosition(), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) ThreadLocalRandom.current().nextDouble(1, 1.5));
		return success;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeProjectile());
	}
}
