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
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(caster.getPositionVector().addVector(0, caster.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 2), 100, 15, (aFloat, particleBuilder) -> {
			if (RandUtil.nextBoolean()) {
				glitter.setColor(getPrimaryColor());
				glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.1, 0.3), RandUtil.nextDouble(-0.05, 0.05)));
			} else {
				glitter.setColor(getSecondaryColor());
				glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(-0.3, -0.1), RandUtil.nextDouble(-0.05, 0.05)));
			}
			glitter.setLifetime(RandUtil.nextInt(70, 100));
			glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			glitter.setScaleFunction(new InterpScale(1, 0));
		});

		//ParticleBuilder glitter = new ParticleBuilder(10);
		//glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		//glitter.setCollision(true);
		//glitter.setCanBounce(true);
		//glitter.disableMotionCalculation();
//
		//for (double j = 0; j < 200; j++) {
		//	double finalJ = j;
		//	ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 1, 0, (i, build) -> {
		//		double radius = 1;
		//		double theta = 2.0f * (float) Math.PI * (finalJ / 200);
		//		double r = radius;
		//		double x = r * MathHelper.cos((float) theta);
		//		double z = r * MathHelper.sin((float) theta) ;
		//		Vec3d normalize = new Vec3d(x, 0, z).normalize();
		//		double strengthSideways = 0.01;
		//		build.addMotion(normalize.scale(strengthSideways));
//
		//		build.setAlphaFunction(new InterpFadeInOut(0.0f, RandUtil.nextFloat()));
		//		build.setDeceleration(new Vec3d(RandUtil.nextDouble(0.8, 0.95), RandUtil.nextDouble(0.8, 0.95), RandUtil.nextDouble(0.8, 0.95)));
		//		build.setLifetime(RandUtil.nextInt(20, 40));
		//		build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1f), 0));
		//		build.setColorFunction(new InterpColorHSV(ColorUtils.changeColorAlpha(getPrimaryColor(), RandUtil.nextInt(50, 150)), ColorUtils.changeColorAlpha(getSecondaryColor(), RandUtil.nextInt(50, 150))));
		//	});
		//}

	}
}
