package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.math.Vec2d;

import java.awt.*;

public class ComponentMainMenu extends GuiComponent {

	private WorktableGui2 gui;
	private ComponentAddCard addCard;
	private RectLayer cardArea = new RectLayer(Color.CYAN);

	public ComponentMainMenu(WorktableGui2 gui) {
		this.gui = gui;
		setClipToBounds(true);

		addCard = new ComponentAddCard(this.gui, 0, WorktableGui2.cardWidth, WorktableGui2.cardHeight);
		add(cardArea, addCard);
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		cardArea.setSize(getSize());

		addCard.setPos(new Vec2d(getWidth() / 2.0 - addCard.getWidth() / 2.0, getHeight() / 2.0 - addCard.getHeight() / 2.0));
	}
}
