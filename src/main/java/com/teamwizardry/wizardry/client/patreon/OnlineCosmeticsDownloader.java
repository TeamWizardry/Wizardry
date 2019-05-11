package com.teamwizardry.wizardry.client.patreon;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

			String s = IOUtils.toString(url, Charset.defaultCharset());
			JsonElement json = new JsonParser().parse(s);
			if (json != null && json.isJsonArray()) {
				ResourceLocation dir = new ResourceLocation(Wizardry.MODID, "wizardry_capes/");
				File file = new File(dir.getPath());
				file.mkdirs();

				for (JsonElement element : json.getAsJsonArray()) {
					if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
						String capeName = element.getAsJsonPrimitive().getAsString();

						try (InputStream in = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/capes/" + capeName + ".png").openStream()) {
							Files.copy(in, Paths.get(new ResourceLocation(Wizardry.MODID, "wizardry_capes/" + capeName + ".png").getPath()), StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							Wizardry.logger.error("Failed to download and save cape with name " + capeName);
							e.printStackTrace();
						}
					}
				}
			}

			Wizardry.logger.info("Cape downloads complete!");
		} catch (IOException e) {
			Wizardry.logger.error("Failed to download capes! :(");
			e.printStackTrace();
		}
	}
}
