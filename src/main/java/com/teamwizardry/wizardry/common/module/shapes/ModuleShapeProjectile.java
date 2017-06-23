package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeProjectile extends ModuleShape {

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
		World world = spell.world;
		if (world.isRemote) return false;

		Vec3d target = spell.getData(TARGET_HIT);
		Vec3d origin = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		if (origin == null) return false;
		//	if (caster == null) return false;
//		if (!tax(this, spell)) return false;

		//float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - caster.rotationYaw));
		//float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - caster.rotationYaw));
		//Vec3d origin = new Vec3d(offX, caster.getEyeHeight() - 0.3, offZ).add(caster.getPositionVector());

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell);
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
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
