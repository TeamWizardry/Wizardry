package com.teamwizardry.wizardry.common.command;

import net.minecraft.command.CommandDebug;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

import javax.annotation.Nonnull;


/**
 * Created by Demoniaque.
 */
public class CommandWizardry extends CommandTreeBase {
	public CommandWizardry() {
		addSubcommand(new CommandListModules());
		addSubcommand(new CommandGenStaff());
		addSubcommand(new CommandGenPearl());
		addSubcommand(new CommandGenCape());
		addSubcommand(new CommandDebug());
//		addSubcommand(new CommandTeleportTorikki());
		addSubcommand(new CommandTeleportUnderworld());
		addSubcommand(new CommandTreeHelp(this));
	}

	@Nonnull
	@Override
	public String getName() {
		return "wizardry";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender) {
		return "wizardry.command.usage";
	}
}
