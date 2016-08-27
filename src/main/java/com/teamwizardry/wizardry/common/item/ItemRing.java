package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by Saad on 6/13/2016.
 */
public class ItemRing extends ItemWizardry implements INacreColorable {
    
    public ItemRing() {
        super("ring", "ring_pearl", "ring");
    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) return;
        
        colorableOnUpdate(stack);
    }
    
    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.worldObj.isRemote) return false;
        
        colorableOnEntityItemUpdate(entityItem);
        
        return super.onEntityItemUpdate(entityItem);
    }
}
