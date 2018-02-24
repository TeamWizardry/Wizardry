package com.teamwizardry.wizardry.api.book.provider;

import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageRecipe;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageStructure;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageText;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.BiFunction;

public class PageTypes {

	private static final HashMap<String, BiFunction<Entry, JsonObject, Page>> pageProviders = new HashMap<>();

	static {
		registerPageProvider("text", PageText::new);
		registerPageProvider("recipe", PageRecipe::new);
		registerPageProvider("structure", PageStructure::new);
	}

	public static void registerPageProvider(@NotNull String name, @NotNull BiFunction<Entry, JsonObject, Page> provider) {
		registerPageProvider(new ResourceLocation(name), provider);
	}

	public static void registerPageProvider(@NotNull ResourceLocation name, @NotNull BiFunction<Entry, JsonObject, Page> provider) {
		String key = name.toString();
		if (!pageProviders.containsKey(key))
			pageProviders.put(key, provider);
	}

	@Nullable
	public static BiFunction<Entry, JsonObject, Page> getPageProvider(@NotNull String type) {
		return getPageProvider(new ResourceLocation(type.toLowerCase(Locale.ROOT)));
	}

	@Nullable
	public static BiFunction<Entry, JsonObject, Page> getPageProvider(@NotNull ResourceLocation type) {
		return pageProviders.getOrDefault(type.toString(), null);
	}
}
