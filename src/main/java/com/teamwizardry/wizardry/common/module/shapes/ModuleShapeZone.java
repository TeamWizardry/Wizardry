package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
public class ModuleShapeZone extends ModuleShape implements ILingeringModule {

	@Nonnull
	@Override
	public String getID() {
		return "shape_zone";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseAOE(), new ModuleModifierIncreasePotency(), new ModuleModifierIncreaseRange(), new ModuleModifierIncreaseDuration()};
	}

	@Override
	public boolean ignoreResult() {
		return true;
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getCaster();
		Vec3d targetPos = spell.getTarget();

		if (targetPos == null) return false;

		double aoe = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double strength = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);
		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);

		spellRing.multiplyMultiplierForAll((float) (aoe / 7.0 * strength / 15.0 * range / 15.0));

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(targetPos)).grow(aoe, 1, aoe));

		if (RandUtil.nextInt((int) ((70 - strength))) == 0) {
			if (!spellRing.taxCaster(spell)) return false;
			for (Entity entity : entities) {
				if (entity.getDistance(targetPos.x, targetPos.y, targetPos.z) <= aoe) {
					Vec3d vec = targetPos.addVector(RandUtil.nextDouble(-strength, strength), RandUtil.nextDouble(range), RandUtil.nextDouble(-strength, strength));

					SpellData copy = spell.copy();
					copy.processEntity(entity, false);
					copy.addData(YAW, entity.rotationYaw);
					copy.addData(PITCH, entity.rotationPitch);
					copy.addData(ORIGIN, vec);

					if (spellRing.getChildRing() != null) {
						spellRing.getChildRing().runSpellRing(spell);
					}
				}
			}
		}

		if (RandUtil.nextInt((int) ((40 - strength))) != 0) return false;

		ArrayList<Vec3d> blocks = new ArrayList<>();
		for (double i = -aoe; i < aoe; i++)
			for (double j = 0; j < range; j++)
				for (double k = -aoe; k < aoe; k++) {
					Vec3d pos = targetPos.addVector(i, j, k);
					if (pos.distanceTo(targetPos) <= aoe) {
//						BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(new BlockPos(pos));
						blocks.add(pos);
					}
				}
		if (blocks.isEmpty()) return false;
		if (!spellRing.taxCaster(spell)) return false;
		Vec3d pos = blocks.get(RandUtil.nextInt(blocks.size() - 1));

		SpellData copy = spell.copy();
		copy.addData(ORIGIN, pos);
		copy.processBlock(new BlockPos(pos), EnumFacing.UP, pos);
		copy.addData(YAW, RandUtil.nextFloat(-180, 180));
		copy.addData(PITCH, RandUtil.nextFloat(-50, 50));

		if (spellRing.getChildRing() != null) {
			spellRing.getChildRing().runSpellRing(copy);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d target = spell.getTarget();

		if (target == null) return;
		if (RandUtil.nextInt(10) != 0) return;

		double aoe = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) aoe, 1, RandUtil.nextFloat()), (int) (aoe * 5), 0, (aFloat, particleBuilder) -> {
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			if (RandUtil.nextBoolean()) {
				glitter.setColor(getPrimaryColor());
			} else {
				glitter.setColor(getSecondaryColor());
			}
			glitter.addMotion(new Vec3d(
					RandUtil.nextDouble(-0.001, 0.001),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.001, 0.001)
			));
		});
	}

	@Override
	public int getLingeringTime(SpellData spell, SpellRing spellRing) {
		double strength = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
		return (int) strength;
	}
}
