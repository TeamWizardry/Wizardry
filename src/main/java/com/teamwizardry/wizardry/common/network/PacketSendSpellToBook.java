package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	private int slot;
	@Save
	private NBTTagList moduleList;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(int slot, List<List<Module>> compiledSpell) {
		this.slot = slot;

		if (compiledSpell == null) return;

		NBTTagList list = new NBTTagList();
		for (List<Module> moduleList : compiledSpell)
		{
			for (Module module : moduleList)
				list.appendTag(module.serialize());
			list.appendTag(new NBTTagString());
		}
		moduleList = list;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().player;

		ItemStack book = player.inventory.getStackInSlot(slot);
		if (book.getItem() != ModItems.BOOK) return;

		ItemNBTHelper.setList(book, Constants.NBT.SPELL, moduleList);
		ItemNBTHelper.setBoolean(book, "has_spell", true);
		ItemNBTHelper.setInt(book, "page", 0);
	}
}
