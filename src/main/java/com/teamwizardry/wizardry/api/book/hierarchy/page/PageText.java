package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

public class PageText extends PageString {

	private final String key;

	public PageText(Entry entry, JsonObject jsonElement) {
		super(entry);
		key = jsonElement.getAsJsonPrimitive("value").getAsString();
	}

	@Override
	public Collection<String> getSearchableKeys() {
		return Lists.newArrayList(key);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getText() {
		return I18n.format(key);
	}
}
