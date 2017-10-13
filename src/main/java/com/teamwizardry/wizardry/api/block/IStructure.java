package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by LordSaad.
 */
public interface IStructure {

	CachedStructure getStructure();

	Vec3i offsetToCenter();

	static ItemStack craftItemFromInventory(EntityPlayer player, ItemStack output) {
		HashSet<IRecipe> recipes = getRecipesForItem(output);
		if (recipes.isEmpty()) return ItemStack.EMPTY;

		HashMap<ItemStack, Integer> finalInv = new HashMap<>();
		IRecipe finalRecipe = null;
		mainLoop:
		for (IRecipe recipe : recipes) {
			if (recipe == null) continue;
			finalInv.clear();
			finalRecipe = recipe;

			ingLoop:
			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.getMatchingStacks().length <= 0) continue;

				// Check if has items
				{
					HashMap<Item, Integer> countStacks = new HashMap<>();
					HashMap<Item, Integer> countAgainstStacks = new HashMap<>();
					for (ItemStack stack : player.inventory.mainInventory) {
						if (stack.isEmpty()) continue;
						countStacks.put(stack.getItem(), countStacks.getOrDefault(stack.getItem(), 0) + stack.getCount());
					}

					for (ItemStack stack : finalInv.keySet()) {
						countAgainstStacks.put(stack.getItem(), countAgainstStacks.getOrDefault(stack.getItem(), 0) + finalInv.get(stack));
					}

					for (Item countStack : countStacks.keySet()) {
						if (countStacks.get(countStack) < countAgainstStacks.getOrDefault(countStack, 0)) {
							finalInv.clear();
							finalRecipe = null;
							continue mainLoop;
						}
					}
				}

				for (ItemStack variant : ingredient.getMatchingStacks()) {
					ItemStack stack;
					if (player.inventory.hasItemStack(variant)) {
						stack = player.inventory.getStackInSlot(player.inventory.getSlotFor(variant));
					} else {
						stack = craftItemFromInventory(player, variant);
						if (!stack.isEmpty()) {
							player.inventory.addItemStackToInventory(stack);
							player.inventory.markDirty();
						}
					}
					if (stack.isEmpty()) continue ingLoop;
					finalInv.put(stack,
							(finalInv.getOrDefault(stack, 0)) + variant.getCount());
					continue ingLoop;
				}
				continue mainLoop;
			}
			break;
		}

		if (finalRecipe == null) return ItemStack.EMPTY;

		if (finalInv.isEmpty()) return ItemStack.EMPTY;
		else {
			for (ItemStack stack : finalInv.keySet()) {
				stack.shrink(finalInv.get(stack));
			}
			return finalRecipe.getRecipeOutput().copy();
		}
	}

	static ItemStack findItemInventoryFromItem(EntityPlayer player, ItemStack toFind) {
		ItemStack stack = ItemStack.EMPTY;
		for (ItemStack invStack : player.inventory.mainInventory)
			if (!invStack.isEmpty()
					&& invStack.isItemEqual(toFind)) {
				stack = invStack;
				break;
			}
		return stack;
	}

	static HashSet<IRecipe> getRecipesForItem(ItemStack stack) {
		HashSet<IRecipe> recipes = new HashSet<>();
		for (IRecipe recipe : ForgeRegistries.RECIPES.getValues()) {
			if (recipe == null) continue;
			if (ItemStack.areItemsEqualIgnoreDurability(recipe.getRecipeOutput(), stack)) {
				recipes.add(recipe);
			}
		}
		return recipes;
	}

	default boolean isStructureComplete(World world, BlockPos pos) {
		for (Template.BlockInfo info : getStructure().blockInfos()) {
			if (info.blockState == null) continue;

			BlockPos realPos = info.pos.add(pos).subtract(offsetToCenter());
			if (world.isAirBlock(realPos)) continue;
			if (world.getBlockState(realPos).getBlock() != info.blockState.getBlock()) return false;
			if (world.getBlockState(realPos) != info.blockState) return false;
		}
		return true;
	}

	default boolean tickStructure(World world, EntityPlayer player, BlockPos pos) {
		if (world.isRemote) return true;

		if (player.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND) {
			for (Template.BlockInfo info : getStructure().blockInfos()) {
				if (info.blockState == null) continue;

				BlockPos realPos = info.pos.add(pos).subtract(offsetToCenter());
				if (world.getBlockState(realPos).getBlock() != info.blockState.getBlock()) {
					if (world.isAirBlock(realPos)) {

						if (player.isCreative()) {
							world.setBlockState(realPos, info.blockState);
						} else {
							ItemStack requiredStack = new ItemStack(info.blockState.getBlock());
							ItemStack stack = findItemInventoryFromItem(player, requiredStack);

							if (stack.isEmpty()) {
								ItemStack outputItem = craftItemFromInventory(player, requiredStack);

								if (outputItem.isEmpty()) continue;
								outputItem.shrink(1);
								player.inventory.addItemStackToInventory(outputItem);
								player.inventory.markDirty();
								world.setBlockState(realPos, info.blockState);
								return true;
							} else {
								stack.shrink(1);
								world.setBlockState(realPos, info.blockState);
								return true;
							}
						}
					}
				} else if (world.getBlockState(realPos) != info.blockState) {
					if (player.isCreative() || !info.blockState.getMaterial().isLiquid()) {
						world.setBlockState(realPos, info.blockState);
						return true;
					} else {
						FluidStack fluidStack = new FluidStack(FluidRegistry.lookupFluidForBlock(info.blockState.getBlock()), 1);
						ItemStack fluidBucket = findItemInventoryFromItem(player, FluidUtil.getFilledBucket(fluidStack));
						if (!fluidBucket.isEmpty()) {
							fluidBucket.shrink(1);
							player.addItemStackToInventory(new ItemStack(Items.BUCKET));
							player.inventory.markDirty();
							world.setBlockState(realPos, info.blockState);
							return true;
						}
					}
				}
			}
		}
		return true;
	}
}
