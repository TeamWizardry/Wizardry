package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PageString implements Page {

	private final Entry entry;

	public PageString(Entry entry) {
		this.entry = entry;
	}

	@SideOnly(Side.CLIENT)
	public abstract String getText();

	@SideOnly(Side.CLIENT)
	public int lineCount(Vec2d size) {
		return (int) Math.ceil(size.getY() / Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) - 1;
	}

	@Override
	public @NotNull Entry getEntry() {
		return entry;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiComponent> createBookComponents(GuiBook book, Vec2d size) {
		List<GuiComponent> pages = new ArrayList<>();

		Minecraft minecraft = Minecraft.getMinecraft();

		int lineCount = lineCount(size);

		String text = getText();

		FontRenderer fr = minecraft.fontRenderer;

		fr.setBidiFlag(true);
		fr.setUnicodeFlag(true);
		List<String> lines = fr.listFormattedStringToWidth(text, size.getXi());

		List<String> sections = Lists.newArrayList();

		List<String> page = Lists.newArrayList();
		for (String line : lines) {
			String trim = line.trim();
			if (!trim.isEmpty()) {
				page.add(trim);
				if (page.size() >= lineCount) {
					sections.add(String.join("\n", page));
					page.clear();
				}
			}
		}

		if (!page.isEmpty())
			sections.add(String.join("\n", page));


		for (String section : sections) {
			ComponentText sectionComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			sectionComponent.getText().setValue(section);
			sectionComponent.getWrap().setValue(size.getXi());
			sectionComponent.getUnicode().setValue(true);

			pages.add(sectionComponent);
		}
		return pages;
	}
}
