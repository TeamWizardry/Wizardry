package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class PageText {

	public ComponentVoid component;
	public int pageID = 0;
	private HashMap<Integer, String> pages = new HashMap<>();

	public PageText(String resource, int width, int height, int currentPageID) {
		pageID = currentPageID;
		component = new ComponentVoid(width, height);

		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, resource);
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_US/index.json");
		}

		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);
			if (json.isJsonObject()) {
				JsonObject object = json.getAsJsonObject();
				if (object.has("title") && object.has("type") && object.get("title").isJsonPrimitive() && object.get("type").isJsonPrimitive()) {
					String title = object.get("title").getAsString();
					int titleWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(title);
					ComponentText componentTitle = new ComponentText((width / 2) - (titleWidth / 2), -20, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
					componentTitle.getText().setValue(title);
					component.add(componentTitle);

					// TEXT PAGE
					if (object.has("content") && object.get("content").isJsonArray()) {
						int i = 0;
						JsonArray array = object.get("content").getAsJsonArray();
						for (JsonElement content : array) {
							if (content.isJsonObject()) {
								JsonObject contObj = content.getAsJsonObject();
								if (contObj.has("text") && contObj.get("text").isJsonArray()) {
									for (JsonElement lineElement : contObj.get("text").getAsJsonArray()) {
										if (lineElement.isJsonPrimitive()) {

											List<String> chunk = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(lineElement.getAsString(), 300);

											if (chunk.size() > 1)
												for (String subChunk : chunk)
													pages.put(i++, subChunk);
											else if (chunk.size() == 1) pages.put(i++, chunk.get(0));
										}
									}
								}
							}
						}
					}
					// TEXT PAGE
				}
			}
		}

		ComponentText page = new ComponentText(0, 0, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
		page.getText().setValue(pages.get(pageID));

		component.add(page);
	}
}
