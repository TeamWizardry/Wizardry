package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassPearl extends ItemWizardry implements Explodable {

    public ItemGlassPearl() {
        super("glass_pearl");
        setMaxStackSize(1);
    }
    
    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL)) {
            NBTTagCompound oldStackNBT = entityItem.getEntityItem().getTagCompound();
            ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE);
            if (oldStackNBT != null)
                newStack.setTagCompound(oldStackNBT);
            entityItem.setEntityItemStack(newStack);
        }
        return super.onEntityItemUpdate(entityItem);
    }
}
