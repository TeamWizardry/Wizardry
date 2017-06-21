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
public class ModuleEffectCrasherFall extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_crasher_fall";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Crasher Fall";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will distribute a percentage of the target's fall damage into any nearby entities absorbing the impact";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity instanceof EntityLivingBase) {
			double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 2, 20, false, true);
			double duration = getModifierPower(spell, Attributes.EXTEND_TIME, 5, 64, false, true) * 10;
			if (!tax(this, spell)) return false;

			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.CRASH, (int) duration, (int) strength, true, false));
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
		return cloneModule(new ModuleEffectCrasherFall());
	}
}
