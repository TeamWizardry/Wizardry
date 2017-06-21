package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectPlace extends ModuleEffect implements IBlockSelectable {

	@Nonnull
	@Override
	public String getID() {
		return "effect_place";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Place";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will place the block selected";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Vec3d originPos = spell.getData(ORIGIN);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		EnumFacing facing = spell.getData(FACE_HIT);
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);

		if (facing == null) return false;

		Set<EnumFacing> facings = new HashSet<>();
		for (EnumFacing facing1 : EnumFacing.VALUES) {
			if (facing1 == facing || facing1 == facing.getOpposite()) continue;
			facings.add(facing1);
		}

		double range = getModifierPower(spell, Attributes.INCREASE_AOE, 1, 64, true, true);
		range = 64;

		if (caster != null && targetPos != null && caster.getEntityData().hasKey("selected")) {
			IBlockState state = NBTUtil.readBlockState(caster.getEntityData().getCompoundTag("selected"));
			Block block = world.getBlockState(targetPos).getBlock();

			HashSet<BlockPos> branch = new HashSet<>();
			HashSet<BlockPos> blocks = new HashSet<>();
			branch.add(targetPos);
			blocks.add(targetPos);
			getBlocks(world, block, facings, (int) range, branch, blocks);
			for (BlockPos ignored : blocks) {

				BlockPos pos = ignored.offset(facing);

				ItemStack stackBlock = null;
				for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
					if (stack.isEmpty()) continue;
					if (!(stack.getItem() instanceof ItemBlock)) continue;
					Block temp = ((ItemBlock) stack.getItem()).getBlock();
					if (temp != state.getBlock()) continue;
					stackBlock = stack;
					break;
				}

				if (stackBlock == null) return false;

				if (!world.isAirBlock(pos)) continue;
				if (!tax(this, spell)) continue;
				stackBlock.shrink(1);
				IBlockState oldState = world.getBlockState(pos);

				BlockUtils.placeBlock(world, pos, state, (EntityPlayerMP) caster);
				world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, caster).getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
				((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));
			}
		}
		return true;
	}

	private void getBlocks(World world, Block block, Set<EnumFacing> facingSet, int maxBlocks, HashSet<BlockPos> branch, HashSet<BlockPos> allBlocks) {
		if (allBlocks.size() >= maxBlocks) return;

		HashSet<BlockPos> newBranch = new HashSet<>();

		for (BlockPos branchPos : branch) {
			for (EnumFacing facing : facingSet) {
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
			getBlocks(world, block, facingSet, maxBlocks, newBranch, allBlocks);
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectPlace());
	}
}
