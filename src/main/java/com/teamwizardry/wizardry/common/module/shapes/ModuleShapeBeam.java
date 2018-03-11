package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtendRange;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleShapeBeam extends ModuleShape implements IContinuousModule {

	@Nonnull
	@Override
	public String getID() {
		return "shape_beam";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierExtendRange(), new ModuleModifierIncreasePotency()};
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d look = spell.getData(LOOK);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		float strength = spell.getData(STRENGTH, 1f);

		if (position == null || look == null) return false;

		double range = getModifier(spell, Attributes.RANGE, 10, 100);
		double potency = 30 - getModifier(spell, Attributes.POTENCY, 0, 25);

		//boolean hasBeenOverriden = false;
		//for (Module child : getAllChildRings()) {
		//	if (child.overrideShapeRun(this, spell)) {
		//		return ((nextModule == null || nextModule instanceof IContinuousModule) || spell.world.getTotalWorldTime() % (int) potency == 0) && runNextModule(spell);
		//	}
		//}

		RayTraceResult trace = new RayTrace(world, look, position, range)
				.setSkipEntity(caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		Vec3d vec = trace.hitVec == null ? look.scale(range) : trace.hitVec;

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			spell.processEntity(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
			spell.processBlock(trace.getBlockPos(), trace.sideHit, trace.hitVec);
		else spell.processBlock(new BlockPos(vec), null, vec);

		return ((nextModule == null || nextModule instanceof IContinuousModule) || spell.world.getTotalWorldTime() % (int) potency == 0) && runNextModule(spell);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @NotNull SpellRing spellRing) {
		for (Module child : getAllChildModules()) {
			if (child.overrideShapeRunClient(this, spell)) {
				return;
			}
		}

		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		Vec3d target = spell.getData(TARGET_HIT);

		if (position == null) return;
		if (target == null) return;

		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, 0, offZ).add(position);
		}
		LibParticles.SHAPE_BEAM(world, target, origin, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeBeam());
	}
}
