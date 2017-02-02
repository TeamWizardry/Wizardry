package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeTouch extends Module {

	public ModuleShapeTouch() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.EGG);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_touch";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Touch";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell on the block hit";
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		if (nextModule == null) return true;
		if (caster == null) return true;

		nextModule.run(world, caster);

		double range = 5;
		if (caster instanceof EntityPlayerMP)
			range = ((EntityPlayerMP) caster).interactionManager.getBlockReachDistance();
		RayTraceResult result = Utils.raytrace(world, caster.getLookVec(), caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0), range, caster);
		if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
			return nextModule.run(world, caster, result.hitVec);
		else if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY)
			return nextModule.run(world, caster, result.entityHit);

		return false;
	}

	@NotNull
	@Override
	public ModuleShapeTouch copy() {
		ModuleShapeTouch module = new ModuleShapeTouch();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
