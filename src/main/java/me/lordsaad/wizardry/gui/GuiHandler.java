package me.lordsaad.wizardry.gui;

import net.minecraftforge.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.gui.book.MainIndex;
import me.lordsaad.wizardry.gui.book.indexes.IndexBasics;
import me.lordsaad.wizardry.gui.worktable.WorktableBase;

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
        if (ID == Constants.PageNumbers.INDEX) return new MainIndex();
        if (ID == Constants.PageNumbers.BASICS) return new IndexBasics();
        if (ID == Constants.PageNumbers.WORKTABLE) return new WorktableBase();
        return null;
    }
}
