package com.teamwizardry.wizardry.common.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModuleRegistry;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static com.teamwizardry.wizardry.proxy.CommonProxy.createModuleRegistryFile;

/**
 * Created by LordSaad.
 */
public class CommandWizardry extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "wizardry";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "/wizardry <reset>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		if (args.length != 1) throw new WrongUsageException(getUsage(sender));

		if (args[0].equalsIgnoreCase("reset")) {

			File config = new File(CommonProxy.directory.getPath() + "/" + Wizardry.MODID, "module_registry.json");

			if (config.exists())
				if (!config.delete()) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "SOMETHING WENT WRONG! Could not delete module_registry.json"));
					Wizardry.logger.error("SOMETHING WENT WRONG! Could not delete module_registry.json");
					return;
				} else {
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "module_registry.json deleted successfully!"));
					Wizardry.logger.info("module_registry.json deleted successfully!");
				}

			if (!createModuleRegistryFile(config.getParentFile())) {
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "SOMETHING WENT WRONG! Could not create module_registry.json"));
				Wizardry.logger.error("SOMETHING WENT WRONG! Could not create module_registry.json");
				return;
			} else
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "module_registry.json file has been recreated successfully!"));

			JsonParser parser = new JsonParser();
			try {
				JsonElement element = parser.parse(new FileReader(config));
				JsonObject obj = element.getAsJsonObject();

				ModuleRegistry.INSTANCE.setJsonObject(obj);
				ModuleRegistry.INSTANCE.processModules();

				PacketHandler.NETWORK.sendToAll(new PacketSyncModuleRegistry(element));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Modules successfully reloaded! Horay! (ﾉ≧∀≦)ﾉ・‥…━━━★"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else throw new WrongUsageException(getUsage(sender));
	}
}
