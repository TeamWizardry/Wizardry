package com.teamwizardry.wizardry.common.module.events;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEventCollideBlock extends Module {

	public ModuleEventCollideBlock() {
		process(this);
	}

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.COAL);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EVENT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "on_collide_block";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "On Collide Block";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Triggered when the spell collides with a block";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		BlockPos pos = spell.getData(BLOCK_HIT);
		return pos != null && nextModule != null && nextModule.run(spell);
	}

	@Nonnull
	@Override
	public ModuleEventCollideBlock copy() {
		ModuleEventCollideBlock module = new ModuleEventCollideBlock();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
