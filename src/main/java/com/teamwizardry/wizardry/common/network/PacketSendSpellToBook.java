package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.CommonWorktableModule;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	public NBTTagList commonModules;
	@Save
	public NBTTagList moduleList;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(List<List<ModuleInstance>> compiledSpell, Set<CommonWorktableModule> commonModules) {
		if (compiledSpell == null || commonModules == null) return;

		NBTTagList compiledList = new NBTTagList();
		for (List<ModuleInstance> moduleList : compiledSpell) {
			for (ModuleInstance module : moduleList)
				compiledList.appendTag(module.serialize());
		}
		moduleList = compiledList;

		NBTTagList commonList = new NBTTagList();
		for (CommonWorktableModule commonModule : commonModules) {
			commonList.appendTag(commonModule.serializeNBT());
		}
		this.commonModules = commonList;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().player;

		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack.getItem() == ModItems.BOOK) {

				ItemNBTHelper.setList(stack, "common_modules", commonModules);
				ItemNBTHelper.setList(stack, Constants.NBT.SPELL, moduleList);
				ItemNBTHelper.setBoolean(stack, "has_spell", true);
				ItemNBTHelper.setInt(stack, "page", 0);
			}
		}
	}
}
