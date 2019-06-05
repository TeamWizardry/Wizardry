package com.teamwizardry.wizardry.common.module.shapes;

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
import com.teamwizardry.wizardry.common.entity.projectile.EntitySpellProjectile;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_projectile")
public class ModuleShapeProjectile implements IModuleShape {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_range", "modifier_increase_speed"};
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
		if (world.isRemote) return true;

		Vec3d origin = spell.getOriginWithFallback(world);
		if (origin == null) return false;

		double dist = spellRing.getAttributeValue(world, AttributeRegistry.RANGE, spell);
		double speed = spellRing.getAttributeValue(world, AttributeRegistry.SPEED, spell);

		if (!spellRing.taxCaster(world, spell, true)) return false;
		
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);

		EntitySpellProjectile proj = new EntitySpellProjectile(world, spellRing, spell, (float) dist, (float) speed, (float) 0.1, !overrides.onRunProjectile(world, spell, spellRing));
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		overrides.onRenderProjectile(world, spell, spellRing);
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

		double dist = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		double interpPosX = caster.lastTickPosX + (caster.posX - caster.lastTickPosX) * partialTicks;
		double interpPosY = caster.lastTickPosY + (caster.posY - caster.lastTickPosY) * partialTicks;
		double interpPosZ = caster.lastTickPosZ + (caster.posZ - caster.lastTickPosZ) * partialTicks;

		RayTraceResult result = new RayTrace(
				world, look, new Vec3d(interpPosX, interpPosY + caster.getEyeHeight(), interpPosZ), dist)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		target = data.getTarget(world);
		if (target == null) return data;
		RenderUtils.drawCircle(target, 0.3, true, false, caster, partialTicks);

		return data;
	}
	
	///////////
	
	@ModuleOverride("shape_projectile_run")
	public boolean onRunProjectile(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;		
	}

	
	@ModuleOverride("shape_projectile_render")
	public boolean onRenderProjectile(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
}
