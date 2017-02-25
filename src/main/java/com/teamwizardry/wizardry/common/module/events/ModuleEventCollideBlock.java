package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;

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
	public boolean run(@NotNull SpellData spell) {
		BlockPos pos = spell.getData(BLOCK_HIT);
		return pos != null && nextModule != null && nextModule.run(spell);
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
