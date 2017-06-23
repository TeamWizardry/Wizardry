package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class ComponentContentPage extends GuiComponent<ComponentContentPage> {

	private final String resource;
	private GuiComponent<?> prevComps;

	public ComponentContentPage(int posX, int posY, int width, int height, String resource, GuiComponent<?> componentBook) {
		super(posX, posY, width, height);
		this.resource = resource;

		HashMap<Integer, GuiComponent<?>> pages = getContent();

		ComponentNavBar navBar = new ComponentNavBar((int) (getSize().getX() / 2.0 - 170 / 2.0), componentBook.getSize().getYi() - getSize().getYi() + 249, 170, 15, pages.size() - 1);
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, eventNavBarChange -> {
			if (prevComps != null) {
				GuiComponent<?> parent = prevComps.getParent();
				if (parent != null) parent.remove(prevComps);
			}

			if (pages.size() <= navBar.getPage()) return;

			GuiComponent<?> content = pages.get(navBar.getPage());
			add(content);
			prevComps = content;
		});
		navBar.BUS.fire(new EventNavBarChange());
	}

	private HashMap<Integer, GuiComponent<?>> getContent() {
		HashMap<Integer, GuiComponent<?>> pages = new HashMap<>();

		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, resource);
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
		}

		if (stream == null) return pages;
		InputStreamReader reader = new InputStreamReader(stream);

		JsonElement json = new JsonParser().parse(reader);
		if (!json.isJsonObject()) return pages;

		JsonObject object = json.getAsJsonObject();
		if (object.has("title") && object.has("type") && object.get("title").isJsonPrimitive() && object.get("type").isJsonPrimitive()) {

			// TODO: String title = object.get("title").getAsString();
			if (object.get("type").getAsString().equals("content")) {


				if (object.has("content") && object.get("content").isJsonArray()) {
					JsonArray array = object.get("content").getAsJsonArray();

					int i = 0;
					for (JsonElement content : array) {
						if (!content.isJsonObject()) continue;

						JsonObject contObj = content.getAsJsonObject();
						if (contObj.has("text") && contObj.get("text").isJsonArray()) {

							for (JsonElement lineElement : contObj.get("text").getAsJsonArray()) {
								if (!lineElement.isJsonPrimitive()) continue;

								// split each line in json into pages if need be.
								List<String> chunks = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(lineElement.getAsString(), 1950);

								for (String chunk : chunks) {
									ComponentText page = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
									page.getScale().setValue(2f);
									page.getUnicode().setValue(true);
									page.getWrap().setValue(100);
									page.getText().setValue(chunk);
									pages.put(i++, page);

									ComponentSprite lineBreak1 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), -5, 177, 2);
									ComponentSprite lineBreak2 = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), getSize().getYi() - 5, 177, 2);
									page.add(lineBreak1, lineBreak2);
								}
							}
						} else {
							// TODO
						}
					}
				}
			}
		}
		return pages;
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
