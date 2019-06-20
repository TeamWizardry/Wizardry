package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;

import java.awt.*;

class ComponentAddCard extends GuiComponent {

	public ComponentAddCard(final int x, final int width, final int height) {
		super(x, 0);
		RectLayer base = new RectLayer(Color.BLUE, 0, 0, width, height);
		add(base);
		setSize(base.getSize());
	}

}
