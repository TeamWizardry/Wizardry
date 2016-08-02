package com.teamwizardry.wizardry.api.bloods;

import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBloodType {
    @SideOnly(Side.CLIENT)
    ResourceLocation getBloodTexture(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    Color getBloodColor(EntityPlayer player);

    default boolean isGlowing(EntityPlayer player) {
        return true;
    }
}
