package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class CommandGenCape extends CommandBase {
	@NotNull
	@Override
	public String getName() {
		return "genmaxcape";
	}

	@NotNull
	@Override
	public String getUsage(ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			ItemStack cape = new ItemStack(ModItems.CAPE);
			NBTHelper.setInt(cape, "maxTick", 1000000);
			((EntityPlayer)sender).inventory.addItemStackToInventory(cape);
		} else {
			notifyCommandListener(sender, this, "wizardry.command.notplayer");
		}
	}
}
