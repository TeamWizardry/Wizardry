package com.teamwizardry.wizardry.lib;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class LibParticles {

	public static void FIZZING_AMBIENT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(0.1, 0.15), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
		});
	}

	public static void FIZZING_ITEM(World world, Vec3d pos) {
		ParticleBuilder fizz = new ParticleBuilder(10);
		fizz.setScale(0.3f);
		fizz.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(fizz, world, new StaticInterp<>(pos.addVector(0, 0.5, 0)), 10, 0, (aFloat, particleBuilder) -> {
			fizz.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			fizz.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			fizz.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.005, 0.005), ThreadLocalRandom.current().nextDouble(0.04, 0.08), ThreadLocalRandom.current().nextDouble(-0.005, 0.005)));
		});
	}

	public static void FIZZING_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
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
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
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
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, builder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03), ThreadLocalRandom.current().nextDouble(0.07, 0.2), ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
		});
	}

	public static void BOOK_BEAM_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 1), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(0, 50));
		});
	}

	public static void BOOK_BEAM_HELIX(World world, Vec3d pos) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 30, 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1, 255), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void BOOK_LARGE_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(1000);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1000, 0, (i, build) -> {

			double radius = 1;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 255), 0));
			glitter.setMotion(new Vec3d(x, 0, z));
			glitter.setJitter(10, new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(-0.05, -0.01), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(70, 170)));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.5));
		});
	}

	public static void HALLOWED_SPIRIT_AIR_THROTTLE(World world, Vec3d pos, Entity collided) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setColor(Color.WHITE);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));
		glitter.setAlphaFunction(new InterpFadeInOut(0.4f, 0.4f));
		
		ParticleSpawner.spawn(glitter, world, new InterpLine(pos, pos.addVector(collided.posX-collided.prevPosX, collided.posY-collided.prevPosY, collided.posZ-collided.prevPosZ)), ThreadLocalRandom.current().nextInt(30, 50), 1, (i, build) -> {
			glitter.setMotion(new Vec3d(collided.motionX + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), collided.motionY / 2 + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), collided.motionZ + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
//			glitter.disableMotion();
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_FAR(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.15;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			glitter.setColorFunction(new InterpColorHSV(Color.RED, 50, 20));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_CLOSE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.2;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			glitter.setColor(Color.RED);
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.15;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			glitter.setColor(new Color(0x4DFFFFFF, true));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
		});
	}

	public static void HALLOWED_SPIRIT_HURT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(100, 150));
		glitter.setColorFunction(new InterpColorHSV(Color.BLUE, 50, 20));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(40, 100), 0, (i, build) -> {
			double radius = 0.2;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
			glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void FAIRY_TRAIL(World world, Vec3d pos, Color color, boolean sad, int age) {
		if (age / 4 >= age / 2 || age == 0) return;
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(age / 4, age / 2));
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 10), 0, (i, build) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			if (sad) glitter.enableMotionCalculation();
		});
	}

	public static void FAIRY_EXPLODE(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setColor(color.darker());
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(50, 100), 0, (i, build) -> {
			double radius = 0.5;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);
			glitter.setMotion(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.5), z));
		});
	}

	public static void MODULE_BEAM(World world, Vec3d target, Vec3d origin, Vec3d reverseNormal, int distance) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(1f, 1f, 1f, 0.1f));
		glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, reverseNormal, 0.15f, 0.15f, 1, 0));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
		ParticleSpawner.spawn(glitter, world, new InterpLine(target, origin), distance, 0, (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
		});
	}
}
