package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	private int slot;
	@Save
	private ArrayList<ItemStack> inventory;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(int slot, ArrayList<ItemStack> inventory) {
		this.slot = slot;
		this.inventory = inventory;
	}

	@Override
	public void handle(@NotNull MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().player;

		ItemStack book = player.inventory.getStackInSlot(slot);
		if (book.getItem() != ModItems.BOOK) return;

		SpellBuilder builder = new SpellBuilder(inventory);
		ItemNBTHelper.setString(book, "spell_recipe", builder.toJson().toString());
		ItemNBTHelper.setBoolean(book, "has_recipe", true);
	}
}
