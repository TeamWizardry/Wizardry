package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@RegisterModule(ID="effect_shatter")
public class ModuleEffectShatter implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		Entity caster = spell.getCaster(world);
		BlockPos pos = spell.getTargetPos();

		if (pos == null) return false;

		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell) / 2;

		if (!spellRing.taxCaster(world, spell, true)) return false;

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

			world.playSound(null, pos, ModSounds.MARBLE_EXPLOSION, SoundCategory.NEUTRAL, 2, RandUtil.nextFloat(0.8f, 1.2f));
			world.playSound(null, pos, ModSounds.FIREWORK, SoundCategory.NEUTRAL, 2, RandUtil.nextFloat(0.8f, 1.2f));
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
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		glitter.disableRandom();
		glitter.setDeceleration(new Vec3d(0.5, 0.5, 0.5));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 100, 0, (i, builder) -> {
			builder.setLifetime(RandUtil.nextInt(30, 60));
			builder.setScale((float) RandUtil.nextDouble(0.3, 0.5));
			builder.setAlphaFunction(new InterpFloatInOut(0.0f, 0.3f));
			builder.setColor(RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
			builder.setAcceleration(new Vec3d(0, 0.001, 0));

			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			builder.setMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z).normalize().scale(RandUtil.nextFloat(2)));

			builder.setTick(particle -> {
				if (particle.getAge() > 15) {

					particle.setAcceleration(new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(0.01, 0.03), RandUtil.nextDouble(-0.01, 0.01)));
					particle.setJitterChance(1);
					particle.setJitterMagnitude(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), 0, RandUtil.nextDouble(-0.05, 0.05)));
				}
			});
		});
	}
}
