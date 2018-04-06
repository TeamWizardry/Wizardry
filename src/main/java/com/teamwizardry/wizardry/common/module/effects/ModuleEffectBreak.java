package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectBreak extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_break";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseAOE(), new ModuleModifierIncreasePotency()};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(SpellData.DefaultKeys.BLOCK_HIT);
		Entity targetEntity = spell.getVictim();
		Entity caster = spell.getCaster();

		double range = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double strength = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);

		if (targetEntity instanceof EntityLivingBase)
			for (ItemStack stack : targetEntity.getArmorInventoryList())
				stack.damageItem((int) strength, (EntityLivingBase) targetEntity);
		if (targetPos != null) {

			Block block = world.getBlockState(targetPos).getBlock();
			HashSet<BlockPos> branch = new HashSet<>();
			HashSet<BlockPos> blocks = new HashSet<>();
			branch.add(targetPos);
			blocks.add(targetPos);
			getBlocks(spell.world, block, (int) range, branch, blocks);
			for (BlockPos pos : blocks) {

				float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
				if (hardness >= 0 && hardness < strength) {
					if (!spellRing.taxCaster(spell)) return false;
					BlockUtils.breakBlock(world, pos, null, caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null, true);
				}
			}
		}
		return true;
	}

	private void getBlocks(World world, Block block, int maxBlocks, HashSet<BlockPos> step, HashSet<BlockPos> allBlocks) {
		if (allBlocks.size() >= maxBlocks) return;

		HashSet<BlockPos> newSteps = new HashSet<>();

		for (BlockPos stepPos : step) {
			for (EnumFacing facing : PosUtils.symmetricFacingValues) {

				BlockPos nextStep = stepPos.offset(facing);

				BlockPos immut = nextStep.toImmutable();
				if (allBlocks.contains(immut)) continue;
				if (!world.isBlockLoaded(nextStep)) continue;

				IBlockState state = world.getBlockState(nextStep);
				if (state.getBlock() != block) continue;

				boolean sideSolid = false;
				for (EnumFacing dir : PosUtils.symmetricFacingValues) {
					BlockPos adjPos = stepPos.offset(dir);
					IBlockState adjState = world.getBlockState(adjPos);
					if (!adjState.isSideSolid(world, adjPos, dir.getOpposite())) {
						sideSolid = true;
						break;
					}
				}
				if (!sideSolid) continue;

				newSteps.add(nextStep);
				allBlocks.add(nextStep);

				if (allBlocks.size() >= maxBlocks) return;
			}
		}

		if (newSteps.isEmpty()) return;

		getBlocks(world, block, maxBlocks, newSteps, allBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}
}
