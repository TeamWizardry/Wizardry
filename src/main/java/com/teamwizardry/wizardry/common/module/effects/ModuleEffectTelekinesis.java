package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectTelekinesis extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_telekinesis";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Telekinesis";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will levitate blocks and entities in the world";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d targetPos = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		double strength = getModifierPower(spell, Attributes.POTENCY, 3, 20, true, true);

		if (targetPos == null) return false;

		List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(targetPos)).expand(strength, strength, strength));

		if (RandUtil.nextInt(6) == 0)
			spell.world.playSound(null, new BlockPos(targetPos), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat());
		for (Entity entity : entityList) {
			double dist = entity.getPositionVector().distanceTo(targetPos);
			if (dist > strength) continue;
			if (!tax(this, spell)) return false;

			final double upperMag = 1;
			final double scale = 1;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);

			Vec3d dir = targetPos.subtract(entity.getPositionVector()).normalize().scale(mag);

			entity.motionX = (dir.x);
			entity.motionY = (dir.y);
			entity.motionZ = (dir.z);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 5, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setScaleFunction(new InterpScale(1, 0));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, RandUtil.nextFloat()));
			glitter.setMotion(new Vec3d(
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.1, 0.1)
			));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectTelekinesis());
	}
}
