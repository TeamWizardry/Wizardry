package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlWheelHolder;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PearlHandlingUtils {

    public static void swapPearl(EntityPlayer player, ItemStack swappableStack, ItemStack wheelHolderStack, int wheelSlot){
        if(player != null){
            // Determine which values are valid
            IPearlSwappable swappable = null;
            if(swappableStack != null && swappableStack.getItem() instanceof IPearlSwappable){
                swappable = (IPearlSwappable) swappableStack.getItem();
            }
            IPearlWheelHolder wheelHolder = null;
            if(wheelHolderStack != null && wheelHolderStack.getItem() instanceof IPearlWheelHolder) {
                wheelHolder = (IPearlWheelHolder) wheelHolderStack.getItem();
            }

            // if a wheel holder exists, remove pearl from selected slot. If a swappable exists, swap its pearl with this
            // and add to belt. If swappable doesn't exist, put pearl in player's inventory.
            if(wheelHolder != null){
                ItemStack pearl = wheelHolder.removePearl(wheelHolderStack, wheelSlot);
                if(swappable != null) {
                    wheelHolder.addPearl(wheelHolderStack,swappable.swapPearl(swappableStack, pearl));
                }else{
                    player.addItemStackToInventory(pearl);
                }
            }
        }
    }

    public static boolean canOpenPearlWheel(EntityPlayer player){
        ItemStack mainhand = player.getHeldItemMainhand();
        ItemStack offhand = player.getHeldItemOffhand();
        return (BaublesSupport.getItem(player, ModItems.PEARL_BELT) != ItemStack.EMPTY &&
                (mainhand.getItem() instanceof IPearlSwappable || offhand.getItem() instanceof IPearlSwappable));
    }
}
