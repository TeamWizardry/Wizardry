package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.io.File;


/**
 * Created by LordSaad.
 */
public class CommandWizardry extends CommandBase {

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

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if (args.length != 1) throw new WrongUsageException(getUsage(sender));

		if (args[0].equalsIgnoreCase("reload")) {

			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.copyMissingModulesFromResources(CommonProxy.directory);
			ModuleRegistry.INSTANCE.processModules();

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			notifyCommandListener(sender, this, "wizardry.command.success");

		} else if (args[0].equalsIgnoreCase("reverseTime")) {
			notifyCommandListener(sender, this, "wizardry.command.reverseTime");

			File moduleDirectory = new File(CommonProxy.directory, "modules");
			if (moduleDirectory.exists()) {

				File[] files = moduleDirectory.listFiles();
				if (files != null)
					for (File file : files) {
						String name = file.getName();
						if (!file.delete()) {
							throw new CommandException("wizardry.command.fail", name);
						} else {
							notifyCommandListener(sender, this, "wizardry.command.success_delete", name);
						}
					}

				if (!moduleDirectory.delete()) {
					throw new CommandException("wizardry.command.fail_dir_delete");

				}
			}
			if (!moduleDirectory.exists())
				if (!moduleDirectory.mkdirs()) {
					throw new CommandException("wizardry.command.fail_dir_create");
				}

			ModuleRegistry.INSTANCE.setDirectory(moduleDirectory);
			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.copyMissingModulesFromResources(CommonProxy.directory);
			ModuleRegistry.INSTANCE.processModules();

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			notifyCommandListener(sender, this, "wizardry.command.success");

		} else throw new WrongUsageException(getUsage(sender));
	}
}
