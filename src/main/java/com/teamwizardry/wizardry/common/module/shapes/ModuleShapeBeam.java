package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.common.util.ConfigPropertyDouble;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeBeam extends Module implements IContinousSpell {

	@ConfigPropertyDouble(modid = Wizardry.MODID, category = "attributes", id = "shape_beam_default_range", comment = "The default range of a pure beam spell shape", defaultValue = 10)
	public static double defaultRange;

	public ModuleShapeBeam() {
		process(this);
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(ModItems.UNICORN_HORN);
	}

	@Override
	public double getManaToConsume() {
		return 5;
	}

	@Override
	public double getBurnoutToFill() {
		return 10;
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_beam";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Beam";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell via a beam emanating from the caster";
	}

	@Override
	public boolean run(@NotNull Spell spell) {
		if (nextModule == null) return false;

		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);

		RayTraceResult trace = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), caster != null ? position.addVector(0, caster.getEyeHeight(), 0) : position, range, caster);
		if (trace == null) return false;

		setTargetPosition(this, trace.hitVec);

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			spell.crunchData(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			spell.addData(BLOCK_HIT, trace.getBlockPos());
			spell.addData(TARGET_HIT, trace.hitVec);
		}
		return nextModule.run(spell);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		if (nextModule == null) return true;

		nextModule.run(world, caster);

		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);

		if (!(caster instanceof EntityPlayer)) return false;
		RayTraceResult trace = Utils.raytrace(world, caster.getLookVec(), caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0), range, caster);

		if (trace == null) return false;
		// TODO: eventAlongPath for trace here
		setTargetPosition(this, trace.hitVec);
		if (nextModule == null) return false;
		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			return nextModule.run(world, caster, trace.entityHit);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
			return nextModule.run(world, caster, trace.hitVec);

		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (caster == null) return;
		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - caster.rotationYaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - caster.rotationYaw));
		Vec3d vec = new Vec3d(offX, caster.getEyeHeight(), offZ).add(caster.getPositionVector());

		LibParticles.SHAPE_BEAM(world, pos, vec, (int) range, getColor() == null ? Color.WHITE : getColor());
	}

	@NotNull
	@Override
	public ModuleShapeBeam copy() {
		ModuleShapeBeam module = new ModuleShapeBeam();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
