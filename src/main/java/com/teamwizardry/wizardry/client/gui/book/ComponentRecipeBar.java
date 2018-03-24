package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentBookMark;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.SPELL_RECIPE_ICON;

public class ComponentRecipeBar extends ComponentBookMark {

	private boolean focused = false;

	public ComponentRecipeBar(@Nonnull GuiBook book, int id) {
		super(book, SPELL_RECIPE_ICON, id, -2, -2);

		clipping.setClipToBounds(true);

		ComponentText textTitle = new ComponentText(2, 1, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textTitle.setVisible(false);
		textTitle.getColor().setValue(Color.WHITE);
		textTitle.getText().setValue("Spell Recipe");
		add(textTitle);

		//textTitle.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, componentTickEvent -> {
		//	textTitle.setPos(new Vec2d(getAnimX(), textTitle.getPos().getY()));
		//});

		BUS.hook(GuiComponentEvents.MouseInEvent.class, mouseInEvent -> {
			if (!focused) {
				textTitle.setVisible(true);
				slideOutShort();
			}
		});

		BUS.hook(GuiComponentEvents.MouseOutEvent.class, mouseOutEvent -> {
			if (!focused) {
				textTitle.setVisible(false);
				slideIn();
			}
		});

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (focused) {
				if (!book.getHistory().empty()) {
					book.forceInFocus(book.getHistory().pop());
				}
				textTitle.setVisible(false);
				slideIn();
				focused = false;
			} else {
				textTitle.setVisible(true);
				book.placeInFocus(new ComponentSpellRecipe(book));
				slideOutLong();
				focused = true;
			}
		});

	}
}
