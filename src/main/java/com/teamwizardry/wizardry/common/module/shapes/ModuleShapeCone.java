package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeCone extends Module {

	@Nonnull
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

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_cone";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Cone";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will run the spell in a circular arc in front of the caster";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double range = 5;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(position);
		}
		int chance = 30;
		if (attributes.hasKey(Attributes.EXTEND)) range = Math.min(1, chance - attributes.getDouble(Attributes.EXTEND));

		for (int i = 0; i < range * 10; i++) {
			if (ThreadLocalRandom.current().nextInt(chance) != 0) continue;

			float newPitch = (float) (pitch + ThreadLocalRandom.current().nextDouble(-range * 6, range * 6));
			float newYaw = (float) (yaw + ThreadLocalRandom.current().nextDouble(-range * 6, range * 6));

			Vec3d target = PosUtils.vecFromRotations(newPitch, newYaw);

			SpellData newSpell = spell.copy();
			runNextModule(newSpell);
			newSpell.addData(TARGET_HIT, target);

			RayTraceResult result = Utils.raytrace(world, target.normalize(), origin, range, caster);
			if (result.entityHit != null) {
				newSpell.crunchData(result.entityHit, false);
			}
			newSpell.addData(BLOCK_HIT, result.getBlockPos());

			runNextModule(newSpell);
		}

		usedShape = this;
		return true;
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @Nonnull SpellData spell) {
		float pitch = spell.getData(PITCH, 0F), yaw = spell.getData(YAW, 0F);
		Entity caster = spell.getData(CASTER);
		Vec3d position = spell.getData(ORIGIN);
		Vec3d lookVec = PosUtils.vecFromRotations(pitch, yaw);

		if (position == null) return;

		Color color = getColor();
		if (color == null) color = Color.WHITE;
		double range = 5;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
		Vec3d origin = new Vec3d(offX, caster == null ? 0 : caster.getEyeHeight(), offZ).add(position);

		ParticleBuilder glitter = new ParticleBuilder(25);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.setScaleFunction(new InterpScale(1, 0));
		double finalRange = range;
		Color finalColor = color;
		ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(origin), (int) (range * 50), 0, (aFloat, particleBuilder) -> {
			double radius = 0.5;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0.5), z));
			glitter.setColor(new Color(
					Math.min(255, finalColor.getRed() + ThreadLocalRandom.current().nextInt(20, 50)),
					Math.min(255, finalColor.getGreen() + ThreadLocalRandom.current().nextInt(20, 50)),
					Math.min(255, finalColor.getBlue() + ThreadLocalRandom.current().nextInt(20, 50)),
					100));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.3, 1)));
			InterpCircle circle = new InterpCircle(lookVec.scale(finalRange), lookVec, (float) finalRange, 1, ThreadLocalRandom.current().nextFloat());
			glitter.setPositionFunction(new InterpLine(Vec3d.ZERO, circle.get(0)));
		});
	}

	@Nonnull
	@Override
	public ModuleShapeCone copy() {
		ModuleShapeCone module = new ModuleShapeCone();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}

	@Override
	public int getChargeUpTime() {
		return 5;
	}
}
