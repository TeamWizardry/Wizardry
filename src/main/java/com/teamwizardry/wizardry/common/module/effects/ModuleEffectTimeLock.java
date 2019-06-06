package com.teamwizardry.wizardry.common.module.effects;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.core.WizardryNemezManager;
import com.teamwizardry.wizardry.common.core.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_time_lock")
public class ModuleEffectTimeLock implements IModuleEffect, IDelayedModule {

	public static HashMultimap<UUID, UUID> timeLockedEntities = HashMultimap.create();

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);
		if (caster == null) return true;

		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell);

		if (!spellRing.taxCaster(world, spell, true)) return false;

		NemezTracker nemezDrive = WizardryNemezManager.getOrCreateNemezDrive(world, caster);

		if (targetEntity instanceof EntityLivingBase) {
			timeLockedEntities.put(caster.getUniqueID(), targetEntity.getUniqueID());
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.TIME_LOCK, (int) time, 1, false, false));
			world.playSound(null, targetEntity.getPosition(), ModSounds.SOUND_BOMB, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.35f, 0.75f), RandUtil.nextFloat(0.35f, 1.5f));
		}

		addDelayedSpell(world, spellRing, spell, (int) time);
		return true;
	}

	@Override
	public void runDelayedEffect(@Nonnull World world, SpellData spell, SpellRing spellRing) {
		Entity caster = spell.getCaster(world);
		if (caster == null) return;

		BlockPos targetPos = spell.getTargetPos();
		if (targetPos == null) return;

		NemezTracker nemezDrive = WizardryNemezManager.getAndRemoveNemezDrive(world, caster);

		if (nemezDrive != null) {
			nemezDrive.endUpdate();
			nemezDrive.collapse();

			NemezEventHandler.reverseTime(world, nemezDrive, targetPos);
		}

		timeLockedEntities.removeAll(caster.getUniqueID());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		Color color = instance.getPrimaryColor();
		if (RandUtil.nextBoolean()) color = instance.getSecondaryColor();

		LibParticles.EFFECT_BURN(world, position, color);
	}
}
