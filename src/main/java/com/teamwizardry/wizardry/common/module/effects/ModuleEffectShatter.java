package com.teamwizardry.wizardry.common.module.effects;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.init.ModPotions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegisterModule(ID="effect_shatter")
public class ModuleEffectShatter implements IModuleEffect {

	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_increase_potency"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim();
		Entity caster = spell.getCaster();

		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 2;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			
			PotionEffect slowness = ((EntityLivingBase) targetEntity).getActivePotionEffect(MobEffects.SLOWNESS);
			PotionEffect frost = ((EntityLivingBase) targetEntity).getActivePotionEffect(ModPotions.SLIPPERY);
			int mult = 0;
			if (slowness != null)
				mult += slowness.getAmplifier() + 1;
			if (frost != null)
				mult += frost.getAmplifier() + 1;
			potency *= 1 + mult * 0.5;
			
			int invTime = targetEntity.hurtResistantTime;
			targetEntity.hurtResistantTime = 0;
			if (caster instanceof EntityLivingBase)
			{
				((EntityLivingBase) caster).setLastAttackedEntity(targetEntity);
				if (caster instanceof EntityPlayer)
					targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster).setMagicDamage(), (float) potency);
				else
					targetEntity.attackEntityFrom(new DamageSource("generic").setMagicDamage(), (float) potency);
			}
			else
				targetEntity.attackEntityFrom(new DamageSource("generic").setMagicDamage(), (float) potency);
			
			targetEntity.hurtResistantTime = invTime;
		}

		// TODO: EffectShatter Sound
//		Vec3d target = spell.getTargetWithFallback();
//		if (target != null)
//			world.playSound(null, new BlockPos(target), ModSounds.CHAINY_ZAP, SoundCategory.NEUTRAL, 0.5f, 1f);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// TODO: EffectShatter Particles
//		World world = spell.world;
//		Vec3d position = spell.getTarget();
//
//		if (position == null) return;
//
//		ParticleBuilder glitter = new ParticleBuilder(10);
//		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
//		glitter.setCollision(true);
//		glitter.setCanBounce(true);
//		glitter.enableMotionCalculation();
//		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.035), 0));
//
//		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 80, 0, (i, builder) -> {
//			builder.setLifetime(RandUtil.nextInt(30, 60));
//			builder.addMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.01, 0.02), RandUtil.nextDouble(-0.05, 0.05)));
//			builder.setScale((float) RandUtil.nextDouble(0.3, 0.5));
//			builder.setAlphaFunction(new InterpFloatInOut(0.0f, 0.3f));
//			builder.setColor(RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
//		});
	}
}
