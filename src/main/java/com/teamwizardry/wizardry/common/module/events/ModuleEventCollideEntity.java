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
public class ModuleEventCollideEntity extends Module {

	public ModuleEventCollideEntity() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.BEEF);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@NotNull
	@Override
	public String getID() {
		return "on_collide_entity";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "On Collide Entity";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Triggered when the spell collides with an entity";
	}

	@NotNull
	@Override
	public ModuleEventCollideEntity copy() {
		ModuleEventCollideEntity module = new ModuleEventCollideEntity();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
