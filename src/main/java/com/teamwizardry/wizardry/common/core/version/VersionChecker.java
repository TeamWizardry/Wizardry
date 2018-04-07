package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public final class VersionChecker {

	public static VersionChecker INSTANCE = new VersionChecker();

	public static boolean doneChecking = false;
	public static String onlineVersion = null;
	public static String updateMessage = null;
	private static boolean triedToWarnPlayer = false;

	private VersionChecker() {
		new ThreadVersionChecker();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (!ConfigValues.versionCheckerEnabled) return;

		if (doneChecking && event.phase == TickEvent.Phase.END && player != null && !triedToWarnPlayer) {
			ITextComponent component = new TextComponentString("[").setStyle(new Style().setColor(TextFormatting.GREEN))
					.appendSibling(new TextComponentTranslation("wizardry.misc.update_link").setStyle(new Style().setColor(TextFormatting.GRAY)))
					.appendSibling(new TextComponentString("]").setStyle(new Style().setColor(TextFormatting.GREEN)));
			component.getStyle()
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(updateMessage)))
					.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.curseforge.com/projects/wizardry-mod/files"));

			if (onlineVersion != null && !onlineVersion.isEmpty()) {
				String clientBuild = Wizardry.VERSION;
				if (!clientBuild.equals("GRADLE:VERSION") && Utils.compareVersions(onlineVersion, clientBuild) == 1) {
					ArrayList<String> messages = new ArrayList<>();
					String base = "wizardry.update";
					int n = 0;
					while (LibrarianLib.PROXY.canTranslate(base + n))
						messages.add(base + n++);

					if (!messages.isEmpty())
						player.sendMessage(new TextComponentTranslation(messages.get(RandUtil.nextInt(messages.size() - 1))).setStyle(new Style().setColor(TextFormatting.YELLOW)));
					player.sendMessage(new TextComponentTranslation("wizardry.misc.update_checker0")
							.setStyle(new Style().setColor(TextFormatting.GREEN)));
					player.sendMessage(new TextComponentTranslation("wizardry.misc.update_checker1")
							.setStyle(new Style().setColor(TextFormatting.GREEN))
							.appendText(" ")
							.appendSibling(new TextComponentString(clientBuild).setStyle(new Style().setColor(TextFormatting.RED))));
					player.sendMessage(new TextComponentTranslation("wizardry.misc.update_checker2")
							.setStyle(new Style().setColor(TextFormatting.GREEN))
							.appendText(" ")
							.appendSibling(new TextComponentString(onlineVersion).setStyle(new Style().setColor(TextFormatting.YELLOW))));

					if (updateMessage != null && !updateMessage.isEmpty())
						player.sendMessage(component);
				} else if (updateMessage != null && !updateMessage.isEmpty())
					player.sendMessage(component);
			}

			triedToWarnPlayer = true;
		}
	}
}
