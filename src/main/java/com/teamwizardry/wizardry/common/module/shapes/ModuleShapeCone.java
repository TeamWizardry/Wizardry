package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_cone")
public class ModuleShapeCone implements IModuleShape {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_extend_range"};
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
		float yaw = spell.getYaw();
		float pitch = spell.getPitch();
		Entity caster = spell.getCaster(world);

		Vec3d origin = spell.getOriginHand(world);
		if (origin == null) return false;

		double range = spellRing.getAttributeValue(world, AttributeRegistry.RANGE, spell);
		int potency = (int) (spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell));

		for (int i = 0; i < potency; i++) {

			if (!spellRing.taxCaster(world, spell, 1.0 / potency, true)) return false;
			
			long seed = RandUtil.nextLong(100, 10000);
			spell.addData(SEED, seed);
			
			IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
			overrides.onRunCone(world, spell, spellRing);
			
			float angle = (float) range * 2;
			float newPitch = pitch + RandUtil.nextFloat(-angle, angle);
			float newYaw = yaw + RandUtil.nextFloat(-angle, angle);

			Vec3d target = PosUtils.vecFromRotations(newPitch, newYaw);

			SpellData newSpell = spell.copy();

			RayTraceResult result = new RayTrace(world, target.normalize(), origin, range)
					.setEntityFilter(input -> input != caster)
					.trace();

			Vec3d lookFallback = spell.getData(LOOK);
			if (lookFallback != null) lookFallback.scale(range);
			newSpell.processTrace(result, lookFallback);

			instance.sendRenderPacket(world, newSpell, spellRing);        // Is already executed via SpellRing.runSpellRing() ???

			newSpell.addData(ORIGIN, result.hitVec);

			if (spellRing.getChildRing() != null) {
				spellRing.getChildRing().runSpellRing(world, newSpell.copy(), true);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if (overrides.onRenderCone(world, spell, spellRing)) return;

		Vec3d target = spell.getTarget(world);

		if (target == null) return;

		Vec3d origin = spell.getOriginHand(world);
		if (origin == null) return;

		ParticleBuilder lines = new ParticleBuilder(10);
		lines.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		lines.setScaleFunction(new InterpScale(0.5f, 0));
		lines.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
		ParticleSpawner.spawn(lines, world, new InterpLine(origin, target), (int) target.distanceTo(origin) * 4, 0, (aFloat, particleBuilder) -> {
			lines.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			lines.setLifetime(RandUtil.nextInt(10, 20));
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster(world);
		Vec3d origin = data.getOrigin(world);
		Vec3d target;

		if (look == null) return data;
		if (caster == null) return data;
		if (origin == null) return data;

		double interpPosX = caster.lastTickPosX + (caster.posX - caster.lastTickPosX) * partialTicks;
		double interpPosY = caster.lastTickPosY + (caster.posY - caster.lastTickPosY) * partialTicks;
		double interpPosZ = caster.lastTickPosZ + (caster.posZ - caster.lastTickPosZ) * partialTicks;

		double dist = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		RayTraceResult result = new RayTrace(
				world, look, new Vec3d(interpPosX, interpPosY + caster.getEyeHeight(), interpPosZ), dist)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		target = data.getTarget(world);
		if (target == null) return data;

		RenderUtils.drawCircle(target, dist / 4.0, true, true);

		return data;
	}

	/////////////
	
	@ModuleOverride("shape_cone_run")
	public void onRunCone(World world, SpellData data, SpellRing shape) {
		// Default implementation
	}
	
	@ModuleOverride("shape_cone_render")
	public boolean onRenderCone(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
}
