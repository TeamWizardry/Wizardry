package com.teamwizardry.wizardry.common.core;

import java.util.Map;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.init.ModEnchantments;
import com.teamwizardry.wizardry.utils.InfusedItemStackNBTData;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InfusionEventHandler {
	
	// TODO: Add to com.teamwizardry.wizardry.common.core.EventHandler.EventHandler() instead
	
	@SubscribeEvent
	public void onAnvilUpdateEvent( AnvilUpdateEvent event ) {
		ItemStack itemToEnchant = event.getLeft();
		ItemStack book = event.getRight();
		if( book.getItem() != Items.ENCHANTED_BOOK )
			return;

		// Check if it is a correctly infused book
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(book);
		if( enchantments.get(ModEnchantments.enchantmentInfusion) == null )
			return;

//		NBTTagList spellData = ItemNBTHelper.getList(book, Constants.NBT.VANILLA_PREFIX + Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
//		if( spellData == null )
//			return;
		
//		String type = ItemNBTHelper.getString(book, Constants.NBT.VANILLA_PREFIX + Constants.NBT.PEARL_TYPE, "");
//		if( type.isEmpty() )
//			return;
		
		InfusedItemStackNBTData data =
				new InfusedItemStackNBTData(Constants.NBT.VANILLA_PREFIX)
					.initByStack(book);
		if( !data.isComplete() )
			return;
		
		// Now try to apply effects by vanilla mechanics
		if( !applyVanillaEnchantmentsFromBook(event, itemToEnchant, book, enchantments) )
			return;

		// Copy all wizardry data
//		float rand = ItemNBTHelper.getFloat(book, Constants.NBT.VANILLA_PREFIX + Constants.NBT.RAND, 0);
		ItemStack output = event.getOutput();
		data.assignToStack(output);
//		ItemNBTHelper.setString(output, Constants.NBT.VANILLA_PREFIX + Constants.NBT.PEARL_TYPE, type);
//		ItemNBTHelper.setFloat(output, Constants.NBT.VANILLA_PREFIX + Constants.NBT.RAND, rand);
//		ItemNBTHelper.setList(output, Constants.NBT.VANILLA_PREFIX + Constants.NBT.SPELL, spellData);
		
		// TODO: Merge effects if two infused books are combined
	}
	
	private static boolean applyVanillaEnchantmentsFromBook(AnvilUpdateEvent event, ItemStack itemToEnchant, ItemStack book, Map<Enchantment, Integer> newEnchantments) {
		// NOTE: Copied and adapted from net.minecraft.inventory.ContainerRepair.updateRepairOutput() 
		
		Map<Enchantment, Integer> curEnchantments = EnchantmentHelper.getEnchantments(itemToEnchant);
		int cost = 0;
		boolean flag2 = false;
        boolean flag3 = false;

        for (Enchantment enchantment1 : newEnchantments.keySet())
        {
            if (enchantment1 != null)
            {
                int i2 = curEnchantments.containsKey(enchantment1) ? ((Integer)curEnchantments.get(enchantment1)).intValue() : 0;
                int j2 = ((Integer)newEnchantments.get(enchantment1)).intValue();
                j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                boolean flag1 = enchantment1.canApply(itemToEnchant);

                if (/*this.player.capabilities.isCreativeMode || */itemToEnchant.getItem() == Items.ENCHANTED_BOOK)
                {
                    flag1 = true;
                }

                for (Enchantment enchantment : curEnchantments.keySet())
                {
                    if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment))
                    {
                        flag1 = false;
                        ++cost;
                    }
                }

                if (!flag1)
                {
                    flag3 = true;
                }
                else
                {
                    flag2 = true;

                    if (j2 > enchantment1.getMaxLevel())
                    {
                        j2 = enchantment1.getMaxLevel();
                    }

                    curEnchantments.put(enchantment1, Integer.valueOf(j2));
                    int k3 = 0;

                    switch (enchantment1.getRarity())
                    {
                        case COMMON:
                            k3 = 1;
                            break;
                        case UNCOMMON:
                            k3 = 2;
                            break;
                        case RARE:
                            k3 = 4;
                            break;
                        case VERY_RARE:
                            k3 = 8;
                    }

//                    if (flag)
                    {
                        k3 = Math.max(1, k3 / 2);
                    }

                    cost += k3 * j2;

                    if (itemToEnchant.getCount() > 1)
                    {
                    	cost = 40;
                    }
                }
            }
        }

        if (flag3 && !flag2)
        {
//            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
//            this.maximumCost = 0;
        	event.setCost(0);
            return false;
        }
        
        ItemStack output = itemToEnchant.copy();
        
        if (!itemToEnchant.getItem().isBookEnchantable(itemToEnchant, book)) {
//        	output = ItemStack.EMPTY;
        	return false;
        }

        if (cost <= 0)
        {
//            output = ItemStack.EMPTY;
        	return false;
        }

        if (event.getCost() >= 40 /*&& !this.player.capabilities.isCreativeMode */ )
        {
//        	output = ItemStack.EMPTY;
        	return false;
        }

//        if (!output.isEmpty())
//        {
            int k2 = output.getRepairCost();

//            if (!book.isEmpty() && k2 < book.getRepairCost())
//            {
//                k2 = book.getRepairCost();
//            }

//            if (k != i || k == 0)
//            {
                k2 = k2 * 2 + 1;
//            }

            output.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(curEnchantments, output);
//        }
//        else

        event.setCost(itemToEnchant.getRepairCost() + cost);
        event.setOutput(output);
        return true;
	}
}
