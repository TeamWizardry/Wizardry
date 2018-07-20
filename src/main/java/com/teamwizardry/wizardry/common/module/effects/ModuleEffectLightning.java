package com.teamwizardry.wizardry.common.module.effects;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;
import static com.teamwizardry.wizardry.common.module.shapes.ModuleShapeBeam.BEAM_CAST;
import static com.teamwizardry.wizardry.common.module.shapes.ModuleShapeBeam.BEAM_OFFSET;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

		registerRenderOverride("shape_self", (data, spellRing, childRing) -> {});
		registerRenderOverride("shape_touch", (data, spellRing, childRing) -> {});
		registerRenderOverride("shape_projectile", (data, spellRing, childRing) -> {});
		registerRenderOverride("shape_cone", (data, spellRing, childRing) -> {});
		registerRenderOverride("shape_beam", (data, spellRing, childRing) -> {});
		registerRenderOverride("shape_zone", (data, spellRing, childRing) -> {});
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
			World world = data.world;
			if (world.isRemote) return;
			
			Entity caster = data.getCaster();
			if (caster == null) return;
			Vec3d origin = data.getOrigin();
			if (origin == null) return;
			
			double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2;
			double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
			
			if (!childRing.taxCaster(data)) return;
			
			RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
			float pitch = rand.nextFloat(-45, 45);
			float yaw = rand.nextFloat(0, 360);
			doLightning(rand.nextLong(100, 100000), world, caster, origin, Vec3d.fromPitchYaw(pitch, yaw).normalize().scale(range).add(origin), range, potency, duration);
			LightningTracker.INSTANCE.addEntity(origin, caster, caster, potency, duration);
		};
	}
	
	private OverrideConsumer<SpellData, SpellRing, SpellRing> getTouchOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			Vec3d look = data.getData(LOOK);
			Entity caster = data.getCaster();
			Vec3d origin = data.getOriginWithFallback();
			if (look == null || caster == null || origin == null) return;
			if (!childRing.taxCaster(data)) return;
			
			double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data);
			double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
			
			RayTraceResult trace = new RayTrace(world, look, origin,
					caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
					.setSkipEntity(caster).setReturnLastUncollidableBlock(true).setIgnoreBlocksWithoutBoundingBoxes(true).trace();
			
			doLightning(RandUtil.nextLong(100, 100000), world, caster, origin, trace.hitVec, range, potency, duration);
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
			proj.setPosition(origin.x, origin.y, origin.z);
			world.spawnEntity(proj);
		};
	}
	
	
	private OverrideConsumer<SpellData, SpellRing, SpellRing> getBeamOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			if (world.isRemote) return;
			Entity caster = data.getCaster();
			float yaw = data.getData(YAW, 0F);
			float pitch = data.getData(PITCH, 0F);
			Vec3d origin = data.getOriginHand();

			if (origin == null) return;

			double lightningRange = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double lightningPotency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
			double lightningDuration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
			double beamRange = spellRing.getAttributeValue(AttributeRegistry.RANGE, data);
			
			if (!childRing.taxCaster(data)) return;
				
			RayTraceResult traceResult = new RayTrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, beamRange).setSkipBlocks(true).setSkipEntities(true).trace();

			doLightning(RandUtil.nextLong(100, 100000), world, caster, origin, traceResult.hitVec, lightningRange, lightningPotency, lightningDuration);
		};
	}
	
	private OverrideConsumer<SpellData, SpellRing, SpellRing> getConeOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			float yaw = data.getData(YAW, 0F);
			float pitch = data.getData(PITCH, 0F);
			Vec3d origin = data.getOriginWithFallback();
			Entity caster = data.getCaster();
			
			if (origin == null) return;

			double coneRange = spellRing.getAttributeValue(AttributeRegistry.RANGE, data);
			int conePotency = (int) (spellRing.getAttributeValue(AttributeRegistry.POTENCY, data));
			
			RandUtilSeed rand = new RandUtilSeed(RandUtil.nextLong(100, 100000));
			for (int i = 0; i < conePotency; i++)
			{
				// cone needs special handling, do cast loop on shape, then get overrides
			}
		};
	}
	
	private OverrideConsumer<SpellData, SpellRing, SpellRing> getZoneOverride() {
		return (data, spellRing, childRing) -> {
			World world = data.world;
			Entity caster = data.getCaster();
			Vec3d targetPos = data.getTargetWithFallback();

			if (targetPos == null) return;

			double lightningRange = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double lightningPotency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data) / 2.0;
			double lightningDuration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
			double zoneAoE = spellRing.getAttributeValue(AttributeRegistry.AREA, data);
			double zoneRange = spellRing.getAttributeValue(AttributeRegistry.RANGE, data);
			double zonePotency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, data);

			Vec3d min = targetPos.subtract(zoneAoE/2, zoneRange/2, zoneAoE/2);
			Vec3d max = targetPos.addVector(zoneAoE/2, zoneRange/2, zoneAoE/2);
			
			NBTTagCompound info = spellRing.getInformationTag();
			
			if (!childRing.taxCaster(data)) return;

			long seed = RandUtil.nextLong(100, 100000);
		};
	}
	
	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
		return true;
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// NO-OP, should always be overriding a shape
	}
	
	@Override
	public int getNewCooldown(@Nonnull SpellData spell, SpellRing ring) {
		return 0;
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
