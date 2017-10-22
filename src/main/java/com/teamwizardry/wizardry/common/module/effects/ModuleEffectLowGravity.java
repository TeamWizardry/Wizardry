package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtendTime;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLowGravity extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_low_gravity";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Low Gravity";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "The target becomes lightweight, falling slowly, jumping higher, and walking on water";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreasePotency(), new ModuleModifierExtendTime()};
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity targetEntity = spell.getData(ENTITY_HIT);
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getData(CASTER);

		double potency = getModifier(spell, Attributes.POTENCY, 2, 16);
		double time = getModifier(spell, Attributes.DURATION, 50, 1000);

		if (!tax(this, spell)) return false;

		if (targetEntity != null) {
			spell.world.playSound(null, targetEntity.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, 1);
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.LOW_GRAVITY, (int) time, (int) potency, true, false));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.1f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.enableMotionCalculation();
		glitter.setScaleFunction(new InterpScale(1, 0));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(5, 15), 0, (aFloat, particleBuilder) -> {
			double radius = 2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setScale(RandUtil.nextFloat());
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-2, 2), z));
			glitter.setLifetime(RandUtil.nextInt(30, 40));
			Vec3d direction = position.add(glitter.getPositionOffset()).subtract(position).normalize();
			glitter.setMotion(direction.scale(RandUtil.nextDouble(0.5, 1.3)));
		});

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectLowGravity());
	}
}
