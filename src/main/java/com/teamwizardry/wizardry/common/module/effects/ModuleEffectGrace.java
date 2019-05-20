package com.teamwizardry.wizardry.common.module.effects;

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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_grace")
public class ModuleEffectGrace implements IModuleEffect, ILingeringModule {
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean runOnce(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim(world);
		BlockPos pos = spell.getTargetPos();

		if (pos == null) return true;

		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(world, spell, true)) return false;

		world.playSound(null, pos, ModSounds.GRACE, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.6f, 1f), RandUtil.nextFloat(0.5f, 1f));
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.GRACE, (int) time, 0, true, false));
		}

		return true;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity target = spell.getVictim(world);

		if (!(target instanceof EntityLivingBase)) return;
		if (!((EntityLivingBase) target).isPotionActive(ModPotions.GRACE)) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();

		ParticleSpawner.spawn(glitter, world, new InterpCircle(target.getPositionVector().add(0, target.height / 2.0, 0), new Vec3d(Math.cos(world.getTotalWorldTime() / 10.0), 1, Math.cos(world.getTotalWorldTime() / 10.0)), 2), 15, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setLifetime(RandUtil.nextInt(20, 25));
			particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(1f, 1.5f), 0f));
			particleBuilder.setAlphaFunction(new InterpFloatInOut(aFloat, aFloat));
		});
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
		return (int) time;
	}
}
