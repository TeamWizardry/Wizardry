package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassPearl extends ItemMod implements Explodable {

    public ItemGlassPearl() {
        super("glass_pearl");
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        Wizardry.proxy.spawnParticleMagicBurst(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
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
