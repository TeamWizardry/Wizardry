package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentBookMark;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.SPELL_RECIPE_ICON;

public class ComponentRecipeBar extends ComponentBookMark {

	private boolean focused = false;

	public ComponentRecipeBar(@Nonnull GuiBook book, int id) {
		super(book, SPELL_RECIPE_ICON, id, -2, -2);

		setBookmarkText(LibrarianLib.PROXY.translate("wizardry.book.spell_recipe_recipe"), book.getBook().getSearchTextColor(), -5);

		slideOutShort();

		BUS.hook(GuiComponentEvents.MouseInEvent.class, mouseInEvent -> {
			if (!focused)
				slideOutLong();
		});

		BUS.hook(GuiComponentEvents.MouseOutEvent.class, mouseOutEvent -> {
			if (!focused)
				slideOutShort();
		});

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (focused) {
				if (!book.getHistory().empty()) {
					book.forceInFocus(book.getHistory().pop());
				}
				slideOutShort();
				focused = false;
			} else {
				book.placeInFocus(new ComponentSpellRecipe(book));
				slideIn();
				focused = true;
			}
		});

	}
}
