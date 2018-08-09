package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentBookMark;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.SPELL_RECIPE_ICON;

public class ComponentRecipeBar extends ComponentBookMark {

	private boolean focused = false;

	public ComponentRecipeBar(@Nonnull GuiBook book, int id) {
		super(book, SPELL_RECIPE_ICON, id, -2, -2);

		setBookmarkText(LibrarianLib.PROXY.translate("wizardry.book.spell_recipe_recipe"), book.getBook().getSearchTextColor(), -5);

		BUS.hook(GuiComponentEvents.MouseInEvent.class, mouseInEvent -> {
			if (!focused) {
				slideOutShort();
			}
		});

		BUS.hook(GuiComponentEvents.MouseOutEvent.class, mouseOutEvent -> {
			if (!focused) {
				slideIn();
			}
		});

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (focused) {
				book.up();
				slideOutShort();
				focused = false;
			} else {
				book.placeInFocus(new ComponentSpellRecipe(book.getBook()));
				slideIn();
				focused = true;
			}
		});
	}

	public static class RecipeBookmark implements Bookmark {
		@NotNull
		@Override
		@SideOnly(Side.CLIENT)
		public ComponentBookMark createBookmarkComponent(@NotNull IBookGui book, int bookmarkIndex) {
			return new ComponentRecipeBar((GuiBook) book, bookmarkIndex);
		}
	}
}
