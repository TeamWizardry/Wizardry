package com.teamwizardry.wizardry.client.patreon;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.teamwizardry.wizardry.common.core.version.VersionChecker.onlineVersion;

public class OnlineCosmeticsDownloader extends Thread {

	public OnlineCosmeticsDownloader() {
		setName("Wizardry Cosmetics Downloader Thread");
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		Wizardry.logger.info("Downloading capes...");
		try {
			URL url = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/capes/capes.json");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));

			String s = IOUtils.toString(url, Charset.defaultCharset());
			JsonElement json = new JsonParser().parse(s);
			if (json != null && json.isJsonArray()) {
				for (JsonElement element : json.getAsJsonArray()) {
					if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
						String capeName = element.getAsJsonPrimitive().getAsString();

						try (InputStream in = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/capes/" + capeName + ".png").openStream()) {
							Files.copy(in, Paths.get(new ResourceLocation(Wizardry.MODID, "textures/capes/" + capeName + ".png").getPath()));
						}
					}
				}
			}

			r.close();
			Wizardry.logger.error("Cape downloads complete! -> " + onlineVersion);
		} catch (Exception e) {
			Wizardry.logger.error("Failed to download capes! :(");
		}
	}
}
