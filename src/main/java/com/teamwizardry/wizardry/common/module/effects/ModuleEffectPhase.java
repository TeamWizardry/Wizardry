package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.common.core.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectPhase extends ModuleEffect implements IDelayedModule {

	@Nonnull
	@Override
	public String getID() {
		return "effect_phase";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseDuration(), new ModuleModifierIncreaseAOE(), new ModuleModifierIncreaseRange()};
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity caster = spell.getCaster();
		Entity targetEntity = spell.getVictim();
		Vec3d targetHit = spell.getTarget();
		BlockPos targetPos = spell.getTargetPos();
		EnumFacing faceHit = spell.getFaceHit();

		double duration = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 20;
		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);

		if (!spellRing.taxCaster(spell)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) targetEntity;
			entity.addPotionEffect(new PotionEffect(ModPotions.PHASE, (int) duration, 0, true, false));
			spell.world.playSound(null, targetEntity.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 1, 1);
		}

		if (targetPos != null && faceHit != null) {
			NemezTracker tracker = new NemezTracker();

			Vec3d centerStart = new Vec3d(targetPos);
			Vec3d centerEnd = centerStart.add(new Vec3d(faceHit.getDirectionVec()).scale(range));

			AxisAlignedBB bb = new AxisAlignedBB(centerStart, centerEnd);

			switch (faceHit) {
				case DOWN:
				case UP:
					bb.grow(area, 0, area);
					break;
				case NORTH:
				case SOUTH:
					bb.grow(area, area, 0);
					break;
				case WEST:
				case EAST:
					bb.grow(0, area, area);
					break;
			}

			for (BlockPos pos : BlockPos.getAllInBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ)) {
				tracker.trackBlock(pos, spell.world.getBlockState(pos));

				IBlockState state = ModBlocks.FAKE_AIR.getDefaultState();

				spell.world.setBlockState(pos, state);

				tracker.trackBlock(pos, state);
			}

			spell.addData(SpellData.DefaultKeys.NEMEZ, tracker);

			addDelayedSpell(spellRing, spell, (int) duration);
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

	}

	@Override
	public void runDelayedEffect(SpellData spell, SpellRing spellRing) {
		NemezTracker nemez = spell.getData(SpellData.DefaultKeys.NEMEZ);
		BlockPos targetPos = spell.getTargetPos();

		if (nemez != null && targetPos != null) {
			NemezEventHandler.reverseTime(spell.world, nemez, targetPos);
		}
	}
}
