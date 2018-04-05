package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleShapeSelf extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_self";
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRunOverrides(spell, spellRing)) return true;

		Entity caster = spell.getCaster();
		if (caster == null) return false;

		if (!spellRing.taxCaster(spell)) return false;
		spell.processEntity(caster, false);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRenderOverrides(spell, spellRing)) return;

		Entity caster = spell.getCaster();

		if (caster == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(caster.getPositionVector().addVector(0, caster.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(10, 15), (aFloat, particleBuilder) -> {
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
}
