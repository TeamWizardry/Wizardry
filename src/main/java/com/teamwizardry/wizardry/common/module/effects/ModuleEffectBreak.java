package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBreak extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_break";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Break";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will break blocks and damage armor";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getData(CASTER);

		double range = getModifier(spell, Attributes.AREA, 1, 64);
		double strength = getModifier(spell, Attributes.POTENCY, 1, 64);

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
					if (!tax(this, spell)) return false;
					BlockUtils.breakBlock(world, pos, null, caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null, true);
				}
			}
		}
		return true;
	}

	private void getBlocks(World world, Block block, int maxBlocks, HashSet<BlockPos> branch, HashSet<BlockPos> allBlocks) {
		if (allBlocks.size() >= maxBlocks) return;

		HashSet<BlockPos> newBranch = new HashSet<>();

		for (BlockPos branchPos : branch) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos posAdj = branchPos.offset(facing);
				IBlockState state = world.getBlockState(posAdj);

				if (!world.isBlockLoaded(posAdj)) continue;
				if (allBlocks.contains(posAdj)) continue;
				if (state.getBlock() != block) continue;

				boolean sideSolid = false;
				for (EnumFacing dir : PosUtils.symmetricFacingValues) {
					BlockPos adjPos = branchPos.offset(dir);
					IBlockState adjState = world.getBlockState(adjPos);
					if (!adjState.isSideSolid(world, adjPos, dir.getOpposite())) {
						sideSolid = true;
						break;
					}
				}
				if (!sideSolid) continue;

				if (allBlocks.size() >= maxBlocks) return;

				newBranch.add(posAdj);
				allBlocks.add(posAdj);
			}
		}
		boolean mismatched = false;
		for (BlockPos pos : branch) if (!newBranch.contains(pos)) mismatched = true;
		if (mismatched)
			getBlocks(world, block, maxBlocks, newBranch, allBlocks);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectBreak());
	}
}
