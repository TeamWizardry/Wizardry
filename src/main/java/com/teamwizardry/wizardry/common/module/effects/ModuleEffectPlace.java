package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectPlace extends ModuleEffect implements IBlockSelectable {

	@Nonnull
	@Override
	public String getID() {
		return "effect_place";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseAOE()};
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster();
		EnumFacing facing = spell.getData(FACE_HIT);

		if (facing == null) return true;

		Set<EnumFacing> facings = new HashSet<>();
		for (EnumFacing facing1 : EnumFacing.VALUES) {
			if (facing1 == facing || facing1 == facing.getOpposite()) continue;
			facings.add(facing1);
		}

		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

		if (targetPos == null) return true;

		if (caster != null && caster.getEntityData().hasKey("selected")) {
			IBlockState state = NBTUtil.readBlockState(caster.getEntityData().getCompoundTag("selected"));
			Block block = world.getBlockState(targetPos).getBlock();

			HashSet<BlockPos> branch = new HashSet<>();
			HashSet<BlockPos> blocks = new HashSet<>();
			branch.add(targetPos);
			blocks.add(targetPos);
			getBlocks(world, block, facings, (int) area, branch, blocks);
			for (BlockPos ignored : blocks) {

				BlockPos pos = ignored.offset(facing);

				ItemStack stackBlock = null;
				for (ItemStack stack : ((EntityPlayer) caster).inventory.mainInventory) {
					if (stack.isEmpty()) continue;
					if (!(stack.getItem() instanceof ItemBlock)) continue;

					ItemStack stack1 = state.getBlock().getItem(world, null, state);
					if (stack1.isEmpty()) continue;
					if (!ItemStack.areItemsEqual(stack, stack1)) continue;
					stackBlock = stack;
					break;
				}

				if (stackBlock == null) return true;

				if (!world.isAirBlock(pos)) continue;
				if (!spellRing.taxCaster(spell)) return false;
				//stackBlock.shrink(1);
				IBlockState oldState = world.getBlockState(pos);

				BlockUtils.placeBlock(world, pos, facing, stackBlock);
				world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, caster).getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
				((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));
			}
		} else if (caster == null) {
			if (!world.isAirBlock(targetPos)) return true;
			if (!spellRing.taxCaster(spell)) return false;

			List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(targetPos).grow(3, 3, 3));
			if (items.isEmpty()) return true;

			EntityItem item = items.get(0);
			if (item == null) return true;

			if (item.getItem().getItem() instanceof ItemBlock) {
				item.getItem().shrink(1);

				BlockUtils.placeBlock(world, targetPos, facing, item.getItem());
				world.playSound(null, targetPos, ((ItemBlock) item.getItem().getItem()).getBlock().getSoundType(((ItemBlock) item.getItem().getItem()).getBlock().getDefaultState(), world, targetPos, caster).getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
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
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}
}
