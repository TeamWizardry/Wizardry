package me.lordsaad.wizardry.gui;

import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.gui.book.util.PageRegistry;
import me.lordsaad.wizardry.gui.worktable.WorktableBase;
import me.lordsaad.wizardry.items.ItemPhysicsBook;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

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
        if (ID == Constants.PageNumbers.GUIDE) {
            String path = ItemPhysicsBook.getHeldPath(player);
            int page = ItemPhysicsBook.getHeldPage(player);
            GuiScreen scr = PageRegistry.construct(null, path, page);
            if (scr == null)
                scr = PageRegistry.construct(null, "/", 0);
            return scr;
        }
        if (ID == Constants.PageNumbers.WORKTABLE) return new WorktableBase();
        return null;
    }
}
