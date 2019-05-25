package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

public class CommandDebug extends CommandBase {

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
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws WrongUsageException {
		if (args.length < 2) throw new WrongUsageException(getUsage(sender));

		ModuleInstance module = ModuleRegistry.INSTANCE.getModule(args[1]);

		if (module == null) {
			notifyCommandListener(sender, this, "wizardry.command." + getName() + ".module_not_found");
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

		if (!module.getAttributeModifiers().isEmpty())
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Default AttributeRegistry");
		for (AttributeModifier attributeModifier : module.getAttributeModifiers())
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |  |_ " + TextFormatting.GRAY + attributeModifier.toString());

		ModuleInstanceModifier[] modifierList = module.applicableModifiers();
		if (modifierList != null) {
			notifyCommandListener(sender, this, TextFormatting.YELLOW + " |  |_ " + TextFormatting.GREEN + "Applicable Modifiers ");
			for (ModuleInstanceModifier modifier : modifierList)
				notifyCommandListener(sender, this, TextFormatting.YELLOW + " |     |_ " + TextFormatting.DARK_GREEN + modifier.getNBTKey());
		}
		notifyCommandListener(sender, this, TextFormatting.YELLOW + " |________________________________________________//");
	}
}
