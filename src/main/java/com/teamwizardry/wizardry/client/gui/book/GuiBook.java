package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.ModGuiBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import org.jetbrains.annotations.NotNull;

public class GuiBook extends ModGuiBook {

	public GuiBook(@NotNull Book book) {
		super(book);

		//ComponentSearchBar searchBar = new ComponentSearchBar(this, 1, s -> {
		//	return Unit.INSTANCE;
		//});
		//getMainComponent().add();
	}
}
