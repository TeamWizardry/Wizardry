package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	private int slot;
	private NBTTagList spellList;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(int slot, List<SpellRing> spellRings) {
		this.slot = slot;

		if (spellRings == null) return;

		NBTTagList list = new NBTTagList();
		for (SpellRing ring : spellRings) {
			list.appendTag(ring.serializeNBT());
		}

		spellList = list;
	}

	@SaveMethodGetter(saveName = "module_saver")
	public NBTTagCompound getter() {
		NBTTagCompound compound = new NBTTagCompound();

		if (spellList != null)
			compound.setTag("spell_list", spellList);
		return compound;
	}

	@SaveMethodSetter(saveName = "module_saver")
	public void setter(NBTTagCompound compound) {
		if (compound.hasKey("spell_list"))
			spellList = compound.getTagList("spell_list", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
	}


	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().player;

		ItemStack book = player.inventory.getStackInSlot(slot);
		if (book.getItem() != ModItems.BOOK) return;

		ItemNBTHelper.setList(book, Constants.NBT.SPELL, spellList);
		ItemNBTHelper.setBoolean(book, "has_spell", true);
	}
}
