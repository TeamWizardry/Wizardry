package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectNullGrav extends Module {

	public ModuleEffectNullGrav() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.DRAGON_BREATH);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_nullify_gravity";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Nullify Gravity";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will completely disable the target entity's gravity.";
	}

	@Override
	public double getManaToConsume() {
		return 1000;
	}

	@Override
	public double getBurnoutToFill() {
		return 500;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.WHITE;
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
		Entity target = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		if (target instanceof EntityLivingBase) {
			double length = 100;
			if (attributes.hasKey(Attributes.EXTEND))
				length *= Math.min(100, attributes.getDouble(Attributes.EXTEND) * 10);
			length *= calcBurnoutPercent(caster);
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, (int) length, 3, false, false));
			return true;
		}
		return false;

	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		LibParticles.EFFECT_NULL_GRAV(world, pos, caster, getColor());
	}

	@NotNull
	@Override
	public ModuleEffectNullGrav copy() {
		ModuleEffectNullGrav module = new ModuleEffectNullGrav();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
