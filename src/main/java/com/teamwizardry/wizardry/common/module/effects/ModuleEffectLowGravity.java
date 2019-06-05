package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_low_gravity")
public class ModuleEffectLowGravity implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_extend_time"};
	}

	@ModuleOverride("shape_zone_run")
	public boolean onRunZone(World world, SpellData data, SpellRing ring, @ContextRing SpellRing childRing) {
		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);
		double range = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		Vec3d targetPos = data.getTarget(world);

		if (targetPos == null) return false;

		Vec3d min = targetPos.subtract(aoe, range, aoe);
		Vec3d max = targetPos.add(aoe, range, aoe);

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(min, max));
		for (Entity entity : entities) {
			if (entity instanceof EntityLivingBase) {
				if (!((EntityLivingBase) entity).isPotionActive(ModPotions.LOW_GRAVITY) && entity.getDistanceSq(targetPos.x, targetPos.y, targetPos.z) <= aoe * aoe) {
					data.processEntity(entity, false);
					run(world, (ModuleInstanceEffect) childRing.getModule(), data, childRing);
				}
			}
		}
		return true;
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster(world);

		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(world, spell, true)) return false;

		if (targetEntity != null) {
			world.playSound(null, targetEntity.getPosition(), ModSounds.TELEPORT, SoundCategory.NEUTRAL, 1, 1);
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.LOW_GRAVITY, (int) duration, (int) potency, true, false));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFloatInOut(0.0f, 0.1f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.enableMotionCalculation();
		glitter.setScaleFunction(new InterpScale(1, 0));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(5, 15), 0, (aFloat, particleBuilder) -> {
			double radius = 2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setScale(RandUtil.nextFloat());
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-2, 2), z));
			glitter.setLifetime(RandUtil.nextInt(30, 40));
			Vec3d direction = position.add(glitter.getPositionOffset()).subtract(position).normalize();
			glitter.setMotion(direction.scale(RandUtil.nextDouble(0.5, 1.3)));
		});

	}
}
