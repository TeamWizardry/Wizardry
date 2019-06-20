package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;

public class MainMenu extends GuiComponent {

	public MainMenu(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);

		ComponentAddCard card = new ComponentAddCard(0, WorktableGui2.cardWidth, WorktableGui2.cardHeight);
		add(card);
	}
}
