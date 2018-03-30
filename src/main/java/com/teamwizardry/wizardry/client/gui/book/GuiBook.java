package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.ModGuiBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiBook extends ModGuiBook {

	static Sprite SPELL_RECIPE_ICON = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/pearl.png"));

	private ItemStack bookItemStack;

	public GuiBook(@Nonnull Book book, @Nonnull ItemStack bookItemStack) {
		super(book);
		this.bookItemStack = bookItemStack;

		//RenderCodex.openingCooldownRight = 10;

		if (bookItemStack.isEmpty()) return;

		if (!ItemNBTHelper.getBoolean(bookItemStack, "has_spell", false)) return;

		ComponentRecipeBar recipe = new ComponentRecipeBar(this, 1);

		getMainComponents().add(recipe);
	}

	public ItemStack getBookItemStack() {
		return bookItemStack;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
