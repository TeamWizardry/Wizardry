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
public class ModuleEffectPhase extends Module {

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

		int strength = 10;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(50, attributes.getDouble(Attributes.EXTEND));

		if (!processCost(strength / 10.0, spell)) return false;

		strength *= calcBurnoutPercent(caster);

		if (caster != null && targetEntity != null) {
			if (caster.getUniqueID().equals(targetEntity.getUniqueID())) {
				if (caster instanceof EntityLivingBase) {
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PHASE, strength, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PUSH, 1, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, strength, 1, true, false));
				}
			} else {
				if (caster instanceof EntityLivingBase) {
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PHASE, strength, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PUSH, 1, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, strength, 1, true, false));
				}
			}
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
