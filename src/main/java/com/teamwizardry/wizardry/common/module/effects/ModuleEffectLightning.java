package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.OverrideConsumer;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.core.LightningTracker;
import com.teamwizardry.wizardry.common.entity.projectile.EntityLightningProjectile;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.common.network.PacketRenderLightningBolt;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectLightning extends ModuleEffect implements IOverrideCooldown {

	public ModuleEffectLightning() {
		registerRunOverride("shape_self", getSelfOverride());
		registerRunOverride("shape_touch", getTouchOverride());
		registerRunOverride("shape_projectile", getProjectileOverride());
		registerRunOverride("shape_cone", getConeOverride());
		registerRunOverride("shape_beam", getBeamOverride());
		registerRunOverride("shape_zone", getZoneOverride());

//		registerRenderOverride("shape_self", getSelfRenderOverride());
//		registerRenderOverride("shape_touch", getTouchRenderOverride());
//		registerRenderOverride("shape_projectile", getProjectileRenderOverride());
//		registerRenderOverride("shape_cone", getConeRenderOverride());
		registerRenderOverride("shape_beam", getBeamRenderOverride());
//		registerRenderOverride("shape_zone", getZoneRenderOverride());
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_lightning";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseRange(), new ModuleModifierIncreasePotency(), new ModuleModifierIncreaseDuration()};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getSelfOverride() {
		return (data, spellRing, childRing) -> {

		};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getTouchOverride() {
		return (data, spellRing, childRing) -> {

		};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getProjectileOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			if (world.isRemote) return;

			Vec3d origin = data.getOriginWithFallback();
			if (origin == null) return;

			double dist = spellRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double speed = spellRing.getAttributeValue(AttributeRegistry.SPEED, data);

			EntityLightningProjectile proj = new EntityLightningProjectile(world, spellRing, childRing, data, (float) dist, (float) speed, (float) 0.1);
			world.spawnEntity(proj);
		};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getBeamOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			Entity caster = data.getCaster();
			float yaw = data.getData(YAW, 0F);
			float pitch = data.getData(PITCH, 0F);
			Vec3d origin = data.getOriginHand();

			if (origin == null) return;

			double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
			double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);

			if (!spellRing.taxCaster(data)) return;

			RayTraceResult traceResult = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, range).setSkipBlocks(true).setSkipEntities(true).trace();

			long seed = RandUtil.nextLong(100, 100000);

			data.addData(SEED, seed);

			LightningGenerator generator = new LightningGenerator(origin, traceResult.hitVec, new RandUtilSeed(seed));

			ArrayList<Vec3d> points = generator.generate();

			data.world.playSound(null, new BlockPos(traceResult.hitVec), ModSounds.LIGHTNING, SoundCategory.NEUTRAL, 0.5f, RandUtil.nextFloat(1, 1.5f));
			for (Vec3d point : points) {
				List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(point)).contract(0.2, 0.2, 0.2));
				if (!entityList.isEmpty()) {
					for (Entity entity : entityList) {
						LightningTracker.INSTANCE.addEntity(origin, entity, caster, potency, duration);
					}
				}
			}
		};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getConeOverride() {
		return (data, spellRing, childRing) -> {

		};
	}

	private OverrideConsumer<SpellData, SpellRing, SpellRing> getZoneOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			Entity caster = data.getCaster();
			float yaw = data.getData(YAW, 0F);
			float pitch = data.getData(PITCH, 0F);
			Vec3d origin = data.getOriginHand();

			if (origin == null) return;

			double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
			double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);

			if (!spellRing.taxCaster(data)) return;

			RayTraceResult traceResult = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, range).setSkipBlocks(true).setSkipEntities(true).trace();

			long seed = RandUtil.nextLong(100, 100000);

			data.addData(SEED, seed);

			LightningGenerator generator = new LightningGenerator(origin, traceResult.hitVec, new RandUtilSeed(seed));

			ArrayList<Vec3d> points = generator.generate();

			data.world.playSound(null, new BlockPos(traceResult.hitVec), ModSounds.LIGHTNING, SoundCategory.NEUTRAL, 0.5f, RandUtil.nextFloat(1, 1.5f));
			for (Vec3d point : points) {
				List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(point)).contract(0.2, 0.2, 0.2));
				if (!entityList.isEmpty()) {
					for (Entity entity : entityList) {
						LightningTracker.INSTANCE.addEntity(origin, entity, caster, potency, duration);
					}
				}
			}
		};
	}

	@SideOnly(Side.CLIENT)
	private OverrideConsumer<SpellData, SpellRing, SpellRing> getBeamRenderOverride() {
		return (data, ring, childRing) -> {
			World world = data.world;
			float yaw = data.getData(YAW, 0F);
			float pitch = data.getData(PITCH, 0F);
			long seed = data.getData(SEED, 0L);
			double range = ring.getAttributeValue(AttributeRegistry.RANGE, data);
			Vec3d origin = data.getOriginHand();
			if (origin == null) return;

			RayTraceResult traceResult = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, range).setSkipBlocks(true).setSkipEntities(true).trace();

			PacketHandler.NETWORK.sendToAllAround(new PacketRenderLightningBolt(origin, traceResult.hitVec, seed),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), origin.x, origin.y, origin.z, 256));
		};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
	}

	@Override
	public int getNewCooldown(@Nonnull SpellData spell, SpellRing ring) {
		return 30;
	}
}
