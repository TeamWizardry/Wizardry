package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectTrace extends Module {

	public ModuleEffectTrace() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.ENDER_PEARL);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "trace";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Trace";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will short-range blink-trace you forwards";
	}

	@Override
	public double getManaToConsume() {
		return 200;
	}

	@Override
	public double getBurnoutToFill() {
		return 500;
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Entity target = spell.getData(ENTITY_HIT);

		if (caster == null) {

		} else {
			caster.posX = 0;
		}
		return super.run(spell);
	}

	@Override
	public int getCooldownTime() {
		return 3;
	}

	@Nullable
	@Override
	public Color getColor() {
		return new Color(0x7F0062);
	}

	@NotNull
	@Override
	public ModuleEffectTrace copy() {
		ModuleEffectTrace module = new ModuleEffectTrace();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
