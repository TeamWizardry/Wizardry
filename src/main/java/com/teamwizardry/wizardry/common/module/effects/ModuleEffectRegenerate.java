package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectRegenerate extends Module implements IContinousSpell {

	public ModuleEffectRegenerate() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.REDSTONE_BLOCK);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "regenerate";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Regenerate";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will heal entities & fix explosion blasts";
	}

	@Override
	public double getManaToConsume() {
		return 20;
	}

	@Override
	public double getBurnoutToFill() {
		return 30;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.RED;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
		if (!(target instanceof EntityLivingBase)) return false;
		double strength = 0.3;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(20.0 / 10.0, attributes.getDouble(Attributes.EXTEND) / 10.0);
		if (caster != null && getCap(caster) != null)
			strength *= calcBurnoutPercent(getCap(caster));

		((EntityLivingBase) target).setHealth((float) (((EntityLivingBase) target).getHealth() + strength));
		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (ThreadLocalRandom.current().nextInt(15) != 0) return;

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColor(Utils.changeColorAlpha(Utils.shiftColorHueRandomly(getColor(), 20), ThreadLocalRandom.current().nextInt(200, 255)));
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos.addVector(0, 1, 0)), 20, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 40));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, ThreadLocalRandom.current().nextFloat()));
			Vec3d dest = new Vec3d(
					ThreadLocalRandom.current().nextDouble(-1, 1),
					ThreadLocalRandom.current().nextDouble(-1, 1),
					ThreadLocalRandom.current().nextDouble(-1, 1));
			glitter.setPositionFunction(new InterpBezier3D(
					Vec3d.ZERO,
					dest,
					dest.scale(2),
					new Vec3d(dest.xCoord, ThreadLocalRandom.current().nextDouble(-2, 2), dest.zCoord)));
		});
	}

	@NotNull
	@Override
	public ModuleEffectRegenerate copy() {
		ModuleEffectRegenerate module = new ModuleEffectRegenerate();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
