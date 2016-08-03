package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Colorable;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.Infusable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends Item implements Infusable, Explodable {

    public ItemNacrePearl() {
        setRegistryName("nacre_pearl");
        setUnlocalizedName("nacre_pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        if (!compound.hasKey("rand"))
            compound.setInteger("rand", worldIn.rand.nextInt(100));
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        ItemStack stack = entityItem.getEntityItem();
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        if (!compound.hasKey("rand"))
            compound.setInteger("rand", entityItem.worldObj.rand.nextInt(100));

        return super.onEntityItemUpdate(entityItem);
    }

    @Override
    public boolean canItemEditBlocks() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IItemColor {

        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            int rand = 0;
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null && compound.hasKey("rand"))
                rand = compound.getInteger("rand");

            return Color.HSBtoRGB((rand + GuiTickHandler.ticksInGame) * 0.01f, 0.15f, 1f);
        }
    }
}
