package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.common.entity.projectile.EntitySpellProjectile;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseSpeed;
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
@RegisterModule
public class ModuleShapeProjectile extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseRange(), new ModuleModifierIncreaseSpeed()};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		if (world.isRemote) return true;

		Vec3d origin = spell.getOriginWithFallback();
		if (origin == null) return false;

		double dist = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double speed = spellRing.getAttributeValue(AttributeRegistry.SPEED, spell);

		EntitySpellProjectile proj = new EntitySpellProjectile(world, spellRing, spell, (float) dist, (float) speed, (float) 0.1);
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		if (!spellRing.taxCaster(spell)) return false;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
		return success;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (spellRing.isRunBeingOverriden()) {
			runRenderOverrides(spell, spellRing);
		}
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster();
		Vec3d origin = data.getOrigin();

		if (look == null) return previousData;
		if (caster == null) return previousData;
		if (origin == null) return previousData;

		double dist = ring.getAttributeValue(AttributeRegistry.RANGE, data);

		RayTraceResult result = new RayTrace(
				data.world, look, caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0), dist)
				.setSkipEntity(caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		if (pos == null) return previousData;

		previousData.processTrace(result);

		return previousData;
	}
}
