package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCast;
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
public class ModuleShapeTouch extends Module {

	public ModuleShapeTouch() {
		process();
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.RABBIT_FOOT);
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
		if (nextModule == null) return false;

		if (nextModule instanceof ModuleEventCast) return nextModule.run(world, caster);
		else if (nextModule instanceof ITargettable) {
			if (caster instanceof EntityPlayerMP) {
				double range = ((EntityPlayerMP) caster).interactionManager.getBlockReachDistance();
				RayTraceResult trace = caster.rayTrace(range, ClientTickHandler.getPartialTicks());
				if (trace == null) return false;
				if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
					return ((ITargettable) nextModule).run(world, caster, trace.hitVec);
				else if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
					return ((ITargettable) nextModule).run(world, caster, trace.entityHit);
			}
		}
		return false;
	}

	@NotNull
	@Override
	public ModuleShapeTouch copy() {
		ModuleShapeTouch module = new ModuleShapeTouch();
		module.deserializeNBT(serializeNBT());
		module.process();
		return module;
	}
}
