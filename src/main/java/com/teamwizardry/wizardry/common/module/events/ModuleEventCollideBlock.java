package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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

	@NotNull
	@Override
	public ModuleEventCollideBlock copy() {
		ModuleEventCollideBlock module = new ModuleEventCollideBlock();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
