package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectLeech extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_leech";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreasePotency()};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity targetEntity = spell.getVictim();
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster();
		EnumFacing facing = spell.getData(FACE_HIT);

		double strength = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 2;

		if (!spellRing.taxCaster(spell)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			if (targetEntity instanceof EntityPlayer) {

				double targetMana = new CapManager(targetEntity).getMana();

				targetEntity.attackEntityFrom(DamageSource.MAGIC, (float) strength);
				if (targetEntity.isDead) {
					targetMana /= 2;
					targetMana = MathHelper.clamp(targetMana, targetMana, spellRing.getManaDrain() * 2);
					new CapManager(caster).addMana(targetMana);
				}

			} else if (targetEntity instanceof EntityWitch) {

				double targetMana = spellRing.getManaDrain() * 2;

				targetEntity.attackEntityFrom(DamageSource.MAGIC, (float) strength);
				if (targetEntity.isDead) {
					new CapManager(caster).addMana(targetMana);
				}

			} else {
				if (caster instanceof EntityLivingBase) ((EntityLivingBase) caster).setLastAttackedEntity(targetEntity);

				if (caster != null)
					targetEntity.attackEntityFrom(new EntityDamageSource("magic", caster).setDamageBypassesArmor().setMagicDamage(), (float) strength);
				else
					targetEntity.attackEntityFrom(DamageSource.MAGIC, (float) strength);

			}
		}

		Vec3d target = spell.getTargetWithFallback();
		if (target != null)
			world.playSound(null, new BlockPos(target), ModSounds.CHAINY_ZAP, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.35f, 0.75f), RandUtil.nextFloat(0.35f, 1.5f));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		glitter.enableMotionCalculation();
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.035), 0));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 80, 0, (i, builder) -> {
			builder.setLifetime(RandUtil.nextInt(30, 60));
			builder.addMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.01, 0.02), RandUtil.nextDouble(-0.05, 0.05)));
			builder.setScale((float) RandUtil.nextDouble(0.3, 0.5));
			builder.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));
			builder.setColor(RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
		});
	}
}
