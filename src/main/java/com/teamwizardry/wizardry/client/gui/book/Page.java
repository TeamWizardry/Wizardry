package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Page {

	//private static Sprite NAV_BAR_HOME = BookGui.SPRITE_SHEET.getSprite("hard_back", 18, 9);
	//private static Sprite NAV_BAR_BACK = BookGui.SPRITE_SHEET.getSprite("back", 18, 10);
	//private static Sprite NAV_BAR_NEXT = BookGui.SPRITE_SHEET.getSprite("forward", 18, 10);
//
	//private static Sprite NAV_BAR_HOME_HIGHLIGHTED = BookGui.SPRITE_SHEET.getSprite("hard_back_highlighted", 18, 9);
	//private static Sprite NAV_BAR_BACK_HIGHLIGHTED = BookGui.SPRITE_SHEET.getSprite("back_highlighted", 18, 10);
	//private static Sprite NAV_BAR_NEXT_HIGHLIGHTED = BookGui.SPRITE_SHEET.getSprite("forward_highlighted", 18, 10);

	public ComponentVoid component;
	public int pageID = 0;

	public Page(BookGui bookGui, String resource, int width, int height, int currentPageID) {
		pageID = currentPageID;
		component = new ComponentVoid(0, 0);

		ComponentVoid navBar = new ComponentVoid(0, 0, width, height);
		//ComponentSprite home = new ComponentSprite(NAV_BAR_HOME_HIGHLIGHTED, (width / 2) - (NAV_BAR_HOME.getWidth() / 2), height - 20);
		//ComponentSprite back = new ComponentSprite(NAV_BAR_BACK_HIGHLIGHTED, (width / 2) - (NAV_BAR_BACK.getWidth() / 2) - 40, height - 20);
		//ComponentSprite next = new ComponentSprite(NAV_BAR_NEXT_HIGHLIGHTED, (width / 2) - (NAV_BAR_NEXT.getWidth() / 2) + 40, height - 20);
		//navBar.add(home, back, next);
		component.add(navBar);

		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, resource);
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
		}

		if (stream == null) return;
		InputStreamReader reader = new InputStreamReader(stream);

		JsonElement json = new JsonParser().parse(reader);
		if (!json.isJsonObject()) return;

		JsonObject object = json.getAsJsonObject();
		if (object.has("title") && object.has("type") && object.get("title").isJsonPrimitive() && object.get("type").isJsonPrimitive()) {

			String title = object.get("title").getAsString();
			int titleWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(title);
			ComponentText componentTitle = new ComponentText((width / 2) - (titleWidth / 2), -20, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
			componentTitle.getText().setValue(title);
			component.add(componentTitle);

			if (object.get("type").getAsString().equals("content")) {
				HashMap<Integer, String> pages = new HashMap<>();

				//-------------//  TEXT PAGE   //-------------//
				if (object.has("content") && object.get("content").isJsonArray()) {
					int i = 0;
					JsonArray array = object.get("content").getAsJsonArray();
					for (JsonElement content : array) {
						if (!content.isJsonObject()) continue;
						JsonObject contObj = content.getAsJsonObject();
						if (contObj.has("text") && contObj.get("text").isJsonArray()) {
							for (JsonElement lineElement : contObj.get("text").getAsJsonArray()) {
								if (!lineElement.isJsonPrimitive()) continue;

								List<String> chunk = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(lineElement.getAsString(), 2100);

								for (String subChunk : chunk) pages.put(i++, subChunk);
							}
						}
					}
				}

				ComponentText page = new ComponentText(15, 15, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
				page.getUnicode().setValue(true);
				page.getWrap().setValue(115);
				page.getText().setValue(pages.get(pageID));
				component.add(page);
				//-------------//  TEXT PAGE   //-------------//

				//-------------//  NAV BAR   //-------------//
				//home.BUS.hook(GuiComponent.MouseClickEvent.class, componentTickEvent -> {
				//	component.setVisible(false);
				//	component.setEnabled(false);
				//	bookGui.mainIndex.setVisible(true);
				//	bookGui.mainIndex.setEnabled(true);
				//});
//
				//back.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
				//	if (pageID <= 0) back.setSprite(NAV_BAR_BACK);
				//	else back.setSprite(NAV_BAR_BACK_HIGHLIGHTED);
				//});
				//back.BUS.hook(GuiComponent.MouseClickEvent.class, componentTickEvent -> {
				//	if (pageID > 0) {
				//		pageID--;
				//		page.getText().setValue(pages.get(pageID));
				//	}
				//});
//
				//next.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
				//	if (pageID >= pages.keySet().size() - 1) next.setSprite(NAV_BAR_NEXT);
				//	else next.setSprite(NAV_BAR_NEXT_HIGHLIGHTED);
				//});
				//next.BUS.hook(GuiComponent.MouseClickEvent.class, componentTickEvent -> {
				//	if (pageID < pages.keySet().size() - 1) {
				//		pageID++;
				//		page.getText().setValue(pages.get(pageID));
				//	}
				//});
				////-------------//  NAV BAR   //-------------//
			}
		}
	}
}
