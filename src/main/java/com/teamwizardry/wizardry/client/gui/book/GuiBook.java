package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.ModGuiBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.BookContext;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiBook extends ModGuiBook {

	static Sprite SPELL_RECIPE_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/pearl.png"));

	private ItemStack bookItemStack;

	public GuiBook(@Nonnull Book book, @Nonnull ItemStack bookItemStack) {
		super(book);
		this.bookItemStack = bookItemStack;

		if (bookItemStack.isEmpty()) return;

		if (!NBTHelper.getBoolean(bookItemStack, "has_spell", false)) return;

		List<Bookmark> bookmarks = book.addAllBookmarks(new ArrayList<>());
		bookmarks.add(new ComponentRecipeBar.RecipeBookmark());

		focusOn(new BookContext(this, book.createComponents(this), book, bookmarks, null));
	}

	public ItemStack getBookItemStack() {
		return bookItemStack;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
