package com.teamwizardry.wizardry.common.command;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.TeleportUtil;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.wizardry.common.core.EventHandler.fallResetter;

public class CommandTeleportUnderworld extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "tpunderworld";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "wizardry.command." + getName() + ".usage";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) {
		Entity entity = sender.getCommandSenderEntity();
		if (entity instanceof EntityPlayerMP) {
			EntityPlayer player = ((EntityPlayer) entity);
			fallResetter.add(player.getUniqueID());
			TeleportUtil.teleportToDimension(player, Wizardry.underWorld.getId(), 0, 300, 0);
			player.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 0, true, false));
		} else notifyCommandListener(sender, this, "wizardry.command.notplayer");
	}
}
