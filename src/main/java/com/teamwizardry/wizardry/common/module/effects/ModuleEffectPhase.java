package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.client.core.StructureErrorRenderer;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
			faceHit = faceHit.getOpposite();
			NemezTracker nemezDrive = new NemezTracker();

			AxisAlignedBB bb = new AxisAlignedBB(targetPos);

			switch (faceHit) {
				case DOWN:
					bb = bb.grow(area, 0, area);
					bb = bb.expand(0, -range, 0);
					break;
				case UP:
					bb = bb.grow(area, 0, area);
					bb = bb.expand(0, range, 0);
					break;
				case NORTH:
					bb = bb.grow(area, area, 0);
					bb = bb.expand(0, 0, -range);
					break;
				case SOUTH:
					bb = bb.grow(area, area, 0);
					bb = bb.expand(0, 0, range);
					break;
				case WEST:
					bb = bb.grow(0, area, area);
					bb = bb.expand(-range, 0, 0);
					break;
				case EAST:
					bb = bb.grow(0, area, area);
					bb = bb.expand(range, 0, 0);
					break;
			}

			StructureErrorRenderer.INSTANCE.addError(targetPos);
			for (BlockPos pos : BlockPos.getAllInBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ)) {
				IBlockState originalState = spell.world.getBlockState(pos);
				if (originalState.getBlock() == ModBlocks.FAKE_AIR || originalState.getBlock() == Blocks.AIR) continue;


				nemezDrive.trackBlock(pos, originalState);

				IBlockState state = ModBlocks.FAKE_AIR.getDefaultState();

				BlockUtils.placeBlock(spell.world, pos, state, (EntityPlayerMP) caster);

				nemezDrive.trackBlock(pos, state);
			}

			nemezDrive.endUpdate();

			spell.addData(SpellData.DefaultKeys.NEMEZ, nemezDrive);

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
		NemezTracker nemezDrive = spell.getData(SpellData.DefaultKeys.NEMEZ);
		BlockPos targetPos = spell.getTargetPos();

		if (nemezDrive != null && targetPos != null) {
			NemezEventHandler.reverseTime(spell.world, nemezDrive, targetPos);
		}
	}
}
