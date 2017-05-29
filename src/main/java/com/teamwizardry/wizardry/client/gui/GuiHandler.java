package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.wizardry.client.gui.book.BookGui;
import com.teamwizardry.wizardry.client.gui.worktable.WorktableGui;
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
		if (ID == 0) return new WorktableGui();
		if (ID == 1) return new BookGui(player.inventory.getStackInSlot(x));

		return null;
	}
}
