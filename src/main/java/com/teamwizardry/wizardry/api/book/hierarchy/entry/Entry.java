package com.teamwizardry.wizardry.api.book.hierarchy.entry;

import com.google.common.collect.Lists;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Entry implements IBookElement {

	public static final Map<ResourceLocation, Entry> ENTRIES = Maps.newHashMap();

	public final Category category;

	public final List<Page> pages;
	public final String titleKey;
	public final String descKey;
	public final JsonElement icon;

	public ICriterion criterion = null;

	public boolean isValid = false;

	public Entry(Category category, String rl, JsonObject json) {
		ENTRIES.put(new ResourceLocation(rl), this);

		List<Page> pages = Lists.newArrayList();
		String titleKey = "";
		String descKey = "";
		JsonElement icon = new JsonObject();

		this.category = category;
		try {
			titleKey = json.getAsJsonPrimitive("title").getAsString();
			descKey = json.getAsJsonPrimitive("description").getAsString();
			icon = json.get("icon");
			JsonArray allPages = json.getAsJsonArray("content");
			for (JsonElement pageJson : allPages) {
				Page page = Page.fromJson(this, pageJson);
				if (page != null)
					pages.add(page);
			}
			if (json.has("criteria"))
				criterion = ICriterion.fromJson(json.get("criteria"));
			isValid = true;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		this.pages = pages;
		this.titleKey = titleKey;
		this.descKey = descKey;
		this.icon = icon;
	}

	public boolean isUnlocked(EntityPlayer player) {
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
}
