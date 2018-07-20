package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;
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

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getCaster();
		EnumFacing facing = spell.getData(FACE_HIT);

		if (facing == null || targetPos == null) return true;

		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

		if (caster instanceof EntityPlayer) {
			IBlockState selected = getSelectedBlockState((EntityPlayer) caster);
			if (selected == null) return true;

			IBlockState targetState = world.getBlockState(targetPos);
			List<ItemStack> stacks = getAllOfStackFromInventory((EntityPlayer) caster, selected);
			if (stacks.isEmpty()) return true;
			
			int stackCount = getCountOfStacks(stacks);
			Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, Math.min(stackCount, (int) area), (int) ((Math.sqrt(area)+1)/2), pos -> {
				if (BlockUtils.isAnyAir(targetState)) return true;

				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
				IBlockState adjacentState = world.getBlockState(mutable.offset(facing));
				if (adjacentState.getBlock() != Blocks.AIR) return true;

				IBlockState state = world.getBlockState(pos);
				return state.getBlock() != targetState.getBlock();
			});
			if (blocks.isEmpty()) {
				blocks.add(targetPos);
			}

			for (BlockPos areaPos : blocks) {
				if (!spellRing.taxCaster(spell, 1/area)) return false;
				
				BlockPos pos = blocks.size() > 1 ? areaPos.offset(facing) : areaPos;

				IBlockState oldState = world.getBlockState(pos);

				ItemStack availableStack = getAvailableStack(stacks);
				if (availableStack == null) return true;

				BlockUtils.placeBlock(world, pos, facing, availableStack);
				world.playSound(null, pos, selected.getBlock().getSoundType(selected, world, pos, caster).getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
				((EntityPlayer) caster).inventory.addItemStackToInventory(new ItemStack(oldState.getBlock().getItemDropped(oldState, spell.world.rand, 0)));
			}

		} else {
			if (!BlockUtils.isAnyAir(world, targetPos)) return true;

			List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(targetPos).grow(3, 3, 3));
			if (items.isEmpty()) return true;

			EntityItem item = items.get(0);
			if (item == null) return true;

			if (item.getItem().getItem() instanceof ItemBlock) {
				if (!spellRing.taxCaster(spell)) return false;
				BlockUtils.placeBlock(world, targetPos, facing, item.getItem());
				world.playSound(null, targetPos, ((ItemBlock) item.getItem().getItem()).getBlock().getSoundType(((ItemBlock) item.getItem().getItem()).getBlock().getDefaultState(), world, targetPos, caster).getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		LibParticles.EXPLODE(world, position, getPrimaryColor(), getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		if (ring.getParentRing() != null
				&& ring.getParentRing().getModule() != null
				&& ring.getParentRing().getModule() == ModuleRegistry.INSTANCE.getModule("event_collide_entity"))
			return previousData;

		BlockPos targetPos = data.getData(SpellData.DefaultKeys.BLOCK_HIT);
		EnumFacing facing = data.getFaceHit();
		Entity caster = data.getCaster();

		if (facing == null || targetPos == null) return previousData;

		double area = ring.getAttributeValue(AttributeRegistry.AREA, data);

		if (caster instanceof EntityPlayer) {
			IBlockState selected = getSelectedBlockState((EntityPlayer) caster);
			if (selected == null) return previousData;

			IBlockState targetState = data.world.getBlockState(targetPos);
			List<ItemStack> stacks = getAllOfStackFromInventory((EntityPlayer) caster, selected);
			if (stacks.isEmpty()) return previousData;

			int stackCount = getCountOfStacks(stacks);
			Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, Math.min(stackCount, (int) area), (int) ((Math.sqrt(area)+1)/2), pos -> {
				if (BlockUtils.isAnyAir(targetState)) return true;

				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
				IBlockState adjacentState = getCachableBlockstate(data.world, mutable.offset(facing), previousData);
				if (adjacentState.getBlock() != Blocks.AIR) return true;

				IBlockState state = getCachableBlockstate(data.world, pos, previousData);
				return state.getBlock() != targetState.getBlock();
			});

			if (blocks.isEmpty()) {
				if (targetState.getBlock() == Blocks.AIR)
					drawCubeOutline(data.world, targetPos, targetState);
				else drawFaceOutline(targetPos, facing);
			} else
				for (BlockPos areaPos : blocks) {
					BlockPos pos = areaPos.offset(facing);

					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
					for (EnumFacing facing1 : EnumFacing.VALUES) {

						mutable.move(facing1);

						if (blocks.contains(mutable)) {

							drawFaceOutline(mutable, facing1.getOpposite());
						}
						mutable.move(facing1.getOpposite());
					}
				}
		}
		return previousData;
	}
}
