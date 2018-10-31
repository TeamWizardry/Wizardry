package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
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

	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_extend_range", "modifier_increase_speed"};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		if (world.isRemote) return true;

		Vec3d origin = spell.getOriginWithFallback();
		if (origin == null) return false;

		double dist = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double speed = spellRing.getAttributeValue(AttributeRegistry.SPEED, spell);

		if (!spellRing.taxCaster(spell, true)) return false;
		
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		
		EntitySpellProjectile proj = new EntitySpellProjectile(world, spellRing, spell, (float) dist, (float) speed, (float) 0.1, /*!instance.runRunOverrides(spell, spellRing)*/overrides.onRunProjectile(spell, spellRing));
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
		return success;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		overrides.onRenderProjectile(spell, spellRing);
//		if (spellRing.isRunBeingOverriden()) {
//			instance.runRenderOverrides(spell, spellRing);
//		}
	}

	@NotNull
	@Override
	public SpellData renderVisualization(ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster();
		Vec3d origin = data.getOrigin();

		if (look == null) return previousData;
		if (caster == null) return previousData;
		if (origin == null) return previousData;

		double dist = ring.getAttributeValue(AttributeRegistry.RANGE, data);

		RayTraceResult result = new RayTrace(
				data.world, look, caster.getPositionVector().add(0, caster.getEyeHeight(), 0), dist)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		if (pos == null) return previousData;

		previousData.processTrace(result);

		return previousData;
	}
	
	///////////
	
	@ModuleOverride("shape_projectile_run")
	public boolean onRunProjectile(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		// Default implementation
		return false;		
	}

	
	@ModuleOverride("shape_projectile_render")
	public boolean onRenderProjectile(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		// Default implementation
		return false;
	}
}
