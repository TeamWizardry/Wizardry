package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
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
public class ModuleShapeBeam extends ModuleShape implements IContinuousModule {

	public static final String BEAM_OFFSET = "beam offset";
	public static final String BEAM_CAST = "beam cast";

	@Nonnull
	@Override
	public String getID() {
		return "shape_beam";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseRange(), new ModuleModifierIncreasePotency()};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRunOverrides(spell, spellRing)) return true;

		World world = spell.world;
		Vec3d look = spell.getData(LOOK);
		Vec3d position = spell.getOrigin();
		Entity caster = spell.getCaster();

		if (look == null || position == null) return false;

		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

		NBTTagCompound info = spellRing.getInformationTag();
		double beamOffset = info.getDouble(BEAM_OFFSET) + potency;

		while (beamOffset >= ConfigValues.beamTimer) {
			beamOffset -= ConfigValues.beamTimer;
			if (!spellRing.taxCaster(spell)) {
				info.setDouble(BEAM_OFFSET, beamOffset % ConfigValues.beamTimer);
				return false;
			}

			RayTraceResult trace = new RayTrace(world, look, position, range)
					.setSkipEntity(caster)
					.setReturnLastUncollidableBlock(true)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.trace();

			spell.processTrace(trace, look.scale(range));

			if (spellRing.getChildRing() != null)
				spellRing.getChildRing().runSpellRing(spell);
		}

		sendRenderPacket(spell, spellRing);
		info.setDouble(BEAM_OFFSET, beamOffset);
		return true;
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		World world = data.world;
		Vec3d look = data.getData(LOOK);
		Vec3d position = data.getOrigin();
		Entity caster = data.getCaster();

		if (look == null || position == null) return previousData;

		double range = ring.getAttributeValue(AttributeRegistry.RANGE, data);
		double potency = ring.getAttributeValue(AttributeRegistry.POTENCY, data);

		NBTTagCompound info = ring.getInformationTag();
		double beamOffset = info.getDouble(BEAM_OFFSET) + potency;

		while (beamOffset >= ConfigValues.beamTimer) {
			beamOffset -= ConfigValues.beamTimer;
			if (!ring.taxCaster(data)) {
				info.setDouble(BEAM_OFFSET, beamOffset % ConfigValues.beamTimer);
				return previousData;
			}

			RayTraceResult trace = new RayTrace(world, look, position, range)
					.setSkipEntity(caster)
					.setReturnLastUncollidableBlock(true)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.trace();

			data.processTrace(trace, look.scale(range));

			BlockPos pos = data.getTargetPos();
			if (pos == null) return previousData;

			previousData.processTrace(trace, look.scale(range));
		}

		return previousData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRenderOverrides(spell, spellRing)) return;

		World world = spell.world;
		Vec3d target = spell.getTargetWithFallback();

		if (target == null) return;

		LibParticles.SHAPE_BEAM(world, target, spell.getOriginHand(), RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
	}
}
