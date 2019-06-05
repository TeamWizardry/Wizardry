package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "shape_zone")
public class ModuleShapeZone implements IModuleShape, ILingeringModule {

	private static final String ZONE_TICK = "zone_tick";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_increase_potency", "modifier_extend_range", "modifier_extend_time"};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignoreResultsForRendering() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRunChildren() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean run(@NotNull World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		//		Vec3d position = spell.getData(ORIGIN);
//		Entity caster = spell.getCaster(world);
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		boolean overriden = overrides.onRunZone(world, spell, spellRing);
		if (overriden) return true;

		Vec3d targetPos = spell.getTarget(world);

		if (targetPos == null) return false;

		double maxPotency = spellRing.getModule() != null ? spellRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY).max : 1;
		double minPotency = spellRing.getModule() != null ? spellRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY).min : 1;

		double aoe = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);
		double potency = Math.max(minPotency, spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell));
		double range = spellRing.getAttributeValue(world, AttributeRegistry.RANGE, spell);

		Vec3d min = targetPos.subtract(aoe, range, aoe);
		Vec3d max = targetPos.add(aoe, range, aoe);

		NBTTagCompound info = spell.getDataWithFallback(SpellData.DefaultKeys.COMPOUND, new NBTTagCompound());

		double zoneTick = 0;
		if (!NBTHelper.hasKey(info, ZONE_TICK)) {
			NBTHelper.setDouble(info, ZONE_TICK, maxPotency - potency);
		} else {
			zoneTick = NBTHelper.getDouble(info, ZONE_TICK, maxPotency);

			if (--zoneTick < 0) {
				zoneTick = maxPotency - potency;
			}

			NBTHelper.setDouble(info, ZONE_TICK, zoneTick);
		}
		spell.addData(COMPOUND, info);

		if (zoneTick == 0) {
			if (!spellRing.taxCaster(world, spell, true)) {
				info.setDouble(ZONE_TICK, zoneTick);
				spell.addData(COMPOUND, info);
				return false;
			}

			BlockPos target = new BlockPos(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));
			List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target));
			for (Entity entity : entities) {
				Vec3d vec = new Vec3d(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));

				SpellData copy = spell.copy();
				copy.processEntity(entity, false);
				copy.addData(YAW, entity.rotationYaw);
				copy.addData(PITCH, entity.rotationPitch);
				copy.addData(ORIGIN, vec);

				if (spellRing.getChildRing() != null)
					spellRing.getChildRing().runSpellRing(world, spell, true);
			}

			Vec3d pos = new Vec3d(target).add(0.5, 0.5, 0.5);
			if (pos.squareDistanceTo(targetPos) > aoe * aoe) return true;

			SpellData copy = spell.copy();
			copy.addData(ORIGIN, pos);
			copy.processBlock(target, EnumFacing.UP, pos);
			copy.addData(YAW, RandUtil.nextFloat(-180, 180));
			copy.addData(PITCH, RandUtil.nextFloat(-50, 50));

			if (spellRing.getChildRing() != null)
				spellRing.getChildRing().runSpellRing(world, copy, true);
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		Vec3d look = data.getData(SpellData.DefaultKeys.LOOK);
		Entity caster = data.getCaster(world);

		if (caster == null) return data;
		if (look == null) return data;

		Vec3d target;

		double interpPosX = caster.lastTickPosX + (caster.posX - caster.lastTickPosX) * partialTicks;
		double interpPosY = caster.lastTickPosY + (caster.posY - caster.lastTickPosY) * partialTicks;
		double interpPosZ = caster.lastTickPosZ + (caster.posZ - caster.lastTickPosZ) * partialTicks;

		RayTraceResult result = new RayTrace(world, look, new Vec3d(interpPosX, interpPosY + caster.getEyeHeight(), interpPosZ),
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		if (pos == null) return data;

		data.processTrace(result);

		target = data.getTarget(world);

		if (target == null) return data;

		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);

		RenderUtils.drawCircle(target, aoe, false, false, caster, partialTicks);

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if (overrides.onRenderZone(world, spell, spellRing)) return;

		Vec3d target = spell.getTarget(world);

		if (target == null) return;
		if (RandUtil.nextInt(10) != 0) return;

		double aoe = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) aoe, 1, RandUtil.nextFloat()), (int) (aoe * 25), 10, (aFloat, particleBuilder) -> {
			glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			glitter.setLifetime(RandUtil.nextInt(30, 50));
			glitter.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
			glitter.setMotion(new Vec3d(
					RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.01, 0.01)
			));
		});
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		return (int) spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
	}

	////////////////

	@ModuleOverride("shape_zone_run")
	public boolean onRunZone(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}

	@ModuleOverride("shape_zone_render")
	public boolean onRenderZone(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
}
