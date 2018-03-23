package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ProcessData;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.init.ModPotions;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ORIGIN;
import static com.teamwizardry.wizardry.api.spell.SpellData.constructPair;

/**
 * Created by Demoniaque.
 */
// TODO: Tracer's blink sound effect
@RegisterModule
public class ModuleEffectZoom extends ModuleEffect {

	private static final Pair<String, Class<Vec3d>> ORIGINAL_LOC = constructPair("original_loc", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
		@Nonnull
		@Override
		public NBTTagCompound serialize(Vec3d object) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setDouble("x", object.x);
			compound.setDouble("y", object.y);
			compound.setDouble("z", object.z);
			return compound;
		}

		@Override
		public Vec3d deserialize(@Nonnull World world, @Nonnull NBTTagCompound object) {
			double x = object.getDouble("x");
			double y = object.getDouble("y");
			double z = object.getDouble("z");
			return new Vec3d(x, y, z);
		}
	});

	@Nonnull
	@Override
	public String getID() {
		return "effect_zoom";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseRange()};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity entityHit = spell.getVictim();
		Vec3d look = spell.getData(LOOK);
		Vec3d origin = spell.getData(ORIGIN);

		if (entityHit == null) return true;
		else {
			if (!spellRing.taxCaster(spell)) return false;

			if (look == null) return true;
			if (origin == null) return true;

			double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
			RayTraceResult trace = new RayTrace(world, look, origin, range)
					.setSkipEntity(entityHit)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.setReturnLastUncollidableBlock(false)
					.trace();

			spell.addData(ORIGINAL_LOC, entityHit.getPositionVector());

			entityHit.setPositionAndUpdate(trace.hitVec.x, trace.hitVec.y, trace.hitVec.z);

			entityHit.motionX = 0;
			entityHit.motionY = 0;
			entityHit.motionZ = 0;
			entityHit.velocityChanged = true;
		}
		if (entityHit instanceof EntityLivingBase) {
			((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 5, 1, true, false));
			((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, 5, 1, true, false));
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;

		Entity entity = spell.getVictim();
		if (entity == null) return;

		Vec3d origin = spell.getData(ORIGINAL_LOC);
		if (origin == null) return;

		Vec3d to = entity.getPositionVector();

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		glitter.enableMotionCalculation();
		glitter.disableRandom();
		glitter.setCollision(true);
		glitter.setTick(particle -> {
			if (particle.getAge() >= particle.getLifetime() / RandUtil.nextDouble(2, 5)) {
				if (particle.getAcceleration().y == 0)
					particle.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.01), 0));
			} else if (particle.getAcceleration().x != 0 || particle.getAcceleration().y != 0 || particle.getAcceleration().z != 0) {
				particle.setAcceleration(Vec3d.ZERO);
			}
		});
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(origin.addVector(0, entity.height / 2.0, 0)), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setPositionOffset(new Vec3d(
					RandUtil.nextDouble(-0.5, 0.5),
					RandUtil.nextDouble(-0.5, 0.5),
					RandUtil.nextDouble(-0.5, 0.5)
			));
			ParticleSpawner.spawn(glitter, world, new InterpLine(origin.add(particleBuilder.getPositionOffset()), to.add(particleBuilder.getPositionOffset()).addVector(0, entity.height / 2.0, 0)), (int) origin.distanceTo(to) * 5, 0, (aFloat2, particleBuilder2) -> {
				glitter.setAlpha(RandUtil.nextFloat(0.5f, 0.8f));
				glitter.setScale(RandUtil.nextFloat(0.3f, 0.6f));
				glitter.setLifetime(RandUtil.nextInt(30, 50));
				glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
				glitter.setAlphaFunction(new InterpFadeInOut(0f, 1f));
			});
		});
	}
}
