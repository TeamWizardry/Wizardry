package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
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
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_bouncing")
public class ModuleEffectBouncing implements IModuleEffect, ILingeringModule {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean runOnce(ModuleInstance instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim();
		World world = spell.world;
		BlockPos pos = spell.getTargetPos();

		if (pos == null) return true;

		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell, true)) return false;

		world.playSound(null, pos, ModSounds.SLIME_SQUISHING, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.6f, 1f), RandUtil.nextFloat(0.5f, 1f));
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.BOUNCING, (int) time, 0, true, false));
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

		if (ClientTickHandler.getTicks() % 10 == 0) return;
		if (!(target instanceof EntityLivingBase)) return;
		if (!((EntityLivingBase) target).isPotionActive(ModPotions.BOUNCING)) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		InterpCircle circle = new InterpCircle(Vec3d.ZERO, new Vec3d(0, 1, 0), target.width, RandUtil.nextFloat(), RandUtil.nextFloat());
		for (Vec3d origin : circle.list(30)) {
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(target.getPositionVector()), 1, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setLifetime(RandUtil.nextInt(20, 25));
				particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(1f, 1.5f), 0f));
				particleBuilder.setAlphaFunction(new InterpFloatInOut(aFloat, aFloat));

				particleBuilder.setMotion(origin.normalize().scale(1.0 / 5.0));
				particleBuilder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.01, 0.01), 0));
			});
		}
	}

	@Override
	public int getLingeringTime(SpellData spell, SpellRing spellRing) {
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
		return (int) time;
	}
}
