package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.eventbus.Hook;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.features.math.Align2d;

import java.awt.*;

class ComponentAddCard extends GuiComponent {

	private final WorktableGui2 gui;

	public ComponentAddCard(WorktableGui2 gui, final int x, final int width, final int height) {
		super(x, 0);
		this.gui = gui;
		RectLayer base = new RectLayer(Color.DARK_GRAY, 0, 0, width, height);
		add(base);
		setSize(base.getSize());

		TextLayer text = new TextLayer(0, 0, width, height);
		text.setText("Add");
		text.setColor(Color.WHITE);
		text.setAlign(Align2d.CENTER);
		add(text);
	}

	@Hook
	private void click(GuiComponentEvents.MouseClickEvent e) {
		gui.selectModuleType.reveal();
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();


	}
}
