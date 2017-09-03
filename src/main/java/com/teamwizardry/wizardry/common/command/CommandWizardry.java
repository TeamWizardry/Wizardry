package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
		return "/wizardry <reset>";
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

			sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Modules successfully reloaded! Horay! (ﾉ≧∀≦)ﾉ・‥…━━━★"));

		} else if (args[0].equalsIgnoreCase("reset")) {
			sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Attempting to reset module directory... Any modifications made to it will be lost!"));

			File moduleDirectory = new File(CommonProxy.directory, "modules");
			if (moduleDirectory.exists()) {

				File[] files = moduleDirectory.listFiles();
				if (files != null)
					for (File file : files) {
						String name = file.getName();
						if (!file.delete()) {
							Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not delete " + file.getName() + ". Cancelling process!");
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "SOMETHING WENT WRONG! Could not delete " + file.getName() + ". Cancelling process!"));
							return;
						} else {
							Wizardry.logger.info(name + " deleted successfully!");
						}
					}

				if (!moduleDirectory.delete()) {
					Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not delete module directory!");
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "SOMETHING WENT WRONG! Could not delete module directory!"));
					return;
				}
			}
			if (!moduleDirectory.exists())
				if (!moduleDirectory.mkdirs()) {
					Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create module directory!");
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "SOMETHING WENT WRONG! Could not create module directory!"));
					return;
				}

			ModuleRegistry.INSTANCE.setDirectory(moduleDirectory);
			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.copyMissingModulesFromResources(CommonProxy.directory);
			ModuleRegistry.INSTANCE.processModules();

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Modules successfully reset! Horay! (ﾉ≧∀≦)ﾉ・‥…━━━★"));

		} else throw new WrongUsageException(getUsage(sender));
	}
}
