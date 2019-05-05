package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellRingCache;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.network.PacketSyncModules;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class CommandReloadModules extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "reloadmodules";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) {
		SpellRingCache.INSTANCE.clear();
		ModuleRegistry.INSTANCE.loadUnprocessedModules();
		ModuleRegistry.INSTANCE.loadOverrideDefaults();
		ModuleRegistry.INSTANCE.copyMissingModules(CommonProxy.directory);
		ModuleRegistry.INSTANCE.loadModules(CommonProxy.directory);

		if (server.isDedicatedServer())
			PacketHandler.NETWORK.sendToAll(new PacketSyncModules(ModuleRegistry.INSTANCE.modules));

		notifyCommandListener(sender, this, "wizardry.command." + getName() + ".success");
	}
}
