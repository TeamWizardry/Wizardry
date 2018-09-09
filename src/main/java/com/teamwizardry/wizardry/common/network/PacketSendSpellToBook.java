package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
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
	public NBTTagList moduleList;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(List<List<Module>> compiledSpell) {
		if (compiledSpell == null) return;

		NBTTagList compiledList = new NBTTagList();
		for (List<Module> moduleList : compiledSpell) {
			for (Module module : moduleList)
				compiledList.appendTag(module.serialize());
			compiledList.appendTag(new NBTTagString());
		}
		moduleList = compiledList;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		for (ItemStack stack : Minecraft.getMinecraft().player.inventory.mainInventory) {
			if (stack.getItem() == ModItems.BOOK) {
				if (stack.getItem() != ModItems.BOOK) return;

				ItemNBTHelper.setList(stack, Constants.NBT.SPELL, moduleList);
				ItemNBTHelper.setBoolean(stack, "has_spell", true);
				ItemNBTHelper.setInt(stack, "page", 0);
			}
		}
	}
}
