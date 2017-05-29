package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	private int slot;
	@Save
	private String json;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(int slot, String json) {
		this.slot = slot;
		this.json = json;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().player;

		ItemStack book = player.inventory.getStackInSlot(slot);
		if (book.getItem() != ModItems.BOOK) return;

		ItemNBTHelper.setString(book, "spell_recipe", json);
		ItemNBTHelper.setBoolean(book, "has_recipe", true);
	}
}
