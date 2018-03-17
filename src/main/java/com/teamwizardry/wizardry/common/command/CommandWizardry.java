package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.io.File;


/**
 * Created by Demoniaque.
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
		if (args.length < 1) throw new WrongUsageException(getUsage(sender));

		if (args[0].equalsIgnoreCase("reload")) {

			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.copyMissingModulesFromResources(CommonProxy.directory);
			ModuleRegistry.INSTANCE.processModules();

			if (server.isDedicatedServer()) {
				PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));
			}

			notifyCommandListener(sender, this, "wizardry.command.success");

		} else if (args[0].equalsIgnoreCase("reset")) {
			notifyCommandListener(sender, this, "wizardry.command.reset");

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

		} else if (args[0].equalsIgnoreCase("listModules")) {

			notifyCommandListener(sender, this, TextFormatting.YELLOW + " ________________________________________________\\\\");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " | " + TextFormatting.GRAY + "Module List");
			for (Module module : ModuleRegistry.INSTANCE.modules) {
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " | |_ " + TextFormatting.GREEN + module.getID() + TextFormatting.RESET + ": " + TextFormatting.GRAY + module.getReadableName());
			}
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");

		} else if (args[0].equalsIgnoreCase("debug")) {
			if (args.length < 2) throw new WrongUsageException(getUsage(sender));

			Module module = ModuleRegistry.INSTANCE.getModule(args[1]);

			if (module == null) {
				notifyCommandListener(sender, this, "Module not found.");
				return;
			}

			notifyCommandListener(sender, this, TextFormatting.YELLOW + " ________________________________________________\\\\");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " | " + TextFormatting.GRAY + "Module " + module.getReadableName() + ":");
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Description           " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getDescription());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Item Stack            " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getItemStack().getDisplayName());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Burnout Fill          " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getBurnoutFill());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_ " + TextFormatting.DARK_GREEN + "Burnout Multiplier" + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getBurnoutMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Mana Drain           " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getManaDrain());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_" + TextFormatting.DARK_GREEN + "Mana Multiplier     " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getManaMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Power Multiplier     " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getPowerMultiplier());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Charge Up Time      " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getChargeupTime());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Cooldown Time        " + TextFormatting.GRAY + " | " + TextFormatting.GRAY + module.getCooldownTime());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Primary Color        " + TextFormatting.GRAY + " | " + TextFormatting.RED + module.getPrimaryColor().getRed() + TextFormatting.GRAY + ", " + TextFormatting.GREEN + module.getPrimaryColor().getGreen() + TextFormatting.GRAY + ", " + TextFormatting.BLUE + module.getPrimaryColor().getBlue());
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Secondary Color    " + TextFormatting.GRAY + " | " + TextFormatting.RED + module.getSecondaryColor().getRed() + TextFormatting.GRAY + ", " + TextFormatting.GREEN + module.getSecondaryColor().getGreen() + TextFormatting.GRAY + ", " + TextFormatting.BLUE + module.getSecondaryColor().getBlue());

			if (!module.getAttributes().isEmpty())
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Default AttributeRegistry");
			for (AttributeModifier attributeModifier : module.getAttributes())
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_ " + TextFormatting.GRAY + attributeModifier.toString());

			ModuleModifier[] modifierList = module.applicableModifiers();
			if (modifierList != null) {
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Applicable Modifiers ");
				for (ModuleModifier modifier : modifierList)
					notifyCommandListener(sender, this, TextFormatting.YELLOW + " |     |_ " + TextFormatting.DARK_GREEN + modifier.getID());
			}
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");
		} else {
			throw new WrongUsageException(getUsage(sender));
		}
	}
}
