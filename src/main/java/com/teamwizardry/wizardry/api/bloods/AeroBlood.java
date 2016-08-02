package com.teamwizardry.wizardry.api.bloods;

import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AeroBlood implements IBloodType {
    private static final ResourceLocation BLOOD_TEXTURE = new ResourceLocation("wizardry", "textures/model/blood_overlay.png");

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getBloodTexture(EntityPlayer player) {
        return BLOOD_TEXTURE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Color getBloodColor(EntityPlayer player) {
        return BloodColorHelper.makeColor(BloodColorHelper.pulseColor(0x00FF00));
    }
}
