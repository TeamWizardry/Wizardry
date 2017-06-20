package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class SubIndex {


	public ComponentVoid component;
	public int pageID = 0;

	public SubIndex(BookGui bookGui, String resource, int width, int height, int currentPageID) {
		pageID = currentPageID;
		component = new ComponentVoid(0, 0);

		//ComponentVoid navBar = new ComponentVoid(0, 0, width, height);
		//ComponentSprite home = new ComponentSprite(NAV_BAR_HOME_HIGHLIGHTED, (width / 2) - (NAV_BAR_HOME.getWidth() / 2), height - 20);
		//ComponentSprite back = new ComponentSprite(NAV_BAR_BACK_HIGHLIGHTED, (width / 2) - (NAV_BAR_BACK.getWidth() / 2) - 40, height - 20);
		//ComponentSprite next = new ComponentSprite(NAV_BAR_NEXT_HIGHLIGHTED, (width / 2) - (NAV_BAR_NEXT.getWidth() / 2) + 40, height - 20);
		//navBar.add(home, back, next);
		//component.add(navBar);

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

			HashMap<Integer, List<ComponentVoid>> pages = new HashMap<>();
			List<ComponentVoid> listItems = new ArrayList<>();

			//-------------//  INDEX PAGE   //-------------//
			ComponentList list = new ComponentList(15, 15);
			component.add(list);

			if (object.has("content") && object.get("content").isJsonArray()) {
				JsonArray array = object.get("content").getAsJsonArray();
				int itemCount = 0;
				int pageNb = 0;
				for (JsonElement content : array) {
					if (!content.isJsonObject()) continue;
					JsonObject contObj = content.getAsJsonObject();
					if (contObj.has("link") && contObj.get("link").isJsonPrimitive()
							&& contObj.has("icon") && contObj.get("icon").isJsonPrimitive()
							&& contObj.has("text") && contObj.get("text").isJsonPrimitive()
							&& contObj.has("info") && contObj.get("info").isJsonPrimitive()) {
						String link = contObj.get("link").getAsString();
						String icon = contObj.get("icon").getAsString();
						String text = contObj.get("text").getAsString();
						String info = contObj.get("info").getAsString();

						ComponentVoid listItem = new ComponentVoid(0, 0, width - 30, 16);
						String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();

						listItem.BUS.hook(GuiComponent.MouseClickEvent.class, mouseClickEvent -> {
							list.setVisible(false);
							list.setEnabled(false);
							componentTitle.setVisible(false);
							componentTitle.setEnabled(false);
							//navBar.setEnabled(false);
							//navBar.setVisible(false);
							component.add(new Page(bookGui, "documentation/" + langname + link, width, height, 0).component);
						});

						ResourceLocation loc = new ResourceLocation(icon);
						ResourceLocation fixedLoc = new ResourceLocation(loc.getResourceDomain(), loc.getResourcePath());
						ComponentSprite listItemIcon = new ComponentSprite(new Sprite(fixedLoc), 0, 0, 16, 16);

						ComponentText listItemText = new ComponentText(20, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
						listItemText.getUnicode().setValue(true);
						listItemText.getText().setValue(text);
						listItemText.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
							if (listItem.getMouseOver())
								listItemText.getText().setValue(" " + TextFormatting.ITALIC + text);
							else listItemText.getText().setValue(text);

						});
						listItem.add(listItemIcon, listItemText);

						if (itemCount >= 8) {
							itemCount = 0;
							pages.put(pageNb, listItems);
							listItems.clear();
							pageNb++;
						} else {
							itemCount++;
							listItems.add(listItem);
						}
					}
				}
			}
			if (!listItems.isEmpty())
				pages.put(pages.size(), listItems);

			if (pages.keySet().contains(pageID))
				for (ComponentVoid listItem : pages.get(pageID)) {
					list.add(listItem);
				}
			//-------------//  INDEX PAGE   //-------------//

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
			//		for (ComponentVoid listItem : pages.get(pageID)) list.remove(listItem);
			//		for (ComponentVoid listItem : pages.get(pageID - 1)) list.add(listItem);
			//		pageID--;
			//	}
			//});
//
			//next.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
			//	if (pageID >= pages.keySet().size() - 1) next.setSprite(NAV_BAR_NEXT);
			//	else next.setSprite(NAV_BAR_NEXT_HIGHLIGHTED);
			//});
			//next.BUS.hook(GuiComponent.MouseClickEvent.class, componentTickEvent -> {
			//	if (pageID < pages.keySet().size() - 1) {
			//		for (ComponentVoid listItem : pages.get(pageID)) list.remove(listItem);
			//		for (ComponentVoid listItem : pages.get(pageID + 1)) list.add(listItem);
			//		pageID++;
			//	}
			//});
			//-------------//  NAV BAR   //-------------//
		}
	}
}
