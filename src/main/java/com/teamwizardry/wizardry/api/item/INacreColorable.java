package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by Saad on 6/7/2016.
 */
public interface INacreColorable extends IItemColorProvider {

    String TAG_RAND = "rand";
    String TAG_PURITY = "purity";
    String TAG_COMPLETE = "complete";
    int NACRE_PURITY_CONVERSION = 30 * 20; // 30 seconds for max purity, 0/60 for no purity
    int COLOR_CYCLE_LENGTH = 50 * 20; // 50 seconds

    default void colorableOnUpdate(ItemStack stack) {
        if (!ItemNBTHelper.verifyExistence(stack, TAG_RAND))
            ItemNBTHelper.setInt(stack, TAG_RAND, 0);
        if (!ItemNBTHelper.verifyExistence(stack, TAG_PURITY))
            ItemNBTHelper.setInt(stack, TAG_PURITY, NACRE_PURITY_CONVERSION);
        if (!ItemNBTHelper.getBoolean(stack, TAG_COMPLETE, false))
            ItemNBTHelper.setBoolean(stack, TAG_COMPLETE, true);
    }

    default void colorableOnEntityItemUpdate(EntityItem entityItem) {
        ItemStack stack = entityItem.getEntityItem();

        if (!ItemNBTHelper.verifyExistence(stack, TAG_RAND))
            ItemNBTHelper.setInt(stack, TAG_RAND, entityItem.worldObj.rand.nextInt(COLOR_CYCLE_LENGTH));

        if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL) && !ItemNBTHelper.getBoolean(stack, TAG_COMPLETE, false)) {
            int purity = ItemNBTHelper.getInt(stack, TAG_PURITY, 0);
            purity = Math.min(purity + 1, NACRE_PURITY_CONVERSION * 2);
            ItemNBTHelper.setInt(stack, TAG_PURITY, purity);
        } else ItemNBTHelper.setBoolean(stack, TAG_COMPLETE, true);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    default IItemColor getItemColor() {
        return (stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFF;
            int rand = ItemNBTHelper.getInt(stack, TAG_RAND, 0);
            int purity = ItemNBTHelper.getInt(stack, TAG_PURITY, NACRE_PURITY_CONVERSION);
            float saturation = MathHelper.sin(purity * (float) Math.PI * 0.5f / NACRE_PURITY_CONVERSION);

            return Color.HSBtoRGB((rand + ClientTickHandler.getTicksInGame()) / (float) COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
        };
    }
}
