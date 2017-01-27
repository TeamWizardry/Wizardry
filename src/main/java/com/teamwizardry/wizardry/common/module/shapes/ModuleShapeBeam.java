package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.util.ConfigPropertyDouble;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Attributes;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.spell.IContinousSpell;
import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCast;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class ModuleShapeBeam extends Module implements IContinousSpell {

	@ConfigPropertyDouble(modid = Wizardry.MODID, category = "modules", id = "shape_beam_default_range", comment = "The default range of a pure beam spell shape", defaultValue = 10)
	public static double defaultRange;

	public ModuleShapeBeam() {
		process();
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
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		if (nextModule == null) return false;

		if (nextModule instanceof ModuleEventCast) nextModule.run(world, caster);

		RayTraceResult trace = null;
		if (caster != null) {
			IWizardryCapability cap = getCap(caster);
			if (cap != null) {
				double range = 10;
				if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
				trace = caster.rayTrace(range, ClientTickHandler.getPartialTicks());
			}
		}

		if (trace == null) {
			return false;
		} else {
			// TODO: eventAlongPath for trace here

			if (nextModule.getModuleType() == ModuleType.EVENT)
				if (trace.typeOfHit == RayTraceResult.Type.ENTITY) {
					if (nextModule instanceof ITargettable)
						((ITargettable) nextModule).run(world, caster, trace.entityHit);
				} else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
					if (nextModule instanceof ITargettable)
						((ITargettable) nextModule).run(world, caster, trace.hitVec);

			return true;
		}
	}

	@Override
	public void runClient(@NotNull World world, @NotNull ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (caster == null) return;
		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - caster.rotationYaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - caster.rotationYaw));
		Vec3d vec = new Vec3d(offX, caster.getEyeHeight(), offZ).add(caster.getPositionVector());

		LibParticles.SHAPE_BEAM(world, caster.getPositionVector().add(caster.getLook(0).scale(range)), vec, caster.getLook(1.0F).scale(-1.0), (int) range, getColor() == null ? Color.WHITE : getColor());
	}

	@NotNull
	@Override
	public ModuleShapeBeam copy() {
		ModuleShapeBeam module = new ModuleShapeBeam();
		module.deserializeNBT(serializeNBT());
		module.process();
		return module;
	}
}
