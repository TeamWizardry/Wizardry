package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeSelf extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_self";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Self";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(DefaultKeys.CASTER);
		if (caster == null) return false;

		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(targetEntity.getPositionVector().addVector(0, targetEntity.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(5, 10), (aFloat, particleBuilder) -> {
			if (RandUtil.nextBoolean()) {
				glitter.setColor(getPrimaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.1), 0));
			} else {
				glitter.setColor(getSecondaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(-0.1, -0.01), 0));
			}
			glitter.setLifetime(RandUtil.nextInt(20, 30));
			glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			glitter.setScaleFunction(new InterpScale(1, 0));
		});

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeSelf());
	}
}
