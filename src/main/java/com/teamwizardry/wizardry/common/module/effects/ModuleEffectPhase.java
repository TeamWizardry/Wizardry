package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectPhase extends Module implements ITaxing {

	public ModuleEffectPhase() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_phase";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Phase";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will short-range blink-trace you forwards";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Vec3d targetHit = spell.getData(TARGET_HIT);

		double strength = 10 * getMultiplier();
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(50, attributes.getDouble(Attributes.EXTEND));

		if (!tax(this, spell)) return false;

		strength *= calcBurnoutPercent(caster);

		if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) targetEntity;
			entity.addPotionEffect(new PotionEffect(ModPotions.PHASE, (int) strength, 1, true, false));
			entity.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, (int) strength, 1, true, false));
			entity.addPotionEffect(new PotionEffect(ModPotions.PUSH, 1, 1, true, false));
			//entity.addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, (int) strength, 1, true, false));
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectPhase());
	}
}
