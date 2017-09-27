package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public final class VersionChecker {

	public static VersionChecker INSTANCE = new VersionChecker();

	public static boolean doneChecking = false;
	public static String onlineVersion = "";
	private static boolean triedToWarnPlayer = false;

	private boolean warnedPlayerOfAlpha = false;

	private VersionChecker() {
		new ThreadVersionChecker();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (doneChecking && Minecraft.getMinecraft().player != null && event.phase == TickEvent.Phase.END && !warnedPlayerOfAlpha) {
			warnedPlayerOfAlpha = true;
			player.sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "WARNING! WIZARDRY IS IN EARLY ALPHA!!!"));
			player.sendMessage(new TextComponentString(TextFormatting.RED + "The mod still lacks most of its content that's on its way."));
			player.sendMessage(new TextComponentString(TextFormatting.RED + "Do NOT expect much from the mod in its current state and expect a lot of things to change!"));
			player.sendMessage(new TextComponentString(TextFormatting.RED + "YOU HAVE BEEN WARNED"));
		}
		
		//TODO: Version checker changelog messages

		if (doneChecking && event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null && !triedToWarnPlayer) {
			if (!onlineVersion.isEmpty()) {
				double onlineBuild = NumberUtils.isCreatable(onlineVersion) ? Double.parseDouble(onlineVersion) : -1;
				if (onlineBuild == -1) return;
				double clientBuild = Double.parseDouble(Wizardry.VERSION);
				if (onlineBuild > clientBuild) {
					ArrayList<String> messages = new ArrayList<>();
					messages.add("Don't let your bugs be thugs! Use our patented Magic-o-gon spray for only $9.99");
					messages.add("What? A new update? Looks like magic...");
					messages.add("There's magic in the air.");
					messages.add("I smell fresh glitter!");
					messages.add("I think the devs spilled a fresh jar of glitter somewhere.");
					messages.add("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
					messages.add("before updating (◡‿◡✿) after updating (⊙ω⊙✿)");
					messages.add("(☞ﾟ∀ﾟ)☞ you're outdated my dude");
					messages.add("yo do ya got any of that changelog shit? シ");
					messages.add("yo dawg we heard you like changelogs so we put changelogs in your changelogs so you can change while you log");
					messages.add("OwO what's this?");
					messages.add("(^з^)-☆ ~bug fixes and shit~");
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
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "There's a new Wizardry update available! your version: '" + TextFormatting.RED + clientBuild + TextFormatting.GREEN + "' new version: '" + TextFormatting.YELLOW + onlineBuild + TextFormatting.GREEN + "'"));

				}
			}

			triedToWarnPlayer = true;
		}
	}
}
