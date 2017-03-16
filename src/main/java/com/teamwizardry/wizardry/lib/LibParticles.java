package com.teamwizardry.wizardry.lib;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.InterpScale;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class LibParticles {

	public static void FIZZING_AMBIENT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(0.05, 0.1), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
		});
	}

	public static void FIZZING_ITEM(World world, Vec3d pos) {
		ParticleBuilder fizz = new ParticleBuilder(10);
		fizz.setScale(0.3f);
		fizz.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		fizz.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(fizz, world, new StaticInterp<>(pos.addVector(0, 0.5, 0)), 10, 0, (aFloat, particleBuilder) -> {
			fizz.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			fizz.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			fizz.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.005, 0.005), ThreadLocalRandom.current().nextDouble(0.04, 0.08), ThreadLocalRandom.current().nextDouble(-0.005, 0.005)));
		});
	}

	public static void FIZZING_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 300, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(100, 150)));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5)));
		});
	}

	public static void DEVIL_DUST_BIG_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(ThreadLocalRandom.current().nextFloat());
		glitter.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, builder) -> {
			Vec3d offset = new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
			glitter.setPositionOffset(offset);
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01), ThreadLocalRandom.current().nextDouble(0.04, 0.06), ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
		});
	}

	public static void DEVIL_DUST_SMALL_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0).darker());
		glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0, 0.5));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, builder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03), ThreadLocalRandom.current().nextDouble(0.07, 0.2), ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
		});
	}

	public static void BOOK_BEAM_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 1.0), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(0, 50));
		});
	}

	public static void BOOK_BEAM_HELIX(World world, Vec3d pos) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 30, 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1.0, 255.0), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void BOOK_LARGE_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(1000);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1000, 0, (i, build) -> {

			double radius = 1.0;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 255.0), 0));
			glitter.setMotion(new Vec3d(x, 0, z));
			glitter.setJitter(10, new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(-0.05, -0.01), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
			glitter.enableMotionCalculation();
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(70, 170)));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.5));
		});
	}

	public static void AIR_THROTTLE(World world, Vec3d pos, Entity collided, Color color1, Color color2, double scatter, boolean enableCollision) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
		glitter.enableMotionCalculation();
		Color color3 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), ThreadLocalRandom.current().nextInt(50, 100));
		Color color4 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), ThreadLocalRandom.current().nextInt(50, 100));
		glitter.setCollision(enableCollision);
		ParticleSpawner.spawn(glitter, world, new InterpLine(pos, pos.addVector(collided.posX - collided.prevPosX, collided.posY - collided.prevPosY, collided.posZ - collided.prevPosZ)), ThreadLocalRandom.current().nextInt(50, 80), 1, (i, build) -> {
			glitter.setMotion(new Vec3d(collided.motionX + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), (collided.motionY / 2.0) + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), collided.motionZ + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
			if (ThreadLocalRandom.current().nextBoolean()) glitter.setColor(color3);
			else glitter.setColor(color4);
			if (scatter > 0) {
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = scatter * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-scatter, scatter), z));
			}
		});
	}

	public static void AIR_THROTTLE(World world, Vec3d pos, Vec3d normal, Color color1, Color color2, double scatter, boolean enableCollision) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
		glitter.enableMotionCalculation();
		Color color3 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), ThreadLocalRandom.current().nextInt(50, 100));
		Color color4 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), ThreadLocalRandom.current().nextInt(50, 100));
		glitter.setCollision(enableCollision);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(50, 80), 1, (i, build) -> {
			glitter.setMotion(normal);
			if (ThreadLocalRandom.current().nextBoolean()) glitter.setColor(color3);
			else glitter.setColor(color4);
			if (scatter > 0) {
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = scatter * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-scatter, scatter), z));
			}
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_FAR(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.15;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColorFunction(new InterpColorHSV(Color.RED, 50, 20.0F));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_CLOSE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 20, 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColor(Color.RED);
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 0.03), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (i, build) -> {
			double radius = 0.1;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 40));
			glitter.setColor(new Color(0x4DFFFFFF, true));
			glitter.setScaleFunction(new InterpScale(0, (float) ThreadLocalRandom.current().nextDouble(3, 4)));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.2), z));
			if (ThreadLocalRandom.current().nextInt(15) == 0)
				glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
						ThreadLocalRandom.current().nextDouble(0, 0.03),
						ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
		});
	}

	public static void HALLOWED_SPIRIT_HURT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(100, 150));
		glitter.setColorFunction(new InterpColorHSV(Color.BLUE, 50, 20.0F));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(40, 100), 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
			glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void FAIRY_TRAIL(World world, Vec3d pos, Color color, boolean sad, int age) {
		if (((age / 4) >= (age / 2)) || (age == 0)) return;
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(age / 4, age / 2));
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 10), 0, (i, build) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			if (sad) {
				glitter.setCollision(true);
				glitter.enableMotionCalculation();
			}
		});
	}

	public static void FAIRY_HEAD(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(3);
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));
		glitter.setScale(3.5f);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 2);

		ParticleBuilder glitter2 = new ParticleBuilder(5);
		glitter2.setColor(color);
		glitter2.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter2.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));
		glitter2.setScale(1);
		ParticleSpawner.spawn(glitter2, world, new StaticInterp<>(pos), 3);
	}

	public static void FAIRY_EXPLODE(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(100, 200), 0, (i, build) -> {
			double radius = ThreadLocalRandom.current().nextDouble(1, 5);
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setMotion(new Vec3d(x / 2, ThreadLocalRandom.current().nextDouble(-5, 5) / 2, z / 2));
			glitter.setAlphaFunction(new InterpFadeInOut(0.0f, ThreadLocalRandom.current().nextFloat()));
			glitter.setColor(Utils.shiftColorHueRandomly(Utils.changeColorAlpha(color, ThreadLocalRandom.current().nextInt(100, 200)), 30).brighter());
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(100, 300));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
		});
	}

	public static void STRUCTURE_FLAIR(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(10, 30), 0, (i, build) -> {
			double radius = ThreadLocalRandom.current().nextDouble(1, 2);
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setMotion(new Vec3d(x / 5, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z / 5));
			glitter.setAlphaFunction(new InterpFadeInOut(0.0f, ThreadLocalRandom.current().nextFloat()));
			glitter.setColor(Utils.shiftColorHueRandomly(Utils.changeColorAlpha(color, ThreadLocalRandom.current().nextInt(180, 255)), 30));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(50, 80));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
		});
	}

	public static void STRUCTURE_BEACON(World world, Vec3d pos, Color color) {
		ParticleBuilder beacon = new ParticleBuilder(10);
		beacon.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(beacon, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(10, 30), 0, (i, build) -> {
			beacon.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), 0));
			beacon.setAlphaFunction(new InterpFadeInOut(0.1f, ThreadLocalRandom.current().nextFloat()));
			beacon.setColor(Utils.shiftColorHueRandomly(Utils.changeColorAlpha(color, 255), 30));
			beacon.setLifetime(ThreadLocalRandom.current().nextInt(50, 80));
			beacon.setScale(ThreadLocalRandom.current().nextFloat());
		});
	}

	public static void CRAFTING_ALTAR_HELIX(World world, Vec3d pos) {
		ParticleBuilder beam = new ParticleBuilder(200);
		beam.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		beam.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));

		ParticleSpawner.spawn(beam, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			beam.setScale(ThreadLocalRandom.current().nextFloat());
			beam.setColor(new Color(ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(150, 255)).brighter().brighter());
			beam.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.1, 0.8), 0));
			beam.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});

		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 170)));
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1.0, 255.0), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void CLUSTER_DRAPE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(200);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.enableMotionCalculation();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 10), 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(50, 255)));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(30, 60));
		});
	}

	public static void MAGIC_DOT(World world, Vec3d pos, float scale) {
		ParticleBuilder glitter = new ParticleBuilder(3);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(50, 255)));
			if (scale == -1) glitter.setScale(ThreadLocalRandom.current().nextFloat());
			else {
				glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.3), 0));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10));
				glitter.setScale(scale);
			}
		});
	}

	public static void CRAFTING_ALTAR_CLUSTER_SUCTION(World world, Vec3d pos, InterpFunction<Vec3d> bezier3D) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(1, 3), 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(10, 255)));
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setPositionFunction(bezier3D);
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void CRAFTING_ALTAR_PEARL_EXPLODE(World world, Vec3d pos) {
		ParticleBuilder builder = new ParticleBuilder(1);
		builder.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));
		builder.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(builder, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
			builder.setColorFunction(new InterpColorFade(new Color(ThreadLocalRandom.current().nextInt(0, 20), ThreadLocalRandom.current().nextInt(100, 255), ThreadLocalRandom.current().nextInt(0, 20)), 1, 255, 1));
			double radius = 0.1;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z));
			builder.setScale(ThreadLocalRandom.current().nextFloat());
			builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03),
					ThreadLocalRandom.current().nextDouble(-0.03, 0.03),
					ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
			builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
		});
	}

	public static void COLORFUL_BATTERY_BEZIER(World world, BlockPos pedestal, BlockPos center) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 0.1f));

		Vec3d sub = new Vec3d(center).addVector(0.5, 0.5, 0.5).subtract(new Vec3d(pedestal).addVector(0.5, 0.5, 0.5));

		Matrix4 matrix = new Matrix4();
		InterpBezier3D bezier3D = new InterpBezier3D(Vec3d.ZERO, sub,
				matrix.rotate(Math.toRadians(90), new Vec3d(0, 1, 0)).apply(sub).addVector(0, 5, 0),
				matrix.rotate(Math.toRadians(-90), new Vec3d(0, 1, 0)).apply(sub));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pedestal).addVector(0.5, 0.5, 0.5)), 3, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(0, ThreadLocalRandom.current().nextInt(30, 100), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(10, 255)));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setPositionFunction(bezier3D);
		});
	}

	public static void SHAPE_BEAM(World world, Vec3d target, Vec3d origin, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(1.0f, 1.0f, 1.0f, 0.1f));
		glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, target.subtract(origin), 0.0f, 0.15f, 1.0F, 0));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		glitter.setColor(new Color(
				Math.min(255, color.getRed() + ThreadLocalRandom.current().nextInt(5, 20)),
				Math.min(255, color.getGreen() + ThreadLocalRandom.current().nextInt(5, 20)),
				Math.min(255, color.getBlue() + ThreadLocalRandom.current().nextInt(5, 20)),
				color.getAlpha()));

		ParticleSpawner.spawn(glitter, world, new InterpHelix(target.subtract(origin), origin, 0.0f, 0.15f, 1.0f, 0), (int) origin.distanceTo(target), 0, (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		});
	}

	public static void EFFECT_NULL_GRAV(World world, @NotNull Vec3d pos, @Nullable EntityLivingBase caster, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
		glitter.setColor(color == null ? Color.WHITE : color);
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 10), ThreadLocalRandom.current().nextInt(0, 30), (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			if (ThreadLocalRandom.current().nextBoolean())
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, caster == null ? 1 / 2 : caster.height / 2, 0),
						new Vec3d(0, caster == null ? -1 : -caster.height, 0),
						1f, 0f, 1f, ThreadLocalRandom.current().nextFloat()));
			else glitter.setPositionFunction(new InterpHelix(
					new Vec3d(0, caster == null ? 1 / 2 : caster.height / 2, 0),
					new Vec3d(0, caster == null ? 1.5 : caster.height + 0.5, 0),
					1f, 0f, 1f, ThreadLocalRandom.current().nextFloat()));
		});
	}

	public static void EFFECT_REGENERATE(World world, @NotNull Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColor(Utils.changeColorAlpha(color, ThreadLocalRandom.current().nextInt(200, 255)));
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 20, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 40));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, ThreadLocalRandom.current().nextFloat()));

			double radius = 1;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextDouble(-1, 1), z);
			glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, dest, dest.scale(2), new Vec3d(dest.xCoord, ThreadLocalRandom.current().nextDouble(-2, 2), dest.zCoord)));
		});
	}

	public static void EFFECT_BURN(World world, @NotNull Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(3);
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 4, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(Utils.changeColorAlpha(color, ThreadLocalRandom.current().nextInt(200, 255)));

			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			glitter.setScaleFunction(new InterpScale((float) ThreadLocalRandom.current().nextDouble(3, 10), 0f));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, ThreadLocalRandom.current().nextFloat()));
			glitter.addMotion(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.05, 0.05),
					ThreadLocalRandom.current().nextDouble(0.05),
					ThreadLocalRandom.current().nextDouble(-0.05, 0.05)
			));
			glitter.setPositionOffset(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.3, 0.3),
					ThreadLocalRandom.current().nextDouble(-0.3, 0.3),
					ThreadLocalRandom.current().nextDouble(-0.3, 0.3)
			));
		});


		ParticleBuilder dust = new ParticleBuilder(3);
		dust.setScale(1);
		dust.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(dust, world, new StaticInterp<>(pos), 3, 0, (aFloat, particleBuilder) -> {

			dust.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			dust.setScaleFunction(new InterpScale(3f, 0.5f));
			dust.setAlphaFunction(new InterpFadeInOut(1, 1));
			dust.setColor(Color.DARK_GRAY);
			dust.addMotion(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.05, 0.05),
					ThreadLocalRandom.current().nextDouble(0.05),
					ThreadLocalRandom.current().nextDouble(-0.05, 0.05)
			));
		});

		//ParticleBuilder dust = new ParticleBuilder(3);
		//dust.setScale(1);
		//dust.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
//
		//ParticleSpawner.spawn(dust, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
		//	dust.setColor(Color.DARK_GRAY);
		//	dust.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
		//	dust.setScale(1);
		//	dust.setScaleFunction(new InterpFadeInOut(0, 0.9f));
		//	//dust.setAlphaFunction(new InterpFadeInOut(0.3f, ThreadLocalRandom.current().nextFloat()));
		//	double x = ThreadLocalRandom.current().nextDouble(-4, 4),
		//			z = ThreadLocalRandom.current().nextDouble(-4, 4);
		//	dust.setPositionFunction(new InterpBezier3D(Vec3d.ZERO,
		//			new Vec3d(x, ThreadLocalRandom.current().nextDouble(4), z),
		//			new Vec3d(x, -5, z), new Vec3d(0, 1, 0)));
//
		//	//double radius = 3;
		//	//double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
		//	//double r = radius * ThreadLocalRandom.current().nextFloat();
		//	//double x = r * MathHelper.cos((float) theta);
		//	//double z = r * MathHelper.sin((float) theta);
		//	//Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextDouble(-1, 1), z);
		//	//glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, dest,
		//	//		new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 1), 0),
		//	//		new Vec3d(0, ThreadLocalRandom.current().nextDouble(-2, 0), 0)));
		//});
	}

	public static void STRUCTURE_BOUNDS(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10));
		glitter.setScale(ThreadLocalRandom.current().nextFloat());
		glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.9F, 0.9F));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> glitter.setColor(Utils.shiftColorHueRandomly(Utils.changeColorAlpha(color, ThreadLocalRandom.current().nextInt(50, 200)), 100)));
	}
}
