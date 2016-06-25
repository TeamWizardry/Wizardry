package com.teamwizardry.wizardry.gui;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.gui.book.util.PageRegistry;
import com.teamwizardry.wizardry.gui.worktable.WorktableBase;
import com.teamwizardry.wizardry.items.ItemPhysicsBook;
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
