package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectVanish extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_vanish";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Vanish";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Invisibility to everyone when still, invisibility from everyone when moving";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getData(CASTER);
		Entity targetEntity = spell.getData(ENTITY_HIT);

		double range = getModifierPower(spell, Attributes.INCREASE_AOE, 1, 64, true, true);
		double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 1, 64, true, true);
		range = 32;

		if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.VANISH, 100, 0, true, false));
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectVanish());
	}

}
