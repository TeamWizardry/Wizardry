package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.core.LightningTracker;
import com.teamwizardry.wizardry.common.entity.projectile.EntityLightningProjectile;
import com.teamwizardry.wizardry.common.network.PacketRenderLightningBolt;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.HashSet;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_lightning")
public class ModuleEffectLightning implements IModuleEffect {

	@Override
	public void initEffect(ModuleInstanceEffect instance) {
	}

	@ModuleOverride("shape_touch_render")
	public boolean onRenderTouch(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		return true;
	}

	@ModuleOverride("shape_projectile_render")
	public boolean onRenderProjectile(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		return true;
	}

	@ModuleOverride("shape_cone_render")
	public boolean onRenderCone(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		return true;
	}

	@ModuleOverride("shape_beam_render")
	public boolean onRenderBeam(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		return true;
	}

	
	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_extend_range", "modifier_increase_potency", "modifier_extend_time"};
	}

	@ModuleOverride("shape_self_run")
	public void onRunSelf(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		if (world.isRemote) return;
		
		Entity caster = data.getCaster();
		if (caster == null) return;
		Vec3d origin = data.getOrigin();
		if (origin == null) return;
		
		if (!childRing.taxCaster(data, true)) return;
		
		double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2;
		double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);

		RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
		float pitch = rand.nextFloat(-45, 45);
		float yaw = rand.nextFloat(0, 360);
		doLightning(rand.nextLong(100, 100000), world, caster, origin, Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(range).add(origin), range, potency, duration);
	}

	@ModuleOverride("shape_touch_run")
	public void onRunTouch(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		Vec3d look = data.getData(LOOK);
		Entity caster = data.getCaster();
		Vec3d origin = data.getOriginWithFallback();
		if (look == null || caster == null || origin == null) return;
		
		if (!childRing.taxCaster(data, true)) return;
		
		double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data);
		double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		
		RayTraceResult trace = new RayTrace(world, look, origin,
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();
		
		doLightning(RandUtil.nextLong(100, 100000), world, caster, origin, trace.hitVec, range, potency, duration);
	}

	@ModuleOverride("shape_projectile_run")
	public boolean onRunProjectile(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		if (world.isRemote) return true;

		Vec3d origin = data.getOriginWithFallback();
		if (origin == null) return true;

		if (!childRing.taxCaster(data, true)) return true;
		
		double dist = shape.getAttributeValue(AttributeRegistry.RANGE, data);
		double speed = shape.getAttributeValue(AttributeRegistry.SPEED, data);

		EntityLightningProjectile proj = new EntityLightningProjectile(world, shape, childRing, data, (float) dist, (float) speed, (float) 0.1);
		proj.setPosition(origin.x, origin.y, origin.z);
		world.spawnEntity(proj);
		
		return true;
	}

	@ModuleOverride("shape_beam_run")
	public void onRunBeam(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		if (world.isRemote) return;
		Entity caster = data.getCaster();
		float yaw = data.getData(YAW, 0F);
		float pitch = data.getData(PITCH, 0F);
		Vec3d origin = data.getOriginHand();

		if (origin == null) return;
		
		if (!childRing.taxCaster(data, true)) return;
		
		double lightningRange = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double lightningPotency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
		double lightningDuration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		double beamRange = shape.getAttributeValue(AttributeRegistry.RANGE, data);

		RayTraceResult traceResult = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, beamRange)
				.setSkipBlocks(true)
				.setSkipEntities(true)
				.trace();

		doLightning(RandUtil.nextLong(100, 100000), world, caster, origin, traceResult.hitVec, lightningRange, lightningPotency, lightningDuration);
	}

	@ModuleOverride("shape_cone_run")
	public void onRunCone(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		float yaw = data.getData(YAW, 0F);
		float pitch = data.getData(PITCH, 0F);
		Vec3d origin = data.getOriginHand();
		Entity caster = data.getCaster();
		
		if (origin == null) return;
		
		if (!childRing.taxCaster(data, true)) return;

		double coneRange = shape.getAttributeValue(AttributeRegistry.RANGE, data);
		double lightningRange = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double lightningPotency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data);
		double lightningDuration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		
		RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
		
		float angle = (float) coneRange * 2;
		float newPitch = pitch + rand.nextFloat(-angle, angle);
		float newYaw = yaw + rand.nextFloat(-angle, angle);

		Vec3d to = Vec3d.fromPitchYaw(newPitch, newYaw).normalize().scale(coneRange).add(origin);
		doLightning(rand.nextLong(100, 100000), world, caster, origin, to, lightningRange, lightningPotency, lightningDuration);
	}

	@ModuleOverride("shape_zone_run")
	public void onRunZone(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		World world = data.world;
		Entity caster = data.getCaster();
		Vec3d targetPos = data.getTargetWithFallback();

		if (targetPos == null) return;

		if (!childRing.taxCaster(data, true)) return;
		
		double lightningRange = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double lightningPotency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
		double lightningDuration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		double zoneAoE = shape.getAttributeValue(AttributeRegistry.AREA, data);
		double zoneRange = shape.getAttributeValue(AttributeRegistry.RANGE, data);

		Vec3d min = targetPos.subtract(zoneAoE/2, zoneRange/2, zoneAoE/2);
		Vec3d max = targetPos.add(zoneAoE / 2, zoneRange / 2, zoneAoE / 2);

		RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
		
		Vec3d from = new Vec3d(rand.nextDouble(min.x, max.x), rand.nextDouble(min.y, max.y), rand.nextDouble(min.z, max.z));
		float pitch = (float) (180 * Math.asin(2 * rand.nextDouble() - 1) / Math.PI);
		float yaw = (float) rand.nextDouble(360);
		
		Vec3d to = Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(lightningRange).add(from);
		
		doLightning(rand.nextLong(100, 100000), world, caster, from, to, lightningRange, lightningPotency, lightningDuration);
	}
	
	@ModuleOverride("generic_chargeup_time")
	public int modifyChargeupTime(int originalTime, @ContextRing SpellRing mySpellRing) {
		return mySpellRing.getChargeUpTime();
	}
	
	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @ContextRing @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
		return true;
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @ContextRing @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
	}
	
	public static void doLightning(long seed, World world, Entity caster, Vec3d from, Vec3d to, double offshootRange, double potency, double duration)
	{
		RandUtilSeed rand = new RandUtilSeed(seed);
		ArrayList<Vec3d> points = new ArrayList<>();
		LightningGenerator.generate(rand, from, to, offshootRange).forEach(point -> points.add(point));

		world.playSound(null, new BlockPos(to), ModSounds.LIGHTNING, SoundCategory.NEUTRAL, 0.5f, RandUtil.nextFloat(1, 1.5f));
		
		HashSet<BlockPos> positions = new HashSet<>();
		for (Vec3d point : points)
			positions.add(new BlockPos(point));

		HashSet<EntityLivingBase> entities = new HashSet<>();
		for (BlockPos position : positions)
			entities.addAll(world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(position).contract(0.2, 0.2, 0.2)));
		entities.remove(caster);

		for (Entity entity : entities)
			LightningTracker.INSTANCE.addEntity(from, entity, caster, potency, duration);
		
		doLightningRender(seed, world, from, to, offshootRange);
	}
	
	public static void doLightningRender(long seed, World world, Vec3d from, Vec3d to, double offshootRange)
	{
		PacketHandler.NETWORK.sendToAllAround(new PacketRenderLightningBolt(seed, from, to, offshootRange),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), from.x, from.y, from.z, 256));
	}
}
