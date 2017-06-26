package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	private int slot;
	private ArrayList<ItemStack> inventory;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(int slot, ArrayList<ItemStack> inventory) {
		this.slot = slot;
		this.inventory = inventory;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound == null) return;
		NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound1 = list.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(compound1);
			inventory.add(stack);
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (ItemStack stack : inventory) {
			list.appendTag(stack.serializeNBT());
		}
		nbt.setTag("list", list);
		return nbt;
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
