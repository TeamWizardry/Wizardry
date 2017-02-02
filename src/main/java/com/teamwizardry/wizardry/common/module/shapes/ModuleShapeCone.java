package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.Attributes;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
public class ModuleShapeCone extends Module {

	public ModuleShapeCone() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.GUNPOWDER);
	}

	@Override
	public double getManaToConsume() {
		return 50;
	}

	@Override
	public double getBurnoutToFill() {
		return 80;
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_cone";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Cone";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell in a circular arc in front of the caster";
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {

		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
		if (caster == null) return;
		//if (getColor() == null) return;
		double range = 5;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - caster.rotationYaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - caster.rotationYaw));
		Vec3d origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(caster.getPositionVector());

		ParticleBuilder glitter = new ParticleBuilder(25);
		glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, caster.getLookVec().scale(range), 0.0f, (float) range, 3f, ThreadLocalRandom.current().nextFloat()));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(origin), 50, (int) (5 * range), (aFloat, particleBuilder) -> {
			double radius = 0.5;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0.5), z));
			glitter.setColor(new Color(
					Math.min(255, Color.RED.getRed() + ThreadLocalRandom.current().nextInt(20, 50)),
					Math.min(255, Color.RED.getGreen() + ThreadLocalRandom.current().nextInt(20, 50)),
					Math.min(255, Color.RED.getBlue() + ThreadLocalRandom.current().nextInt(20, 50)),
					Color.RED.getAlpha()));
			glitter.setScale(1);
		});
	}

	@NotNull
	@Override
	public ModuleShapeCone copy() {
		ModuleShapeCone module = new ModuleShapeCone();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
