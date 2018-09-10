package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.CommonWorktableModule;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public class PacketSendSpellToBook extends PacketBase {

	@Save
	public NBTTagList commonModules;
	@Save
	private UUID playerUUID;
	@Save
	public NBTTagList moduleList;

	public PacketSendSpellToBook() {
	}

	public PacketSendSpellToBook(UUID playerUUID, List<List<Module>> compiledSpell, Set<CommonWorktableModule> commonModules) {
		this.playerUUID = playerUUID;
		if (compiledSpell == null || commonModules == null) return;

		NBTTagList compiledList = new NBTTagList();
		for (List<Module> moduleList : compiledSpell) {
			for (Module module : moduleList)
				compiledList.appendTag(module.serialize());
			compiledList.appendTag(new NBTTagString());
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
		PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
		EntityPlayer player = players.getPlayerByUUID(playerUUID);

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
