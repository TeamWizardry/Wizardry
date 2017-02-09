package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by LordSaad.
 */
public class PageText {

	ComponentVoid page;

	public PageText(String resource, int width, int height) {
		page = new ComponentVoid(width, height);

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, resource);
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_US/index.json");
		}

		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);

			if (json.isJsonObject() && json.getAsJsonObject().has("title") && json.getAsJsonObject().has("type") && json.getAsJsonObject().has("content")) {

			}
		}
	}
}
