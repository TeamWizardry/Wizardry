package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;
import net.minecraft.client.resources.I18n;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.TITLE_BAR;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentEntryPage extends NavBarHolder {

	public ComponentEntryPage(GuiBook book, Entry entry) {
		super(16, 16, book.bookComponent.getSize().getXi() - 32, book.bookComponent.getSize().getYi() - 32, book);

		String title = I18n.format(entry.titleKey);

		ComponentSprite titleBar = new ComponentSprite(TITLE_BAR,
				(int) ((getSize().getX() / 2.0) - (TITLE_BAR.getWidth() / 2.0)),
				-getPos().getXi() - 15);
		titleBar.getColor().setValue(book.mainColor);
		add(titleBar);

		ComponentText titleText = new ComponentText((int) (TITLE_BAR.getWidth() / 2.0), (int) (titleBar.getSize().getY() / 2.0) + 1, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
		titleText.getText().setValue(title);
		titleBar.add(titleText);

		for (Page page : entry.pages) {
			for (GuiComponent pageComponent : page.createBookComponents(book, getSize())) {
				addPage(pageComponent);
			}
		}
	}
}
