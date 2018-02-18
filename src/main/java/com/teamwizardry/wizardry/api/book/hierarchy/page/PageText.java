package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PageText implements Page {

	private String text;

	public PageText(JsonObject jsonElement) {
		text = jsonElement.getAsJsonPrimitive("value").getAsString();
	}

	@NotNull
	@Override
	public String getType() {
		return "text";
	}

	@Override
	public List<String> getSearchableKeys() {
		return Lists.newArrayList();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiComponent> createBookComponents(GuiBook book, Vec2d size) {
		List<GuiComponent> pages = new ArrayList<>();
		if (text == null) return pages;

		List<String> list = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(text, 2200);

		for (String section : list) {
			if (section.trim().isEmpty()) continue;

			ComponentText sectionComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			sectionComponent.getText().setValue(section);
			sectionComponent.getWrap().setValue(size.getXi());
			sectionComponent.getUnicode().setValue(true);

			pages.add(sectionComponent);
		}
		return pages;
	}
}
