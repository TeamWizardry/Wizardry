package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCast;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ModuleShapeSelf extends Module {

	public ModuleShapeSelf() {
		process();
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.DIAMOND);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_self";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Self";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell on the caster";
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		if (nextModule == null) return false;

		if (nextModule instanceof ModuleEventCast) return nextModule.run(world, caster);
		else if (nextModule instanceof ITargettable) return ((ITargettable) nextModule).run(world, caster, caster);

		return false;
	}

	@NotNull
	@Override
	public ModuleShapeSelf copy() {
		ModuleShapeSelf module = new ModuleShapeSelf();
		module.deserializeNBT(serializeNBT());
		module.process();
		return module;
	}
}
