package com.teamwizardry.wizardry.client.fx;

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Demoniaque on 8/29/2016.
 */
public class LibParticles {

	private static int beamTick = 0;

	static {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				beamTick++;
				if (beamTick > 100) beamTick = 0;
			}
		}, 0, 1);
	}

	public static void FIZZING_AMBIENT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos.x + RandUtil.nextDouble(-0.5, 0.5), pos.y + RandUtil.nextDouble(-0.5, 0.5), pos.z + RandUtil.nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
			glitter.setLifetime(RandUtil.nextInt(20, 30));
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.05, 0.1), RandUtil.nextDouble(-0.05, 0.05)));
		});
	}

	public static void FIZZING_ITEM(World world, Vec3d pos) {
		ParticleBuilder fizz = new ParticleBuilder(10);
		fizz.setScale(0.3f);
		fizz.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		fizz.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(fizz, world, new StaticInterp<>(pos.addVector(0, 0.5, 0)), 10, 0, (aFloat, particleBuilder) -> {
			fizz.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
			fizz.setLifetime(RandUtil.nextInt(20, 30));
			fizz.setPositionOffset(new Vec3d(
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.1, 0.1)
			));
			fizz.setMotion(new Vec3d(RandUtil.nextDouble(-0.005, 0.005), RandUtil.nextDouble(0.04, 0.08), RandUtil.nextDouble(-0.005, 0.005)));
		});
	}

	public static void FIZZING_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 300, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(20, 30));
			glitter.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5)));
		});
	}

	public static void DEVIL_DUST_BIG_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(RandUtil.nextFloat());
		glitter.setColor(new Color(RandUtil.nextFloat(), 0, 0));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, builder) -> {
			Vec3d offset = new Vec3d(RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5));
			glitter.setPositionOffset(offset);
			glitter.setLifetime(RandUtil.nextInt(30, 50));
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(0.04, 0.06), RandUtil.nextDouble(-0.01, 0.01)));
		});
	}

	public static void DEVIL_DUST_SMALL_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(RandUtil.nextFloat(), 0, 0).darker());
		glitter.setScale((float) RandUtil.nextDouble(0, 0.5));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, builder) -> {
			glitter.setLifetime(RandUtil.nextInt(10, 30));
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.03, 0.03), RandUtil.nextDouble(0.07, 0.2), RandUtil.nextDouble(-0.03, 0.03)));
		});
	}

	public static void BOOK_BEAM_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.02, 0.02), RandUtil.nextDouble(0, 1.0), RandUtil.nextDouble(-0.02, 0.02)));
			glitter.setColor(new Color(255, 255, 255, RandUtil.nextInt(0, 255)));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setLifetime(RandUtil.nextInt(0, 50));
		});
	}

	public static void BOOK_BEAM_HELIX(World world, Vec3d pos) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 30, 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(255, 255, 255, RandUtil.nextInt(0, 255)));
			helix.setScale(RandUtil.nextFloat());
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, RandUtil.nextDouble(1.0, 255.0), 0), 0, RandUtil.nextInt(1, 5), RandUtil.nextInt(1, 5), 0));
			helix.setLifetime(RandUtil.nextInt(0, 200));
		});
	}

	public static void BOOK_LARGE_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(1000);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1000, 0, (i, build) -> {

			double radius = 1.0;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(0, RandUtil.nextDouble(0, 255.0), 0));
			glitter.setMotion(new Vec3d(x, 0, z));
			glitter.setJitter(10, new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(-0.05, -0.01), RandUtil.nextDouble(-0.05, 0.05)));
			glitter.enableMotionCalculation();
			glitter.setColor(new Color(255, 255, 255, RandUtil.nextInt(70, 170)));
			glitter.setScale((float) RandUtil.nextDouble(0.3, 0.5));
		});
	}

	public static void AIR_THROTTLE(World world, Vec3d pos, Vec3d normal, Color color1, Color color2, double scatter) {
		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(30, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
		glitter.enableMotionCalculation();
		glitter.setCollision(true);
		glitter.setAcceleration(new Vec3d(0, -0.01, 0));
		glitter.setScaleFunction(new InterpScale(1, 0));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), RandUtil.nextInt(40, 50), 1, (i, build) -> {
			glitter.setMotion(new Vec3d(normal.x + RandUtil.nextDouble(-0.01, 0.01), normal.y + RandUtil.nextDouble(-0.01, 0.01), normal.z + RandUtil.nextDouble(-0.01, 0.01)));
			if (RandUtil.nextBoolean()) glitter.setColor(color1);
			else glitter.setColor(color2);
			if (scatter > 0) {
				double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
				double r = scatter * RandUtil.nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-scatter, scatter), z));
			}
		});
	}

	public static void SPIRIT_WIGHT_FLAME_FAR(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.15;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColorFunction(new InterpColorHSV(Color.RED, 50, 20.0F));
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(0, RandUtil.nextDouble(0, 0.02), 0));
		});
	}

	public static void SPIRIT_WIGHT_FLAME_CLOSE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 20, 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColor(Color.RED);
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.02, 0.02), RandUtil.nextDouble(0, 0.03), RandUtil.nextDouble(-0.02, 0.02)));
		});
	}

	public static void SPIRIT_WIGHT_FLAME_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (i, build) -> {
			double radius = 0.1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setLifetime(RandUtil.nextInt(10, 40));
			glitter.setColor(new Color(0x4DFFFFFF, true));
			glitter.setScaleFunction(new InterpScale(0, (float) RandUtil.nextDouble(3, 4)));
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.2), z));
			if (RandUtil.nextInt(15) == 0)
				glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
						RandUtil.nextDouble(0, 0.03),
						RandUtil.nextDouble(-0.01, 0.01)));
		});
	}

	public static void CRAFTING_ALTAR_IDLE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
		glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
				RandUtil.nextDouble(0, 0.05),
				RandUtil.nextDouble(-0.01, 0.01)));
		glitter.setColor(new Color(0x0022FF));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, build) -> {
			double radius = 0.1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setScale((float) RandUtil.nextDouble(1, 2));
			glitter.setLifetime(RandUtil.nextInt(5, 30));
			glitter.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(1, 1.5), 0));
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.3), z));
		});
	}

	public static void SPIRIT_WIGHT_HURT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(100, 150));
		glitter.setColorFunction(new InterpColorHSV(Color.BLUE, 50, 20.0F));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), RandUtil.nextInt(40, 100), 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(0, 0.4), z));
			glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0, 0.02), 0));
		});
	}

	public static void FAIRY_TRAIL(World world, Vec3d pos, Color color, boolean sad, int age) {
		if (((age / 4) >= (age / 2)) || (age == 0)) return;
		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(age / 8, age / 4));
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), RandUtil.nextInt(1, 3), 0, (i, build) -> {
			glitter.setMotion(new Vec3d(RandUtil.nextDouble(-0.02, 0.02), RandUtil.nextDouble(-0.02, 0.02), RandUtil.nextDouble(-0.02, 0.02)));
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

	public static void EXPLODE(World world, Vec3d pos, Color color1, Color color2, double strengthUpwards, double strengthSideways, int amount, int lifeTime, int lifeTimeRange, boolean bounce) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setCollision(true);
		glitter.enableMotionCalculation();
		glitter.setColorFunction(new InterpColorHSV(ColorUtils.changeColorAlpha(color1, RandUtil.nextInt(50, 150)), ColorUtils.changeColorAlpha(color2, RandUtil.nextInt(50, 150))));
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.03, -0.04), 0));
		glitter.setCanBounce(true);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), amount, 0, (i, build) -> {
			double radius = RandUtil.nextDouble(1, 2);
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d normalize = new Vec3d(x, 0, z).normalize();
			glitter.setMotion(new Vec3d(
					normalize.x * RandUtil.nextDouble(-strengthSideways, strengthSideways),
					RandUtil.nextDouble(-strengthUpwards, strengthUpwards),
					normalize.z * RandUtil.nextDouble(-strengthSideways, strengthSideways)
			));
			glitter.setAlphaFunction(new InterpFadeInOut(0.0f, RandUtil.nextFloat()));
			glitter.setLifetime(RandUtil.nextInt(lifeTime - lifeTimeRange, lifeTime + lifeTimeRange));
			glitter.setScale(RandUtil.nextFloat());
		});
	}

	public static void STRUCTURE_FLAIR(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));

		glitter.setAlphaFunction(new InterpFadeInOut(0F, 0.9F));
		glitter.setColor(color);
		glitter.setLifetime(40);
		glitter.setScale(2);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, build) -> {
			build.setScale(5);
		});
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (i, build) -> {
			build.setScale(2);
			int x = RandUtil.nextInt(4);
			switch (x) {
				case 0:
					glitter.setMotion(new Vec3d(0, 0, 0.2));
					break;
				case 1:
					glitter.setMotion(new Vec3d(0, 0, -0.2));
					break;
				case 2:
					glitter.setMotion(new Vec3d(0.2, 0, 0));
					break;
				case 3:
					glitter.setMotion(new Vec3d(-0.2, 0, 0));
					break;
			}
		});
	}

	public static void STRUCTURE_BEACON(World world, Vec3d pos, Color color) {
		ParticleBuilder beacon = new ParticleBuilder(10);
		beacon.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		beacon.setScale(2);
		beacon.setLifetime(40);
		beacon.setAlphaFunction(new InterpFadeInOut(0F, 0.9F));
		beacon.setColor(color);
		ParticleSpawner.spawn(beacon, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			if (RandUtil.nextBoolean()) {
				beacon.setMotion(new Vec3d(0, 0.2, 0));
			} else {
				beacon.setMotion(new Vec3d(0, -0.2, 0));
			}
		});
	}

	public static void CRAFTING_ALTAR_HELIX(World world, Vec3d pos) {
		ParticleBuilder beam = new ParticleBuilder(200);
		beam.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		beam.setAlphaFunction(new InterpFadeInOut(1f, 1f));

		pos = pos.addVector(0, 0.75, 0);
		ParticleSpawner.spawn(beam, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			beam.setScale(RandUtil.nextFloat());
			beam.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
			beam.setMotion(new Vec3d(0, RandUtil.nextDouble(0.1, 0.8), 0));
			beam.setLifetime(RandUtil.nextInt(0, 40));
		});

		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(1f, 1f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			helix.setScale(RandUtil.nextFloat());
			helix.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(200, 255)));
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, RandUtil.nextDouble(0.0, 255.0), 0), 0, RandUtil.nextInt(1, 5), RandUtil.nextInt(1, 5), 0));
			helix.setLifetime(RandUtil.nextInt(0, 100));
		});
	}

	public static void CLUSTER_DRAPE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(200);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.enableMotionCalculation();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
			glitter.setScale(RandUtil.nextFloat());
			glitter.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(-0.01, 0.01)));
			glitter.setLifetime(RandUtil.nextInt(30, 60));
		});
	}

	public static void MAGIC_DOT(World world, Vec3d pos, float scale) {
		ParticleBuilder glitter = new ParticleBuilder(3);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(RandUtil.nextInt(0, 100), RandUtil.nextInt(0, 100), RandUtil.nextInt(50, 255)));
			if (scale == -1) glitter.setScale(RandUtil.nextFloat());
			else {
				glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0.3), 0));
				glitter.setLifetime(RandUtil.nextInt(30));
				glitter.setScale(scale);
			}
		});
	}

	public static void CRAFTING_ALTAR_CLUSTER_SUCTION(World world, Vec3d pos, InterpFunction<Vec3d> bezier3D) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.5f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
			helix.setColorFunction(new InterpColorHSV(ColorUtils.changeColorAlpha(Color.BLUE, RandUtil.nextInt(100, 150)), ColorUtils.changeColorAlpha(Color.CYAN, RandUtil.nextInt(100, 150))));
			helix.setScale(RandUtil.nextFloat());
			helix.setPositionFunction(bezier3D);
			helix.setScaleFunction(new InterpScale(1, 0));
			helix.setLifetime(RandUtil.nextInt(10, 30));
		});
	}

	public static void CRAFTING_ALTAR_PEARL_EXPLODE(World world, Vec3d pos) {
		ParticleBuilder builder = new ParticleBuilder(1);
		builder.setAlphaFunction(new InterpFadeInOut(0.0f, 0.1f));
		builder.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		builder.enableMotionCalculation();
		ParticleSpawner.spawn(builder, world, new InterpLine(pos, pos.addVector(0, 100, 0)), RandUtil.nextInt(400, 500), 0, (aFloat, particleBuilder) -> {
			builder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(200, 255)));
			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			builder.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-0.1, 0.1), z));
			builder.setScale(RandUtil.nextFloat());
			builder.setMotion(new Vec3d(x, 0, z));
			builder.setLifetime(RandUtil.nextInt(400, 600));
		});

		ParticleBuilder shockwave = new ParticleBuilder(1);
		shockwave.setAlphaFunction(new InterpFadeInOut(0.0f, 0.1f));
		shockwave.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		shockwave.setCollision(true);
		shockwave.enableMotionCalculation();
		ParticleSpawner.spawn(shockwave, world, new StaticInterp<>(pos), RandUtil.nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
			shockwave.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(200, 255)));
			double radius = 2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			shockwave.setScale(RandUtil.nextFloat());
			shockwave.setMotion(new Vec3d(x, RandUtil.nextDouble(0.5), z));
			shockwave.setLifetime(RandUtil.nextInt(50, 100));
		});
	}

	public static void COLORFUL_BATTERY_BEZIER(World world, BlockPos pedestal, BlockPos center) {
		ParticleBuilder glitter = new ParticleBuilder(200);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pedestal).addVector(0.5, 1, 0.5)), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColorFunction(new InterpColorHSV(ColorUtils.changeColorAlpha(Color.BLUE, RandUtil.nextInt(100, 150)), ColorUtils.changeColorAlpha(Color.CYAN, RandUtil.nextInt(100, 150))));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(center.subtract(pedestal)), new Vec3d(0, 3, 0), new Vec3d(0, 5, 0)));
			glitter.setScaleFunction(new InterpScale(1, 0.4f));
			glitter.setLifetime(RandUtil.nextInt(10, 30));
		});
	}

	public static void SHAPE_BEAM(World world, Vec3d target, Vec3d origin, Color color) {
		ParticleBuilder beam = new ParticleBuilder(10);
		beam.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		beam.setAlphaFunction(new InterpFadeInOut(0.3f, 1f));
		//beam.disableRandom();

		beam.setColor(ColorUtils.shiftColorHueRandomly(color, 10));
		beam.setCollision(true);

		Vec3d look = target.subtract(origin).normalize();
		double dist = target.distanceTo(origin);

		ParticleSpawner.spawn(beam, world, new StaticInterp<>(origin), 1, 0, (aFloat1, particleBuilder1) -> {
			particleBuilder1.setPositionOffset(look.scale(RandUtil.nextDouble(0, dist)));
			particleBuilder1.setScale(RandUtil.nextFloat(0.1f, 0.5f));
			final int life = RandUtil.nextInt(50, 60);
			particleBuilder1.setLifetime(life);
			particleBuilder1.enableMotionCalculation();
			particleBuilder1.setCollision(true);
			particleBuilder1.setCanBounce(true);
			particleBuilder1.setAcceleration(new Vec3d(0, -0.03, 0));

			double radius = 2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, RandUtil.nextDouble(-1, 2), z);
			particleBuilder1.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, dest, dest.scale(2), new Vec3d(dest.x, RandUtil.nextDouble(-2, 2), dest.z)));

			particleBuilder1.setTick(particle -> {
				if (particle.getAge() >= particle.getLifetime() / RandUtil.nextDouble(2, 5)) {
					if (particle.getAcceleration().y == 0)
						particle.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.01), 0));
				} else if (particle.getAcceleration().x != 0 || particle.getAcceleration().y != 0 || particle.getAcceleration().z != 0) {
					particle.setAcceleration(Vec3d.ZERO);
				}
			});
		});

		ParticleSpawner.spawn(beam, world, new StaticInterp<>(origin), 10, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setTick(particle -> particle.setAcceleration(Vec3d.ZERO));

			particleBuilder.setScaleFunction(new InterpFadeInOut(0, 1f));
			particleBuilder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.2f));
			particleBuilder.setScale(RandUtil.nextFloat(0.5f, 1.5f));
			particleBuilder.setLifetime(RandUtil.nextInt(10, 20));
			particleBuilder.disableMotionCalculation();
			particleBuilder.setMotion(Vec3d.ZERO);
			particleBuilder.setCanBounce(false);
			particleBuilder.setPositionOffset(Vec3d.ZERO);
			particleBuilder.setPositionFunction(new InterpLine(Vec3d.ZERO, target.subtract(origin)));
		});
	}

	public static void EFFECT_NULL_GRAV(World world, @Nonnull Vec3d pos, @Nullable EntityLivingBase caster, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(20, 30));
		glitter.setColor(color == null ? Color.WHITE : color);
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), RandUtil.nextInt(5, 10), RandUtil.nextInt(0, 30), (aFloat, particleBuilder) -> {
			glitter.setScale((float) RandUtil.nextDouble(0.3, 0.8));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			if (RandUtil.nextBoolean())
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, caster == null ? 1 / 2 : caster.height / 2, 0),
						new Vec3d(0, caster == null ? -1 : -caster.height, 0),
						1f, 0f, 1f, RandUtil.nextFloat()));
			else glitter.setPositionFunction(new InterpHelix(
					new Vec3d(0, caster == null ? 1 / 2 : caster.height / 2, 0),
					new Vec3d(0, caster == null ? 1.5 : caster.height + 0.5, 0),
					1f, 0f, 1f, RandUtil.nextFloat()));
		});
	}

	public static void EFFECT_REGENERATE(World world, @Nonnull Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColor(ColorUtils.changeColorAlpha(color, RandUtil.nextInt(200, 255)));
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 20, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(10, 40));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, RandUtil.nextFloat()));

			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, RandUtil.nextDouble(-1, 1), z);
			glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, dest, dest.scale(2), new Vec3d(dest.x, RandUtil.nextDouble(-2, 2), dest.z)));
		});
	}

	public static void EFFECT_BURN(World world, @Nonnull Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(3);
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 4, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(ColorUtils.changeColorAlpha(color, RandUtil.nextInt(200, 255)));

			glitter.setLifetime(RandUtil.nextInt(10, 30));
			glitter.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(3, 10), 0f));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, RandUtil.nextFloat()));
			glitter.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.05, 0.05),
					RandUtil.nextDouble(0.05),
					RandUtil.nextDouble(-0.05, 0.05)
			));
			glitter.setPositionOffset(new Vec3d(
					RandUtil.nextDouble(-0.3, 0.3),
					RandUtil.nextDouble(-0.3, 0.3),
					RandUtil.nextDouble(-0.3, 0.3)
			));
		});


		ParticleBuilder dust = new ParticleBuilder(3);
		dust.setScale(1);
		dust.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(dust, world, new StaticInterp<>(pos), 3, 0, (aFloat, particleBuilder) -> {

			dust.setLifetime(RandUtil.nextInt(10, 30));
			dust.setScaleFunction(new InterpScale(3f, 0.5f));
			dust.setAlphaFunction(new InterpFadeInOut(1, 1));
			dust.setColor(Color.DARK_GRAY);
			dust.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.05, 0.05),
					RandUtil.nextDouble(0.05),
					RandUtil.nextDouble(-0.05, 0.05)
			));
		});

		//ParticleBuilder dust = new ParticleBuilder(3);
		//dust.setScale(1);
		//dust.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
//
		//ParticleSpawner.spawn(dust, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
		//	dust.setColor(Color.DARK_GRAY);
		//	dust.setLifetime(RandUtil.nextInt(20, 30));
		//	dust.setScale(1);
		//	dust.setScaleFunction(new InterpFadeInOut(0, 0.9f));
		//	//dust.setAlphaFunction(new InterpFadeInOut(0.3f, RandUtil.nextFloat()));
		//	double x = RandUtil.nextDouble(-4, 4),
		//			z = RandUtil.nextDouble(-4, 4);
		//	dust.setPositionFunction(new InterpBezier3D(Vec3d.ZERO,
		//			new Vec3d(x, RandUtil.nextDouble(4), z),
		//			new Vec3d(x, -5, z), new Vec3d(0, 1, 0)));
//
		//	//double radius = 3;
		//	//double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
		//	//double r = radius * RandUtil.nextFloat();
		//	//double x = r * MathHelper.cos((float) theta);
		//	//double z = r * MathHelper.sin((float) theta);
		//	//Vec3d dest = new Vec3d(x, RandUtil.nextDouble(-1, 1), z);
		//	//glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, dest,
		//	//		new Vec3d(0, RandUtil.nextDouble(0, 1), 0),
		//	//		new Vec3d(0, RandUtil.nextDouble(-2, 0), 0)));
		//});
	}

	public static void BLOCK_HIGHLIGHT(World world, BlockPos pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.setColor(color);
		glitter.disableRandom();
		glitter.disableMotionCalculation();
		glitter.setScale(2f);
		glitter.setLifetime(200);

		double indent = 0.75;
		Vec3d bottom = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5 - indent, pos.getZ() + 0.5);
		Vec3d top = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5 + indent, pos.getZ() + 0.5);
		Vec3d front = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 + indent);
		Vec3d back = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 - indent);
		Vec3d left = new Vec3d(pos.getX() + 0.5 + indent, pos.getY() + 0.5, pos.getZ() + 0.5);
		Vec3d right = new Vec3d(pos.getX() + 0.5 - indent, pos.getY() + 0.5, pos.getZ() + 0.5);

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(bottom), 1);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(top), 1);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(left), 1);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(right), 1);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(front), 1);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(back), 1);
	}

	public static void STRUCTURE_BOUNDS(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(10));
		glitter.setScale(RandUtil.nextFloat());
		glitter.setRenderNormalLayer(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.9F, 0.9F));
		glitter.setScale(2);
		glitter.setLifetime(40);
		glitter.setColor(color);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1);
	}
}
