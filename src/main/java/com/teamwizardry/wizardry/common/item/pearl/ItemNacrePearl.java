package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.gui.TickCounter;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
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

    public static final String TAG_RAND = "rand";
    public static final String TAG_PURITY = "purity";
    public static final String TAG_COMPLETE = "complete";
    public static final int NACRE_PURITY_CONVERSION = 30 * 20; // 30 seconds
    public static final int COLOR_CYCLE_LENGTH = 50 * 20; // 50 seconds

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
        if (!worldIn.isRemote) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }

        if (!compound.hasKey(TAG_RAND))
            compound.setInteger(TAG_RAND, 0);
        if (!compound.hasKey(TAG_PURITY))
            compound.setInteger(TAG_PURITY, NACRE_PURITY_CONVERSION);
        if (!compound.getBoolean(TAG_COMPLETE))
            compound.setBoolean(TAG_COMPLETE, true);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.worldObj.isRemote) return false;

        ItemStack stack = entityItem.getEntityItem();
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }


        if (!compound.hasKey(TAG_RAND))
            compound.setInteger(TAG_RAND, entityItem.worldObj.rand.nextInt(COLOR_CYCLE_LENGTH));

        if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL) && !compound.getBoolean(TAG_COMPLETE)) {
            int purity = 0;
            if (compound.hasKey(TAG_PURITY))
                purity = compound.getInteger(TAG_PURITY);
            purity = Math.min(purity + 1, NACRE_PURITY_CONVERSION * 2);
            compound.setInteger(TAG_PURITY, purity);
        } else
            compound.setBoolean(TAG_COMPLETE, true);

        return super.onEntityItemUpdate(entityItem);
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IItemColor {

        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            int rand = 0;
            float saturation = 1f;
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null && compound.hasKey(TAG_RAND))
                rand = compound.getInteger(TAG_RAND);
            if (compound != null && compound.hasKey(TAG_PURITY))
                saturation = MathHelper.sin(compound.getInteger(TAG_PURITY) * (float) Math.PI * 0.5f / NACRE_PURITY_CONVERSION);

            return Color.HSBtoRGB((rand + TickCounter.Companion.getTicksInGame() ) / (float) COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
        }
    }
}
