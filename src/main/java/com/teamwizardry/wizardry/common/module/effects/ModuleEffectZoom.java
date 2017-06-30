package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.ProcessData;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.CubicBezier;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.lib.LibParticles;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;
import static com.teamwizardry.wizardry.api.spell.SpellData.constructPair;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectZoom extends ModuleEffect implements ILingeringModule {

	private static final Pair<String, Class<Vec3d>> ORIGINAL_LOC = constructPair("original_loc", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
		@NotNull
		@Override
		public NBTTagCompound serialize(Vec3d object) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setDouble("x", object.x);
			compound.setDouble("y", object.y);
			compound.setDouble("z", object.z);
			return compound;
		}

		@Override
		public Vec3d deserialize(@NotNull World world, @NotNull NBTTagCompound object) {
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

	@Nonnull
	@Override
	public String getReadableName() {
		return "Zoom";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Move swiftly and quickly to the target";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d target = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);
		Vec3d originalLoc = spell.getData(ORIGINAL_LOC);
		//if (!tax(this, spell)) return false;

		if (caster == null) return true;
		if (target == null) return true;
		if (originalLoc == null) return true;

		Vec3d sub = target.subtract(caster.getPositionVector());
		double dist = originalLoc.lengthVector();
		double tmax = spell.getData(MAX_TIME, 1);
		double tleft = spell.getData(TIME_LEFT, 1);
		float diff = (float) (sub.lengthVector() / dist);
		double v = (dist / 2) * new CubicBezier(0.05f, 0.78f, 0.58f, 0.99f).eval(diff < 0.05 ? 0 : diff);

		Vec3d norm = sub.normalize();
		caster.motionX = norm.x * v;
		caster.motionY = norm.y * v;
		caster.motionZ = norm.z * v;
		caster.velocityChanged = true;

		if (caster instanceof EntityLivingBase) {
			((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 10, 1, true, false));
			((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PHASE, 10, 1, true, false));
		}

		return true;
	}

	private double timePercentage(double t, double tmax) {
		return t / tmax;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity caster = spell.getData(CASTER);

		if (caster == null) return;

		LibParticles.EFFECT_REGENERATE(world, caster.getPositionVector(), getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectZoom());
	}

	@Override
	public int lingeringTime(SpellData spell) {
		Vec3d target = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		if (caster == null) return 0;
		if (target == null) return 0;
		int time = 200;
		Vec3d sub = target.subtract(caster.getPositionVector());
		spell.addData(ORIGINAL_LOC, sub);
		spell.addData(MAX_TIME, time);
		return time;
	}
}
