package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_decay")
public class ModuleEffectDecay implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", /*"modifier_increase_aoe", */"modifier_extend_time"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
//		World world = spell.world;
		Entity targetEntity = spell.getVictim();
		BlockPos targetPos = spell.getTargetPos();
//		Entity caster = spell.getCaster();

		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 5;
//		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2;
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			EntityLivingBase target = (EntityLivingBase) targetEntity;
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) time, (int) potency));
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) time, (int) potency));
		}

		if (targetPos != null) {
			// TODO: Add block decay
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 500, 0, (i, build) -> {
			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.1f, 1f), 1f));
			build.setMotion(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z).normalize().scale(1.5 * RandUtil.nextFloat()));
			build.setAcceleration(Vec3d.ZERO);
			build.setLifetime(50);
			build.setDeceleration(new Vec3d(0.7, 0.7, 0.7));
			build.setAlphaFunction(new InterpFloatInOut(0f, 0.1f));

			build.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));

			build.setTick(particle -> {
				if (particle.getAge() > 15) {

					particle.setAcceleration(new Vec3d(0, -0.015, 0));
					particle.setJitterChance(1);
					particle.setJitterMagnitude(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), 0, RandUtil.nextDouble(-0.05, 0.05)));
				}
			});
		});
	}
}
