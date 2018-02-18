package com.teamwizardry.wizardry.api.book.provider;

import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageRecipe;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageStructure;
import com.teamwizardry.wizardry.api.book.hierarchy.page.PageText;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class PageProviderRegistry {

	private static final HashMap<String, Function<JsonObject, Page>> pageProviders = new HashMap<>();

	static {
		registerPageProvider("text", PageText::new);
		registerPageProvider("recipe", PageRecipe::new);
		registerPageProvider("structure", PageStructure::new);
	}

	public static void registerPageProvider(@NotNull String name, @NotNull Function<JsonObject, Page> provider) {
		registerPageProvider(new ResourceLocation(name), provider);
	}

	public static void registerPageProvider(@NotNull ResourceLocation name, @NotNull Function<JsonObject, Page> provider) {
		String key = name.toString();
		if (!pageProviders.containsKey(key))
			pageProviders.put(key, provider);
	}

	@Nullable
	public static Function<JsonObject, Page> getPageProvider(@NotNull String type) {
		return getPageProvider(new ResourceLocation(type));
	}

	@Nullable
	public static Function<JsonObject, Page> getPageProvider(@NotNull ResourceLocation type) {
		return pageProviders.getOrDefault(type.toString(), null);
	}
}
