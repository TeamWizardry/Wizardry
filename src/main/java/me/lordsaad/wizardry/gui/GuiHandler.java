package me.lordsaad.wizardry.gui;

import me.lordsaad.wizardry.gui.indexes.IndexBasics;
import me.lordsaad.wizardry.gui.pages.GettingStarted;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by Saad on 4/13/2016.
 */
public class GuiHandler implements IGuiHandler {
    public static int INDEX = 0, BASICS = 1;

    // BASICS INDEX
    public static int basics_getting_started = 2;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == INDEX) return new MainIndex();
        if (ID == BASICS) return new IndexBasics();
        if (ID == basics_getting_started) return new GettingStarted();
        return null;
    }
}
