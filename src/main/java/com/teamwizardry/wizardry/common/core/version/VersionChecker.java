package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class VersionChecker {

	public static boolean doneChecking = false;
	public static String onlineVersion = "";
	private static boolean triedToWarnPlayer = false;

	private VersionChecker() {
	}

	public static void init() {
		new ThreadVersionChecker();
		MinecraftForge.EVENT_BUS.register(VersionChecker.class);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (doneChecking && event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null && !triedToWarnPlayer) {
			if (!onlineVersion.isEmpty()) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				double onlineBuild = NumberUtils.isNumber(onlineVersion) ? Double.parseDouble(onlineVersion) : -1;
				if (onlineBuild == -1) return;
				double clientBuild = Double.parseDouble(Wizardry.VERSION);
				if (onlineBuild > clientBuild) {
					ArrayList<String> messages = new ArrayList<>();
					messages.add("Don't let your bugs be thugs! Use our patented Magic-ogon spray for only $9.99");
					messages.add("What? A new update? Looks like magic...");
					messages.add("There's magic in the air.");
					messages.add("I smell fresh glitter!");
					messages.add("I think the devs spilled a fresh jar of glitter somewhere.");
					messages.add("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
					messages.add("before updating (◡‿◡✿) after updating (⊙ω⊙✿)");
					messages.add("(☞ﾟ∀ﾟ)☞ you're outdated my dude");
					messages.add("yo dog ya got any of that changelog shit? シ");
					messages.add("yo dawg we heard you like changelogs so we put changelogs in your changelogs so you can change while you log");
					messages.add("SASAAAAGEYOOO, SASAAAGEYOOO, WIZARDRY BUGS FIXED, SASAAAAGEYOOOOO");
					messages.add("⊙ω⊙ what's this?");
					messages.add("(^з^)-☆ ~bug fixes and shit~");
					messages.add("◔_◔ looks like youre out of date");
					messages.add("ಠ~ಠ why aren't you up to date yet");
					messages.add("Get in the update, Shinji!");
					messages.add("( 　ﾟ,_ゝﾟ) is that.. ( 　ﾟ,_ゝﾟ) ..a mod update i see?");
					messages.add("ಠ_ರೃ Watson, I believe they have an outdated jar.");
					messages.add("╚(•⌂•)╝ update your jars, dammit!");
					messages.add("(」ﾟヘﾟ)」y u no update?!");
					messages.add("*:･ﾟ✧ᕕ( ᐛ )ᕗ");
					messages.add("hey friend, could you update me please? ( /)w(\\✿)");
					messages.add("v(ಥ ̯ ಥ)v mfw not up-to-date");

					player.sendMessage(new TextComponentString(TextFormatting.YELLOW + messages.get(ThreadLocalRandom.current().nextInt(messages.size() - 1))));
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "There's a new Wizardry update available! your version: '" + TextFormatting.RED + clientBuild + TextFormatting.GREEN + " new version: '" + TextFormatting.YELLOW + onlineBuild + TextFormatting.GREEN + "'"));

				}
			}

			triedToWarnPlayer = true;
		}
	}

}
