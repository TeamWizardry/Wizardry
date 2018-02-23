package com.teamwizardry.wizardry.api.book.hierarchy.entry;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.wizardry.api.book.hierarchy.IBookElement;
import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game.EntryUnlockedEvent;
import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;
import com.teamwizardry.wizardry.client.gui.book.ComponentEntryPage;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Entry implements IBookElement {

	public static final Map<ResourceLocation, Entry> ENTRIES = Maps.newHashMap();
	public final Category category;
	private final HashMap<String, TranslatedEntryHolder> translations = Maps.newHashMap();
	public JsonElement icon = null;
	public ICriterion criterion = null;

	public boolean isValid = false;

	public Entry(Category category, String rl, JsonObject json) {
		ENTRIES.put(new ResourceLocation(rl), this);

		this.category = category;

		try {
			this.icon = json.get("icon");

			if (json.has("criteria"))
				criterion = ICriterion.fromJson(json.get("criteria"));
			else criterion = null;

			if (json.has("en_us")) {
				for (Map.Entry<String, JsonElement> languageEntrySet : json.entrySet()) {
					if (!languageEntrySet.getValue().isJsonObject()) continue;
					JsonObject object = languageEntrySet.getValue().getAsJsonObject();

					translations.put(languageEntrySet.getKey(), new TranslatedEntryHolder(this, object));
					isValid = true;
				}
			} else {
				translations.put("en_us", new TranslatedEntryHolder(this, json));
				isValid = true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean isUnlocked(EntityPlayer player) {
		ICriterion criterion = getCriterion();
		if (criterion == null) return true;

		boolean unlocked = !MinecraftForge.EVENT_BUS.post(new EntryUnlockedEvent(player, this));

		return criterion.isUnlocked(player, unlocked);
	}

	@Override
	public @Nullable IBookElement getBookParent() {
		return category;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiComponent createComponent(GuiBook book) {
		return new ComponentEntryPage(book, this);
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public String getTitle() {
		String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase(Locale.ROOT);

		TranslatedEntryHolder translatedEntryHolder = translations.getOrDefault(lang, translations.get("en_us"));
		return translatedEntryHolder == null ? null : translatedEntryHolder.titleKey;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public String getDescription() {
		String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase(Locale.ROOT);

		TranslatedEntryHolder translatedEntryHolder = translations.getOrDefault(lang, translations.get("en_us"));
		return translatedEntryHolder == null ? null : translatedEntryHolder.descKey;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public List<Page> getPages() {
		String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().toLowerCase(Locale.ROOT);

		TranslatedEntryHolder translatedEntryHolder = translations.getOrDefault(lang, translations.get("en_us"));
		return translatedEntryHolder == null ? null : translatedEntryHolder.pages;
	}

	@Nullable
	public ICriterion getCriterion() {
		return criterion;
	}

	public class TranslatedEntryHolder {

		private final String titleKey;
		private final String descKey;
		private final List<Page> pages = new ArrayList<>();

		public TranslatedEntryHolder(Entry entry, JsonObject object) {
			titleKey = object.getAsJsonPrimitive("title").getAsString();
			descKey = object.getAsJsonPrimitive("description").getAsString();
			JsonArray allPages = object.getAsJsonArray("content");
			for (JsonElement pageJson : allPages) {
				Page page = Page.fromJson(entry, pageJson);
				if (page != null)
					pages.add(page);
			}
		}
	}
}
