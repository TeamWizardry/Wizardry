package me.lordsaad.wizardry.gui.book;

import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.gui.book.contentpages.BasicsGettingStarted;
import me.lordsaad.wizardry.gui.book.indexes.IndexBasics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by Saad on 4/13/2016.
 */
public class GuiHandler implements IGuiHandler {
    public static int INDEX = 0, BASICS = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == INDEX) return new MainIndex();
        if (ID == BASICS) return new IndexBasics();
        if (ID == Constants.PageNumbers.BASICS_GETTING_STARTED) return new BasicsGettingStarted();
        return null;
    }
}
