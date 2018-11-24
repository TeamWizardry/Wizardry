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
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegisterModule(ID="effect_suffocate")
public class ModuleEffectSuffocate implements IModuleEffect {

	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim();

		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.SUFFOCATE, (int) time));

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// TODO: EffectSuffocate Particles
//		World world = spell.world;
//		Vec3d position = spell.getTarget();
//
//		if (position == null) return;
//
//		ParticleBuilder glitter = new ParticleBuilder(1);
//		glitter.setAlphaFunction(new InterpFloatInOut(0.0f, 0.1f));
//		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
//		glitter.enableMotionCalculation();
//		glitter.setScaleFunction(new InterpScale(1, 0));
//		glitter.setAcceleration(new Vec3d(0, -0.02, 0));
//		glitter.setCollision(true);
//		glitter.setCanBounce(true);
//		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(5, 15), 0, (aFloat, particleBuilder) -> {
//			double radius = 2;
//			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
//			double r = radius * RandUtil.nextFloat();
//			double x = r * MathHelper.cos((float) theta);
//			double z = r * MathHelper.sin((float) theta);
//			glitter.setScale(RandUtil.nextFloat());
//			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-2, 2), z));
//			glitter.setLifetime(RandUtil.nextInt(50, 100));
//			Vec3d direction = position.add(glitter.getPositionOffset()).subtract(position).normalize().scale(1 / 5);
//			glitter.addMotion(direction.scale(RandUtil.nextDouble(0.5, 1)));
//		});

	}
}
