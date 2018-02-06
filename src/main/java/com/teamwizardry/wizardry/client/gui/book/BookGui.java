package com.teamwizardry.wizardry.client.gui.book;

import ai.api.model.Result;
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
import com.teamwizardry.wizardry.api.AI;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
	static Sprite BOOKMARK_EXTENDED_SWITCH = GUIDE_BOOK_SHEET.getSprite("bookmark_extended_switch", 226, 22);
	static Sprite BOOKMARK_SWITCH = GUIDE_BOOK_SHEET.getSprite("bookmark_switch", 170, 22);
	static Sprite ARROW = GUIDE_BOOK_SHEET.getSprite("arrow", 22, 15);

	public ComponentVoid componentBookSearch;
	public ItemStack bookItem;
	@Nullable
	public GuiComponent activeComponent = null;
	public int bookmarkIndex;
	ComponentSprite componentBook;

	public BookGui(ItemStack book) {
		super(508, 360);
		this.bookItem = book;

		// THE BOOK SPRITE
		ComponentVoid base = new ComponentVoid(0, 0, 509, 360);
		getMainComponents().add(base);

		componentBook = new ComponentSprite(BOOK, 0, 0);
		base.add(componentBook);

		// THE BOOK SEARCH BAR
		{
			componentBookSearch = new ComponentVoid(225, 35, 200, 300);
			base.add(componentBookSearch);

			ComponentTextBox box = new ComponentTextBox(0, 0, componentBookSearch.getSize().getXi(), message -> {
				Minecraft.getMinecraft().player.sendMessage(
						new TextComponentString("Analyzing..."));

				Thread thread = new Thread(() -> {
					Result result = AI.INSTANCE.think(message);
					analyzePlayerDysfunctionality(result);

				});
				thread.start();
			});
			componentBookSearch.add(box);
		}

		// INDEX
		{
			String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase();
			String path = "documentation/" + langname;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, path + "/index.json");
			if (stream == null) {
				stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "documentation/en_us/index.json");
				path = "documentation/en_us";
			}

			ArrayList<IndexItem> indexItems = new ArrayList<>();
			if (stream != null) {
				InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
				JsonElement json = new JsonParser().parse(reader);
				if (json.isJsonObject() && json.getAsJsonObject().has("index")) {

					JsonArray array = json.getAsJsonObject().getAsJsonArray("index");
					for (JsonElement element : array) {
						if (element.isJsonObject()) {

							JsonObject chunk = element.getAsJsonObject();
							if (chunk.has("text") && chunk.has("link") && chunk.get("text").isJsonPrimitive() && chunk.get("link").isJsonPrimitive()) {

								Sprite icon = null;
								if (chunk.has("icon") && chunk.get("icon").isJsonPrimitive())
									icon = new Sprite(new ResourceLocation(chunk.getAsJsonPrimitive("icon").getAsString()));

								ItemStack stack = ItemStack.EMPTY;
								if (chunk.has("item") && chunk.get("item").isJsonPrimitive()) {
									Item itemIcon = ForgeRegistries.ITEMS.getValue(new ResourceLocation(chunk.getAsJsonPrimitive("item").getAsString()));
									if (itemIcon != null) stack = new ItemStack(itemIcon);
								}

								String finalPath = path + chunk.getAsJsonPrimitive("link").getAsString();
								String text = chunk.getAsJsonPrimitive("text").getAsString();

								IndexItem item = new IndexItem(text, finalPath, icon, stack);
								indexItems.add(item);
							}
						}
					}
				}
			}

			ComponentBookmarkSwitch bookmarkIndex = new ComponentBookmarkSwitch(
					new Vec2d(35, 35),
					this,
					componentBook,
					new ComponentIndex(componentBook, indexItems, 45, true, this, new Vec2d(45, 45)),
					this.bookmarkIndex++,
					LibrarianLib.PROXY.translate("wizardry.gui.index"),
					false, true, false, true);
			componentBook.add(bookmarkIndex);
		}

		// RECIPE BOOMARK
		if (ItemNBTHelper.getBoolean(bookItem, "has_recipe", false)) {
			ComponentBookmarkSwitch bookmarkRecipe = new ComponentBookmarkSwitch(new Vec2d(35, 35), this, componentBook, new ComponentRecipe(this), this.bookmarkIndex++, LibrarianLib.PROXY.translate("wizardry.gui.recipe"), false, false, false, true);
			componentBook.add(bookmarkRecipe);
		}

	}

	private void analyzePlayerDysfunctionality(Result result) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (result == null || result.getAction() == null || result.getScore() <= 0.6) {
			player.sendMessage(
					new TextComponentString("You're completely retarded."));
			return;
		}

		switch (result.getAction()) {

			// Some structure our stupid players tried building
			// isn't completing. Let's find it and report our findings
			case "cant_build_structure": {
				String structure1 = result.getStringParameter("mod-entities");
				String structure2 = result.getStringParameter("mod-entities1");

				if (structure1.contains("battery") || structure1.contains("plate")
						|| structure2.contains("battery") || structure2.contains("plate")) {

					player.sendMessage(
							new TextComponentString("Your can't build one of the structures? Analyzing surroundings... // todo"));
					// TODO: Find nearby structure, check incompleteness and what's missing
				} else {
					player.sendMessage(
							new TextComponentString("Your can't build one of the structures? Which one of your structures? Battery or crafting plate? // todo"));
				}
				return;
			}

			// The player has no mana. Let's find out why
			case "no_mana": {
				ItemStack halo = BaublesSupport.getItem(player, ModItems.REAL_HALO, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO);

				if (halo.isEmpty()) {
					player.sendMessage(
							new TextComponentString("Skidoodle, Skidadle, you're not wearing a fucking halo"));
				} else {
					CapManager manager = new CapManager(player);

					if (manager.getMana() < 5) {
						player.sendMessage(
								new TextComponentString("Shoot yourself up with mana."));
					} else {
						player.sendMessage(
								new TextComponentString("Everything is normal, it's right there on the bottom right of your screen"));
					}
				}
				return;
			}

			// The player believes the mana orbs shouldn't be shattering.
			// They should be.
			case "mana_orbs_shattering": {
				player.sendMessage(
						new TextComponentString("They're supposed to do that. Your transferring the mana"));
				return;
			}

			// The player can't figure out how to make a spell.
			// Let's search for their spells and check out why.
			case "spell_not_working": {

				// TODO: Loop through whole inventory, find all the staff items and debug them

				player.sendMessage(
						new TextComponentString("Your spell isn't working? Checking why... // todo"));

				return;
			}

			// The player's pearl is stuck crafting.
			// Find the nearest plate and assess the damage.
			case "spell_not_crafting": {

				// TODO: Find nearby plate, check tile information.

				player.sendMessage(
						new TextComponentString("Your spell isn't crafting properly? Checking why... // todo"));
				return;
			}

			default: {
				player.sendMessage(
						new TextComponentString("You're completely retarded."));
			}
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

		@Nullable
		public final Sprite icon;
		public final String text;
		public final String link;
		public final ItemStack iconStack;

		public IndexItem(String text, String link, @Nullable Sprite icon, ItemStack iconStack) {
			this.text = text;
			this.icon = icon;
			this.link = link;
			this.iconStack = iconStack;
		}
	}
}
