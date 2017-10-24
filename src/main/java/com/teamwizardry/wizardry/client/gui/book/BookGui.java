package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
public class BookGui extends GuiBase {

	static Texture GUIDE_BOOK_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/guide_book.png"));
	static Sprite BOOK = GUIDE_BOOK_SHEET.getSprite("background", 508, 360);
	static Sprite LINE_BREAK = GUIDE_BOOK_SHEET.getSprite("index_break", 177, 2);
	static Sprite ARROW_NEXT = GUIDE_BOOK_SHEET.getSprite("arrow_next", 36, 20);
	static Sprite ARROW_NEXT_PRESSED = GUIDE_BOOK_SHEET.getSprite("arrow_next_pressed", 36, 20);
	static Sprite ARROW_PREV = GUIDE_BOOK_SHEET.getSprite("arrow_prev", 36, 20);
	static Sprite ARROW_PREV_PRESSED = GUIDE_BOOK_SHEET.getSprite("arrow_prev_pressed", 36, 20);
	static Sprite BOOKMARK_EXTENDED = GUIDE_BOOK_SHEET.getSprite("bookmark_extended", 226, 22);
	static Sprite BOOKMARK = GUIDE_BOOK_SHEET.getSprite("bookmark", 170, 22);
	static Sprite ARROW = GUIDE_BOOK_SHEET.getSprite("arrow", 22, 15);

	public ComponentSprite componentLogo;
	public ItemStack bookItem;
	@Nullable
	public GuiComponent activeComponent = null;
	public int bookmarkIndex;
	ComponentSprite componentBook;

	public BookGui(ItemStack book) {
		super(508, 360);
		this.bookItem = book;

		ComponentVoid base = new ComponentVoid(0, 0, 509, 360);
		getMainComponents().add(base);

		componentBook = new ComponentSprite(BOOK, 0, 0);
		base.add(componentBook);

		Vec2d logoSize = new Vec2d(524, 978).divide(5);
		componentLogo = new ComponentSprite(new Sprite(new ResourceLocation(Wizardry.MODID, "textures/wizardry_logo.png")), (int) (250 + 250 / 2.0 - logoSize.getXf() / 2.0), (int) (360 / 2.0 - logoSize.getYf() / 2.0), logoSize.getXi(), logoSize.getYi());
		base.add(componentLogo);

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase();
		InputStream stream;
		String path;

		stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/" + langname + "/index.json");
		path = "documentation/" + langname;
		if (stream == null) {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
			path = "documentation/en_us";
		}

		ArrayList<IndexItem> indexItems = new ArrayList<>();
		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);
			if (json.isJsonObject() && json.getAsJsonObject().has("index")) {

				JsonArray array = json.getAsJsonObject().getAsJsonArray("index");
				for (JsonElement element : array) {
					if (element.isJsonObject()) {

						JsonObject chunk = element.getAsJsonObject();
						if (chunk.has("icon") && chunk.has("text") && chunk.has("link") && chunk.get("icon").isJsonPrimitive() && chunk.get("text").isJsonPrimitive() && chunk.get("link").isJsonPrimitive()) {

							Sprite icon = new Sprite(new ResourceLocation(chunk.getAsJsonPrimitive("icon").getAsString()));
							String finalPath = path + chunk.getAsJsonPrimitive("link").getAsString();
							String text = chunk.getAsJsonPrimitive("text").getAsString();

							IndexItem item = new IndexItem(text, icon, finalPath);
							indexItems.add(item);
						}
					}
				}
			}
		}

		ComponentBookmark bookmarkIndex = new ComponentBookmark(new Vec2d(35, 35), this, componentBook, this.bookmarkIndex++, new ComponentIndex(componentBook, indexItems, 45, true, this, new Vec2d(45, 45)), "Index", true);
		componentBook.add(bookmarkIndex);

		if (ItemNBTHelper.getBoolean(bookItem, "has_recipe", false)) {
			ComponentBookmark bookmarkRecipe = new ComponentBookmark(new Vec2d(35, 35), this, componentBook, this.bookmarkIndex++, new ComponentRecipe(this), "Spell Recipe", false);
			componentBook.add(bookmarkRecipe);
		}

	}

	@Override
	public boolean adjustGuiSize() {
		return false;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	public static class IndexItem {

		public final Sprite icon;
		public final String text;
		public final String link;

		public IndexItem(String text, Sprite icon, String link) {
			this.text = text;
			this.icon = icon;
			this.link = link;
		}
	}
}
