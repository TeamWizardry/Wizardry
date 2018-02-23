package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Map;

public class PageTranslated extends PageString {

	private final String defaultValue;
	private final Map<String, String> translations = Maps.newHashMap();

	public PageTranslated(Entry entry, JsonObject jsonElement) {
		super(entry);
		this.defaultValue = jsonElement.getAsJsonPrimitive("en_us").getAsString();
		for (Map.Entry<String, JsonElement> kv : jsonElement.entrySet())
			if (!kv.getKey().equals("type"))
				translations.put(kv.getKey(), kv.getValue().getAsString());
	}

	@Override
	public Collection<String> getSearchableStrings() {
		return translations.values();
	}


	@Override
	@SideOnly(Side.CLIENT)
	public String getText() {
		String lang = Minecraft.getMinecraft().gameSettings.language;

		return translations.getOrDefault(lang, defaultValue);
	}
}
