package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

public class CommandListModules extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "listmodules";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) {
		notifyCommandListener(sender, this, TextFormatting.YELLOW + " ________________________________________________\\\\");
		notifyCommandListener(sender, this, TextFormatting.YELLOW + " | " + TextFormatting.GRAY + "Module List");
		for (ModuleInstance module : ModuleRegistry.INSTANCE.modules)
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " | |_ " + TextFormatting.GREEN + module.getSubModuleID() + TextFormatting.RESET + ": " + TextFormatting.GRAY + module.getReadableName());
		notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");

	}
}
