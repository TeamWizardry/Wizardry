package com.teamwizardry.wizardry.api.item.halo;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.function.BiConsumer;

@SideOnly(Side.CLIENT)
public class HaloInfusionItemRenderers {

	public static final HashMap<HaloInfusionItem, BiConsumer<Vec3d, World>> renders = new HashMap<>();
	public static HaloInfusionItemRenderers INSTANCE = new HaloInfusionItemRenderers();

	static {
		addRender(HaloInfusionItemRegistry.EMPTY, (vec3d, world) -> {
			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xff8300));

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
				build.setLifetime(50);
			});
		});

		addRender(HaloInfusionItemRegistry.OVERWORLD_PRISMARINE_CRYSTALS, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0x0022FF));
			glitter.setCollision(true);

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(4) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.02, 0.02),
						RandUtil.nextDouble(-0.02, 0.05),
						RandUtil.nextDouble(-0.02, 0.02)
				));
				build.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.015, -0.01), 0));
				build.setLifetime(RandUtil.nextInt(15, 25));
			});
		});

		addRender(HaloInfusionItemRegistry.OVERWORLD_EMERALD, (vec3d, world) -> {
			if (RandUtil.nextInt(3) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0x16bf00));

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.02, 0.02),
						RandUtil.nextDouble(-0.02, 0.02),
						RandUtil.nextDouble(-0.02, 0.02)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});
		});

		addRender(HaloInfusionItemRegistry.OVERWORLD_RABBIT_FOOT, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0x63e2a7));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(4) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(1.5f, 4.5f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.015, 0.015),
						RandUtil.nextDouble(-0.015, 0.1),
						RandUtil.nextDouble(-0.015, 0.015)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});
		});

		addRender(HaloInfusionItemRegistry.NETHER_BLAZE_POWDERS, (vec3d, world) -> {
			if (RandUtil.nextInt(1) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColorFunction(new InterpColorHSV(RandUtil.nextBoolean() ? Color.RED : Color.ORANGE, Color.GRAY));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(2) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2f, 4.5f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.01, 0.01),
						RandUtil.nextDouble(-0.01, 0.07),
						RandUtil.nextDouble(-0.01, 0.01)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});
		});

		addRender(HaloInfusionItemRegistry.NETHER_GHAST_TEARS, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xff6df5));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(3) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1.5f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.05, 0.05),
						RandUtil.nextDouble(-0.05, 0.05),
						RandUtil.nextDouble(-0.05, 0.05)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
				build.setAcceleration(new Vec3d(0, 0.001, 0));
			});
		});

		addRender(HaloInfusionItemRegistry.NETHER_NETHER_STAR, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xdabfff));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(4) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1.5f), 0));
				build.setPositionOffset(new Vec3d(
						RandUtil.nextDouble(-0.2, 0.2),
						RandUtil.nextDouble(-0.2, 0.2),
						RandUtil.nextDouble(-0.2, 0.2)
				));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.01, 0.01),
						RandUtil.nextDouble(0.01, 0.05),
						RandUtil.nextDouble(-0.01, 0.01)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});
		});


		addRender(HaloInfusionItemRegistry.END_DRAGON_BREATH, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xb118bf));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (world.getTotalWorldTime() % 10 != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 10, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 2f), 0));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.015, 0.015),
						RandUtil.nextDouble(-0.015, 0.015),
						RandUtil.nextDouble(-0.015, 0.015)
				));
				build.setLifetime(20);
			});

		});


		addRender(HaloInfusionItemRegistry.END_POPPED_CHORUS, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0f, 1f));
			glitter.setColor(new Color(0x561d8c));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 4.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(4) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(4f, 4.5f), 0));
				build.setPositionOffset(new Vec3d(
						RandUtil.nextDouble(-0.2, 0.2),
						RandUtil.nextDouble(-0.2, 0.2),
						RandUtil.nextDouble(-0.2, 0.2)
				));
				build.addMotion(new Vec3d(
						RandUtil.nextDouble(-0.01, 0.01),
						RandUtil.nextDouble(0.01, 0.05),
						RandUtil.nextDouble(-0.01, 0.01)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});
		});


		addRender(HaloInfusionItemRegistry.END_SHULKER_SHELL, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0x8200ff));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(2.5f, 3f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});

			if (RandUtil.nextInt(4) != 0) return;

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(4f, 3f), 0));
				build.setPositionOffset(new Vec3d(
						RandUtil.nextDouble(-0.02, 0.02),
						RandUtil.nextDouble(-0.02, 0.02),
						RandUtil.nextDouble(-0.02, 0.02)
				));
				build.setLifetime(RandUtil.nextInt(10, 20));
				build.setAcceleration(new Vec3d(0, 0.015, 0));
			});
		});


		addRender(HaloInfusionItemRegistry.UNDEROWRLD_UNICORN_HORN, (vec3d, world) -> {
			if (RandUtil.nextInt(2) != 0) return;

			ParticleBuilder glitter = new ParticleBuilder(5);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xff4c4c));
			glitter.setScaleFunction(new InterpScale(RandUtil.nextFloat(3f, 3.5f), 0));

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
				build.setScaleFunction(new InterpScale(RandUtil.nextFloat(1.5f, 3.5f), 0));
				build.setLifetime(RandUtil.nextInt(10, 20));
			});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(0, 0.01, 0));
				});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(0.01, 0, 0));
				});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(0, 0, 0.01));
				});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(0, -0.01, 0));
				});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(-0.01, 0, 0));
				});

			if (RandUtil.nextInt(4) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					build.setAcceleration(new Vec3d(0, 0, -0.01));
				});
		});

		addRender(HaloInfusionItemRegistry.UNDERWORLD_FAIRY_DUST, (vec3d, world) -> {

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.5f, 1f));
			glitter.setColor(new Color(0xffffff));

			if (RandUtil.nextInt(2) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(vec3d), 1, 0, (aFloat, build) -> {
					Color color = RandUtil.nextBoolean() ? Color.GREEN : RandUtil.nextBoolean() ? Color.BLUE : Color.RED;
					build.setColor(color.brighter());
					build.setScaleFunction(new InterpScale(RandUtil.nextFloat(1.5f, 3.5f), 0));
					build.setLifetime(RandUtil.nextInt(10, 20));
				});
		});
	}

	public static void addRender(HaloInfusionItem item, BiConsumer<Vec3d, World> renderHook) {
		renders.put(item, renderHook);
	}

	@Nonnull
	public static BiConsumer<Vec3d, World> getHaloRenderer(HaloInfusionItem item) {
		if (renders.containsKey(item)) return renders.get(item);
		return getHaloRenderer(HaloInfusionItemRegistry.EMPTY);
	}

	public static HashMap<HaloInfusionItem, BiConsumer<Vec3d, World>> getRenderers() {
		return renders;
	}
}
