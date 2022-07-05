package com.teamwizardry.wizardry.common.module.effects;

/**
 * Created by Demoniaque.
 */
/*
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
*/