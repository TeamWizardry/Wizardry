package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentBookMark;
import com.teamwizardry.librarianlib.features.math.Vec2d;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.SPELL_RECIPE_ICON;

public class ComponentRecipeBar extends ComponentBookMark {

	private boolean focused = false;

	public ComponentRecipeBar(@Nonnull GuiBook book, int id) {
		super(book, SPELL_RECIPE_ICON, id);

		clipping.setClipToBounds(true);

		ComponentText textTitle = new ComponentText(2, 3, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textTitle.setVisible(true);
		textTitle.getText().setValue("Spell Recipe");
		add(textTitle);
		textTitle.getTransform().setTranslateZ(20);

		textTitle.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, componentTickEvent -> {
			textTitle.setPos(new Vec2d(getAnimX(), textTitle.getPos().getY()));
		});

		BUS.hook(GuiComponentEvents.MouseInEvent.class, mouseInEvent -> {
			if (!focused)
				slideOutShort();
		});

		BUS.hook(GuiComponentEvents.MouseOutEvent.class, mouseOutEvent -> {
			if (!focused)
				slideIn();
		});

		BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			if (focused) {
				if (!book.getHistory().empty()) {
					book.forceInFocus(book.getHistory().pop());
				}
				slideIn();
				focused = false;
			} else {
				book.placeInFocus(new ComponentSpellRecipe(book));
				slideOutLong();
				focused = true;
			}
		});

	}
}
