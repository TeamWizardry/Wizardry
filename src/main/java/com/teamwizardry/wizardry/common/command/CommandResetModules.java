package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CommandResetModules extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "resetmodules";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		notifyCommandListener(sender, this, "wizardry.command." + getName() + ".warning");

		File moduleDirectory = new File(CommonProxy.directory, "modules");
		if (moduleDirectory.exists()) {

			File[] files = moduleDirectory.listFiles();
			if (files != null)
				for (File file : files) {
					String name = file.getName();
					if (!file.delete())
						throw new CommandException("wizardry.command." + getName() + ".fail", name);
					else
						notifyCommandListener(sender, this, "wizardry.command." + getName() + ".success_delete", name);
				}

			if (!moduleDirectory.delete())
				throw new CommandException("wizardry.command" + getName() + ".fail_dir_delete");
		}
		if (!moduleDirectory.exists())
			if (!moduleDirectory.mkdirs())
				throw new CommandException("wizardry.command" + getName() + ".fail_dir_create");

		ModuleRegistry.INSTANCE.loadUnprocessedModules();
		ModuleRegistry.INSTANCE.copyMissingModules(CommonProxy.directory);
		ModuleRegistry.INSTANCE.loadModules(CommonProxy.directory);

		if (server.isDedicatedServer())
			PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));

		notifyCommandListener(sender, this, "wizardry.command" + getName() + ".success");
	}
}
