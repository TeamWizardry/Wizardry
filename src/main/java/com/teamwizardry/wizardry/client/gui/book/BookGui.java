package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

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

	public ComponentSprite componentLogo;
	public BookGui(ItemStack book) {
		super(508, 360);

		ComponentSprite componentBook = new ComponentSprite(BOOK, 0, 0);
		getMainComponents().add(componentBook);

		Vec2d logoSize = new Vec2d(524, 978).divide(5);
		componentLogo = new ComponentSprite(new Sprite(new ResourceLocation(Wizardry.MODID, "textures/wizardry_logo.png")), (int) (250 + 250 / 2.0 - logoSize.getXf() / 2.0), (int) (360 / 2.0 - logoSize.getYf() / 2.0), logoSize.getXi(), logoSize.getYi());
		getMainComponents().add(componentLogo);

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		InputStream stream;
		String path;
		try {
			stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/" + langname + "/index.json");
			path = "documentation/" + langname;
		} catch (Throwable e) {
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

		ComponentIndex index = new ComponentIndex(componentBook, indexItems, 45, true, this);
		componentBook.add(index);


		//if (ItemNBTHelper.getBoolean(book, "has_recipe", false)) {
		//	ComponentSprite componentPaper = new ComponentSprite(new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/book/paper.png")), componentBackground.getSize().getXi() + 5, (componentBackground.getSize().getYi() / 2) - (148 / 2), 143, 148);
//
		//	JsonObject object = new Gson().fromJson(ItemNBTHelper.getString(book, "spell_recipe", null), JsonObject.class);
//
		//	String spellName = object.getAsJsonPrimitive("name").getAsString();
		//	int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(spellName);
		//	ComponentText title = new ComponentText((componentPaper.getSize().getXi() / 2) - (width / 2), 4);
		//	title.getText().setValue(spellName);
		//	componentPaper.add(title);
//
		//	JsonArray array = object.getAsJsonArray("list");
		//	int row = 0;
		//	for (int i = 0; i < array.size(); i++) {
		//		if (i > 0 && i % 6 == 0) row++;
//
		//		JsonElement element = array.get(i);
		//		if (!element.isJsonPrimitive()) continue;
		//		String name = element.getAsString();
		//		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
		//		if (item == null) continue;
		//		ItemStack stack = new ItemStack(item);
//
		//		ComponentStack componentStack = new ComponentStack(4 + 24 * (i % 6), 14 + 24 * row);
		//		componentStack.getItemStack().setValue(stack);
		//		componentPaper.add(componentStack);
//
		//		if (i % 6 == 0) continue;
		//		ComponentSprite next = new ComponentSprite(SPRITE_NEXT, -4 + 24 * (i % 6), 20 + 24 * row, 9, 4);
		//		componentPaper.add(next);
		//	}
//
		//	getMainComponents().add(componentPaper);
		//}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	public void hookSlider(GuiComponent<?> component, String text) {
		Slider slider = new Slider(text);
		slider.component.setPos(new Vec2d(component.getPos().getX() - component.getSize().getX(), component.getPos().getY()));
		slider.component.setEnabled(false);
		component.BUS.hook(GuiComponent.MouseInEvent.class, componentMouseIn -> {
			if (!component.getMouseOver()) return;
			component.add(slider.component);
			GlMixin.INSTANCE.transform(slider.component).setValue(new Vec3d(slider.component.getPos().getX(), slider.component.getPos().getY(), -10));
		});

		component.BUS.hook(GuiComponent.MouseOutEvent.class, componentMouseOut -> {
			if (component.getMouseOver()) return;
			slider.component.addTag("kill");
		});
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
