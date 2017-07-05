package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeTouch extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_touch";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Touch";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Entity target = spell.getData(ENTITY_HIT);
		Vec3d targetHit = spell.getData(TARGET_HIT);
		Vec3d origin = spell.getData(ORIGIN);
		Vec3d look = spell.getData(LOOK);

		Entity finalEntity = isHead() ? (caster == null ? target : caster) : target;

		if (look == null) return true;
		if (finalEntity == null && targetHit == null && origin == null) return true;
		if (isHead() && origin == null) return true;
		if (!isHead() && targetHit == null) return true;

		RayTraceResult result = new RayTrace(
				spell.world,
				look,
				isHead() ? origin : targetHit,
				finalEntity instanceof EntityPlayerMP ?
						((EntityPlayerMP) finalEntity).interactionManager.getBlockReachDistance() : 5).setSkipEntity(finalEntity).setReturnLastUncollidableBlock(true).setIgnoreBlocksWithoutBoundingBoxes(false).trace();

		if (result == null) return true;

		Minecraft.getMinecraft().player.sendChatMessage(finalEntity == null ? "null" : finalEntity.getName() + " - " + (result.entityHit != null ? result.entityHit.getName() : "null") + "");

		spell.processBlock(result.getBlockPos(), result.sideHit, result.hitVec);

		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(targetEntity.getPositionVector().addVector(0, targetEntity.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(10, 15), (aFloat, particleBuilder) -> {
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
		return cloneModule(new ModuleShapeTouch());
	}
}
