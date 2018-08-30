package com.teamwizardry.wizardry.crafting.irecipies;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/30/2016.
 */
public class RecipeUnmountPearl extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		boolean foundStaff = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.STAFF) {

				if (stack.getItemDamage() == 1) {
					if (foundStaff) return false;
					foundStaff = true;
				}
			}
		}
		return foundStaff;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack foundStaff = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.STAFF) {

				if (stack.getItemDamage() == 1) {
					foundStaff = stack;
					break;
				}
			}
		}

		if (foundStaff.isEmpty()) return ItemStack.EMPTY;

		ItemStack infusedPearl = new ItemStack(ModItems.PEARL_NACRE);

		double pearlMultiplier = 1;
		if (infusedPearl.getItem() instanceof INacreProduct) {
			float purity = ((INacreProduct) infusedPearl.getItem()).getQuality(infusedPearl);
			if (purity >= 1f) pearlMultiplier = ConfigValues.perfectPearlMultiplier * purity;
			else if (purity <= ConfigValues.damagedPearlMultiplier)
				pearlMultiplier = ConfigValues.damagedPearlMultiplier;
			else {
				double base = purity - 1;
				pearlMultiplier = 1 - (base * base * base * base);
			}
		}

		NBTTagList moduleList = ItemNBTHelper.getList(foundStaff, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
		if (moduleList == null) return ItemStack.EMPTY;

		SpellBuilder builder = new SpellBuilder(SpellUtils.getSpellItems(SpellUtils.deserializeModuleList(moduleList)), pearlMultiplier);

		NBTTagList list = new NBTTagList();
		for (SpellRing spellRing : builder.getSpell()) {
			list.appendTag(spellRing.serializeNBT());
		}
		ItemNBTHelper.setList(infusedPearl, Constants.NBT.SPELL, list);


		return infusedPearl;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		NonNullList<ItemStack> remainingItems = ForgeHooks.defaultRecipeGetRemainingItems(inv);

		ItemStack sword;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.STAFF) {
				sword = stack.copy();
				sword.setItemDamage(0);
				remainingItems.set(i, sword);
				break;
			}
		}

		return remainingItems;
	}
}
