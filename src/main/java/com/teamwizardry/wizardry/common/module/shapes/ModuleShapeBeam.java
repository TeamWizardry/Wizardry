package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleShapeBeam extends ModuleShape implements IContinuousModule {

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
	public boolean ignoreResult() {
		return true;
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) throws NullPointerException {
		World world = spell.world;
		Vec3d look = spell.getData(LOOK);
		Vec3d position = spell.getOrigin();
		Entity caster = spell.getCaster();

		if (look == null || position == null) return false;

		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double potency = 30 - spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

		spellRing.multiplyMultiplierForAll((float) (potency / 25.0 * range / 75.0));

		RayTraceResult trace = new RayTrace(world, look, position, range)
				.setSkipEntity(caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		spell.processTrace(trace, look.scale(range));

		sendRenderPacket(spell, spellRing);
		if (spell.world.getTotalWorldTime() % potency == 0) {
			if (spellRing.getChildRing() != null) {
				spellRing.getChildRing().runSpellRing(spell);
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d target = spell.getTargetWithFallback();

		if (target == null) return;

		LibParticles.SHAPE_BEAM(world, target, spell.getOriginHand(), spellRing.getPrimaryColor());
	}
}
