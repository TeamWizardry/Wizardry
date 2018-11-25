package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_grace")
public class ModuleEffectGrace implements IModuleEffect, ILingeringModule {
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean runOnce(ModuleInstance instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim();
		World world = spell.world;

		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.GRACE, (int) time));
		}

		return true;
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity target = spell.getVictim();

		if (target == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.setCanBounce(true);

		Vec3d motion;
		if (target.motionX != 0 || target.motionY != 0 || target.motionZ != 0)
			motion = new Vec3d(target.motionX, target.motionY, target.motionZ);
		else motion = new Vec3d(0, 1, 0);

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(target.getPositionVector().add(0, target.height / 2.0, 0)), 20, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setLifetime(RandUtil.nextInt(15, 20));
			particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1f), 0f));
			particleBuilder.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			InterpCircle circle = new InterpCircle(Vec3d.ZERO, motion.normalize(), 2, 1f, RandUtil.nextFloat(0, 1));
			particleBuilder.setMotion(circle.get(aFloat).scale(0.1));
		});
	}

	@Override
	public int getLingeringTime(SpellData spell, SpellRing spellRing) {
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
		return (int) time;
	}
}
