package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import com.teamwizardry.wizardry.client.gui.worktable.WorktableGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.teamwizardry.wizardry.common.item.ItemBook.BOOK;

/**
 * Created by Saad on 4/13/2016.
 */
public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0) return new WorktableGui(new BlockPos(x, y, z));
		if (ID == 1) {
			//ItemStack stack = player.inventory.getStackInSlot(x);
			//if (stack.isEmpty() || !(stack.getItem() instanceof ItemBook)) return null;
			return new GuiBook(BOOK);
		}

		return null;
	}
}
