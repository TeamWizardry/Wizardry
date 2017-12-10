package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
	public void onTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (!ConfigValues.versionCheckerEnabled) return;

		if (doneChecking && event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null && !triedToWarnPlayer) {
			if (onlineVersion != null && !onlineVersion.isEmpty()) {
				String clientBuild = Wizardry.VERSION;
				if (Utils.compareVersions(onlineVersion, clientBuild) == 1) {
					ArrayList<String> messages = new ArrayList<>();
					messages.add("Don't let your bugs be thugs! Use our patented Magic-o-gon spray for only $9.99");
					messages.add("What? A new update? Looks like magic...");
					messages.add("There's magic in the air.");
					messages.add("I smell fresh glitter!");
					messages.add("I think the devs spilled a fresh jar of glitter somewhere.");
					messages.add("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
					messages.add("before updating (◡‿◡✿) after updating (⊙ω⊙✿)");
					messages.add("(☞ﾟ∀ﾟ)☞ you're outdated my dude");
					messages.add("yo dawg we heard you like changelogs so we put changelogs in your changelogs so you can change while you log");
					messages.add("OwO what's this?");
					messages.add("(^з^)-☆ ~bug fixes and stuff~");
					messages.add("◔_◔ looks like you're out of date");
					messages.add("ಠ~ಠ why aren't you up to date yet");
					messages.add("GET IN THE UPDATE, SHINJI");
					messages.add("( ﾟ,_ゝﾟ) is that.. ( ﾟ,_ゝﾟ) ..a mod update i see?");
					messages.add("ಠ_ರೃ Watson, I believe they have an outdated jar.");
					messages.add("╚(•⌂•)╝ update your jars, dammit!");
					messages.add("(」ﾟヘﾟ)」y u no update?!");
					messages.add("*:･ﾟ✧ᕕ( ᐛ )ᕗ");
					messages.add("hey friend, could you update me please? (✿)w(✿)");
					messages.add("v(ಥ ̯ ಥ)v mfw not up-to-date");
					messages.add("Hey morty, *burp* morty, update the mod jar morty *burp*");
					messages.add("Ooooohwheeee! a new mod update!");
					messages.add("I am now up back up to... one. wizardry. update.");
					messages.add("I'm Mr. Meeseeks! Look at my new update!");

					player.sendMessage(new TextComponentString(TextFormatting.YELLOW + messages.get(RandUtil.nextInt(messages.size() - 1))));
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "There's a new Wizardry update available! your version: '" + TextFormatting.RED + clientBuild + TextFormatting.GREEN + "' new version: '" + TextFormatting.YELLOW + onlineVersion + TextFormatting.GREEN + "'"));

					if (updateMessage != null && !updateMessage.isEmpty()) {
						player.sendMessage(new TextComponentString(TextFormatting.GREEN + "[" + TextFormatting.GRAY + "UPDATE LINK & NOTES" + TextFormatting.GREEN + "]")
								.setStyle(new Style()
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(updateMessage)))
										.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.curseforge.com/projects/wizardry-mod/files"))));

					}
				} else if (updateMessage != null && !updateMessage.isEmpty()) {
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "[" + TextFormatting.GRAY + "UPDATE LINK & NOTES" + TextFormatting.GREEN + "]")
							.setStyle(new Style()
									.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(updateMessage)))
									.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.curseforge.com/projects/wizardry-mod/files"))));
				}
			}

			triedToWarnPlayer = true;
		}
	}
}
