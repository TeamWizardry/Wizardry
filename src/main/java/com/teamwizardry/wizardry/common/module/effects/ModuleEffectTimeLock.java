package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.spell.module.IModuleModifier;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by Demoniaque.
 */
//TODO: @RegisterModule
public class ModuleEffectTimeLock implements IModuleEffect, IDelayedModule {

	@Nonnull
	@Override
	public String getClassID() {
		return "effect_time_lock";
	}

	@Override
	public IModuleModifier[] applicableModifiers() {
		return new IModuleModifier[]{new ModuleModifierIncreaseDuration()};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity targetEntity = spell.getVictim();

		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell);

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity != null) {
			targetEntity.setFire((int) time);
			world.playSound(null, targetEntity.getPosition(), ModSounds.FIRE, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.35f, 0.75f), RandUtil.nextFloat(0.35f, 1.5f));
		}
		return true;
	}

	@Override
	public void runDelayedEffect(SpellData spell, SpellRing spellRing) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		Color color = instance.getPrimaryColor();
		if (RandUtil.nextBoolean()) color = instance.getSecondaryColor();

		LibParticles.EFFECT_BURN(world, position, color);
	}
}
