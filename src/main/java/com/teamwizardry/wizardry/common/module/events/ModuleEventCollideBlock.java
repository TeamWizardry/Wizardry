package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventCollideBlock extends Module {

	public ModuleEventCollideBlock() {
		process(this);
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.COAL);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@NotNull
	@Override
	public String getID() {
		return "on_collide_block";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "On Collide Block";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Triggered when the spell collides with a block";
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		return super.run(world, caster);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target) {
		return nextModule != null && nextModule.run(world, caster, target);
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		return false;
	}

	@NotNull
	@Override
	public ModuleEventCollideBlock copy() {
		ModuleEventCollideBlock module = new ModuleEventCollideBlock();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
