package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeZone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
@RegisterModule(ID="effect_place")
public class ModuleEffectPlace implements IModuleEffect, IBlockSelectable {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity caster = spell.getCaster(world);
		EnumFacing facing = spell.getData(FACE_HIT);

		if (facing == null || targetPos == null) return false;

		double area = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);

		if (caster instanceof EntityPlayer) {
			IBlockState selected = instance.getSelectedBlockState((EntityPlayer) caster);
			if (selected == null) return true;

			IBlockState targetState = world.getBlockState(targetPos);
			List<ItemStack> stacks = instance.getAllOfStackFromInventory((EntityPlayer) caster, selected);
			if (stacks.isEmpty()) return true;
			
			int stackCount = instance.getCountOfStacks(stacks);
			Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, Math.min(stackCount, (int) area), MathHelper.ceil(MathHelper.sqrt(area)) / 2, pos -> {
				BlockPos front = pos.offset(facing);
				if (!world.getBlockState(front).getBlock().isReplaceable(world, front)) return true;

				IBlockState state = world.getBlockState(pos);
				return state.getBlock() != targetState.getBlock();
			});
			if (blocks.isEmpty()) {
				blocks.add(targetPos);
			}

			for (BlockPos pos : blocks) {
				if (!spellRing.taxCaster(world, spell, 1 / area, false)) return false;

				ItemStack availableStack = instance.getAvailableStack(stacks);
				if (availableStack == null) return true;

				BlockUtils.placeBlock(world, pos, facing, availableStack);
			}

		} else {
			List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(targetPos).grow(3, 3, 3));
			if (items.isEmpty()) return true;

			EntityItem item = items.get(0);
			if (item == null) return true;

			if (item.getItem().getItem() instanceof ItemBlock) {
				if (!spellRing.taxCaster(world, spell, true)) return false;
				BlockUtils.placeBlock(world, targetPos, facing, item.getItem());
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EXPLODE(world, position, instance.getPrimaryColor(), instance.getSecondaryColor(), 0.2, 0.3, 20, 40, 10, true);
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceEffect instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		if (ring.getParentRing() != null
				&& ring.getParentRing().getModule() != null
				&& ring.getParentRing().getModule().getModuleClass() instanceof ModuleShapeZone)
			return data;

		BlockPos targetPos = data.getData(SpellData.DefaultKeys.BLOCK_HIT);
		EnumFacing facing = data.getFaceHit();
		Entity caster = data.getCaster(world);

		if (facing == null || targetPos == null) return data;

		double area = ring.getAttributeValue(world, AttributeRegistry.AREA, data);

		if (caster instanceof EntityPlayer) {
			IBlockState selected = instance.getSelectedBlockState((EntityPlayer) caster);
			if (selected == null) return data;

			IBlockState targetState = world.getBlockState(targetPos);
			List<ItemStack> stacks = instance.getAllOfStackFromInventory((EntityPlayer) caster, selected);
			if (stacks.isEmpty()) return data;

			int stackCount = instance.getCountOfStacks(stacks);
			Set<BlockPos> blocks = BlockUtils.blocksInSquare(targetPos, facing, Math.min(stackCount, (int) area), (int) ((Math.sqrt(area)+1)/2), pos -> {
				if (BlockUtils.isAnyAir(targetState)) return true;

				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
				IBlockState adjacentState = instance.getCachableBlockstate(world, mutable.offset(facing), data);
				if (adjacentState.getBlock() != Blocks.AIR) return true;

				IBlockState state = instance.getCachableBlockstate(world, pos, data);
				return state.getBlock() != targetState.getBlock();
			});

			if (blocks.isEmpty()) {
				if (targetState.getBlock() == Blocks.AIR)
					RenderUtils.drawCubeOutline(world, targetPos, targetState);
				else RenderUtils.drawFaceOutline(targetPos, facing);
			} else
				for (BlockPos areaPos : blocks) {
					BlockPos pos = areaPos.offset(facing);

					BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
					for (EnumFacing facing1 : EnumFacing.VALUES) {

						mutable.move(facing1);

						if (blocks.contains(mutable)) {

							RenderUtils.drawFaceOutline(mutable, facing1.getOpposite());
						}
						mutable.move(facing1.getOpposite());
					}
				}
		}
		return data;
	}
}
