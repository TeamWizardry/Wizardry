package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Saad on 3/7/2016.
 */
public class ItemManaCake extends ItemWizardryFood {

    public ItemManaCake() {
        super("mana_cake", 0, 0.3F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        IWizardryCapability cap = WizardryCapabilityProvider.get(player);
        if (cap.getMaxMana() >= cap.getMana() + 300) cap.setMana(cap.getMana() + 300, player);
        else cap.setMana(cap.getMaxMana(), player);
    }
}
