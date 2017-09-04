package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ComponentIndex extends GuiComponent<ComponentIndex> {

	private static final int plateWidth = 195;
	private static final int buffer = 10;
	private final GuiComponent<?> componentBook;
	private final int plateHeight;
	private final Vec2d pos;
	private ArrayList<ComponentVoid> prevComps = new ArrayList<>();
	private GuiComponent<?> prevContent;

	public ComponentIndex(GuiComponent<?> componentBook, ArrayList<BookGui.IndexItem> list, int plateHeight, boolean isMainIndex, BookGui bookGui, Vec2d pos) {
		super(pos.getXi(), pos.getYi(), plateWidth, 120);
		this.componentBook = componentBook;
		this.plateHeight = plateHeight;
		this.pos = pos;

		HashMap<Integer, ArrayList<BookGui.IndexItem>> pages = getPages(list);

		ComponentNavBar navBar = new ComponentNavBar(0, componentBook.getSize().getYi() - getSize().getYi() + 80, 170, 15, pages.size() - 1);
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, eventNavBarChange -> {
			for (ComponentVoid componentVoid : prevComps) componentVoid.invalidate();
			prevComps.clear();

			if (pages.size() <= navBar.getPage()) return;

			for (int i = 0; i < pages.get(navBar.getPage()).size(); i++) {
				BookGui.IndexItem indexItem = pages.get(navBar.getPage()).get(i);

				ComponentVoid plate = new ComponentVoid(0, i * plateHeight + i * buffer, plateWidth, plateHeight + buffer);
				if (!getChildren().contains(plate)) add(plate);
				plate.setVisible(true);
				plate.setEnabled(false);

				plate.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
					GlStateManager.pushMatrix();
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					if (isMainIndex) {
						if (!event.getComponent().getMouseOver()) GlStateManager.color(0, 0, 0);
						else GlStateManager.color(0, 0.5f, 1);
					}
					indexItem.icon.getTex().bind();
					indexItem.icon.draw((int) ClientTickHandler.getPartialTicks(), plate.getPos().getXi(), plate.getPos().getYf() + (plate.getSize().getYf() / 2 - plateHeight / 2.0f), plateHeight, plateHeight);
					GlStateManager.popMatrix();
				});

				plate.BUS.hook(GuiComponent.MouseClickEvent.class, (event) -> {
					Pair<String, GuiComponent<?>> pair = next(indexItem.link);
					if (pair == null) return;

					if (isMainIndex && !bookGui.componentLogo.isInvalid()) bookGui.componentLogo.invalidate();

					if (pair.getFirst().equals("index")) {

						getChildren().forEach(guiComponent -> {
							guiComponent.setVisible(false);
							guiComponent.setEnabled(false);
						});

						GuiComponent<?> subindex = pair.getSecond();
						add(subindex);
					} else if (pair.getFirst().equals("content")) {
						add(pair.getSecond());
						if (prevContent != null) prevContent.invalidate();
						prevContent = pair.getSecond();
					}
				});

				double height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
				ComponentText text = new ComponentText(plateHeight + 10, (int) (plateHeight / 2.0 + height / 2.0), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
				text.getText().setValue(indexItem.text);
				text.getScale().setValue(2f);
				text.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
					text.getText().setValue((plate.getMouseOver() ? TextFormatting.ITALIC + " " : "") + indexItem.text);
				});
				plate.add(text);

				ComponentSprite indexBreak1 = new ComponentSprite(BookGui.LINE_BREAK, 0, 0, 177, 2);
				ComponentSprite indexBreak2 = new ComponentSprite(BookGui.LINE_BREAK, 0, plate.getSize().getYi(), 177, 2);
				plate.add(indexBreak1, indexBreak2);
				prevComps.add(plate);
			}
		});
		navBar.BUS.fire(new EventNavBarChange());
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}

	private HashMap<Integer, ArrayList<BookGui.IndexItem>> getPages(ArrayList<BookGui.IndexItem> items) {
		HashMap<Integer, ArrayList<BookGui.IndexItem>> pages = new HashMap<>();
		int itemCount = 0, pageNb = 0, itemsPerPage = (int) (300.0 / (plateHeight + buffer));

		ArrayList<BookGui.IndexItem> tmp = new ArrayList<>();
		for (BookGui.IndexItem item : items) {
			if (itemCount > itemsPerPage) {
				itemCount = 0;
				pages.put(pageNb++, tmp);
				tmp.clear();
			} else {
				itemCount++;
				tmp.add(item);
			}
		}
		if (!tmp.isEmpty()) pages.put(pageNb > 0 ? ++pageNb : 0, tmp);

		return pages;
	}

	private Pair<String, GuiComponent<?>> next(String newResource) {
		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, newResource);
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
		}
		if (stream == null) return null;
		InputStreamReader reader = new InputStreamReader(stream);
		JsonElement json = new JsonParser().parse(reader);
		if (!json.isJsonObject()) return null;

		JsonObject object = json.getAsJsonObject();
		if (object.has("type") && object.get("type").isJsonPrimitive()) {
			String type = object.getAsJsonPrimitive("type").getAsString();
			if (type.equals("index")) {
				return new Pair<>(type, getNewIndex(newResource));
			} else if (type.equals("content")) {
				return new Pair<>(type, new ComponentContentPage(225, -getPos().getYi() + pos.getYi() - 10, 200, 300, newResource, componentBook));
			}
		}
		return null;
	}

	private ComponentIndex getNewIndex(String newResource) {
		int newPlateHeight = 32;

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		InputStream stream;
		String path;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, newResource);
			path = "documentation/" + langname;
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
			path = "documentation/en_us";
		}

		if (stream == null) return null;
		InputStreamReader reader = new InputStreamReader(stream);
		JsonElement json = new JsonParser().parse(reader);
		if (!json.isJsonObject()) return null;

		ArrayList<BookGui.IndexItem> indexItems = new ArrayList<>();
		JsonObject object = json.getAsJsonObject();
		if (object.has("title") && object.has("type") && object.get("title").isJsonPrimitive() && object.get("type").isJsonPrimitive()) {

			// TODO: String title = object.getAsJsonPrimitive("title").getAsString();


			if (object.has("content") && object.get("content").isJsonArray()) {
				JsonArray array = object.get("content").getAsJsonArray();

				for (JsonElement element : array) {
					if (element.isJsonObject()) {

						JsonObject chunk = element.getAsJsonObject();
						if (chunk.has("icon") && chunk.has("text") && chunk.has("link") && chunk.get("icon").isJsonPrimitive() && chunk.get("text").isJsonPrimitive() && chunk.get("link").isJsonPrimitive()) {

							Sprite icon = new Sprite(new ResourceLocation(chunk.getAsJsonPrimitive("icon").getAsString()));
							String finalPath = path + chunk.getAsJsonPrimitive("link").getAsString();
							String text = chunk.getAsJsonPrimitive("text").getAsString();

							indexItems.add(new BookGui.IndexItem(text, icon, finalPath));
						}
					}
				}
			}
		}
		return new ComponentIndex(componentBook, indexItems, newPlateHeight, false, null, Vec2d.ZERO);
	}
}
