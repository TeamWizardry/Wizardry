package com.teamwizardry.wizardry.client.gui;

import net.minecraftforge.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.client.gui.worktable.WorktableBase;

/**
 * Created by Saad on 4/13/2016.
 */
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Constants.PageNumbers.WORKTABLE) return new WorktableBase();
        return null;
    }
}
