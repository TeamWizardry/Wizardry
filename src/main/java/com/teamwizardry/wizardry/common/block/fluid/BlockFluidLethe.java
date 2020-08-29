package com.teamwizardry.wizardry.common.block.fluid;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.teamwizardry.librarianlib.features.base.fluid.BlockModFluid;
import com.teamwizardry.librarianlib.features.base.fluid.ModFluid;
import com.teamwizardry.librarianlib.features.forgeevents.EntityUpdateEvent;
import com.teamwizardry.wizardry.api.block.FluidTracker;
import com.teamwizardry.wizardry.crafting.mana.FluidRecipeBuilder;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockFluidLethe extends BlockModFluid {

	public BlockFluidLethe(ModFluid lethe) {
		super(lethe, new MaterialLethe(MapColor.WATER));
		setQuantaPerBlock(6);
	}

	@SubscribeEvent
	public static void onEntityUpdate(EntityUpdateEvent event) {
		Entity entityIn = event.getEntity();
		BlockPos pos = entityIn.getPosition();
		World world = entityIn.world;
		IBlockState state = world.getBlockState(pos);
//		if (state.getBlock() == ModFluids.LETHE.getActualBlock()) {
//
//			run(world, pos, state.getBlock(), entityIn,
//					entity -> entity instanceof EntityPlayer,
//					entity -> {
//						EntityPlayer player = (EntityPlayer) entity;
//						if (player.experienceLevel > 0)
//							CapManager.forObject(player).addMana(expToNextLevel(--player.experienceLevel));
//					});
//
//		}

		run(world, pos, state.getBlock(), entityIn,
				entity -> entity instanceof EntityItem && ManaRecipes.RECIPES.keySet().stream().anyMatch(item -> item.apply(((EntityItem) entity).getItem())),
				entity -> {
					List<Map.Entry<Ingredient, FluidRecipeBuilder.FluidCrafter>> allEntries = ManaRecipes.RECIPES.entries().stream().filter(entry ->
							entry.getValue().getFluid().getBlock() == state.getBlock() &&
									entry.getKey().apply(((EntityItem) entity).getItem())).collect(Collectors.toList());
					allEntries.forEach(crafter -> FluidTracker.INSTANCE.addManaCraft(entity.world, entity.getPosition(), crafter.getValue().build()));
				});
	}

	public static void run(World world, BlockPos pos, Block block, Entity entity, Predicate<Entity> test, Consumer<Entity> process) {
		if (!(block instanceof IFluidBlock)) return;
		float height;
		IBlockState up = world.getBlockState(pos.up());
		if (up.getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock)
			height = 1f;
		else
			height = ((IFluidBlock) block).getFilledPercentage(world, pos) * 0.875f;
		AxisAlignedBB bb = new AxisAlignedBB(pos).contract(0, 1 - height, 0);
		AxisAlignedBB entityBox = entity.getCollisionBoundingBox();
		if ((entityBox == null || entityBox.intersects(bb))
				&& test.test(entity)) process.accept(entity);
	}
	
	private static int expToNextLevel(int currentLevel)
	{
		if (currentLevel > 30)
			return 9 * currentLevel - 158;
		if (currentLevel > 15)
			return 5 * currentLevel - 38;
		return 2 * currentLevel + 7;
	}
}
