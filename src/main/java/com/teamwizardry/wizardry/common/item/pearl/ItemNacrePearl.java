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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends Item implements Infusable, Explodable, Colorable {

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

        colorableOnUpdate(stack);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (!entityItem.worldObj.isRemote) return false;

        colorableOnEntityItemUpdate(entityItem);

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

            return java.awt.Color.HSBtoRGB((rand + GuiTickHandler.ticksInGame) / (float) COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
        }
    }
}
