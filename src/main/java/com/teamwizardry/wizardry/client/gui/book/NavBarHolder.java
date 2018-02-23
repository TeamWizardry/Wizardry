package com.teamwizardry.wizardry.client.gui.book;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;

import java.util.List;

public abstract class NavBarHolder extends GuiComponent {

	protected final List<GuiComponent> pages = Lists.newArrayList();
	protected ComponentNavBar navBar;
	protected GuiComponent currentActive;
	private boolean first = true;

	public NavBarHolder(int posX, int posY, int width, int height, GuiBook book) {
		super(posX, posY, width, height);

		navBar = new ComponentNavBar(book, (getSize().getXi() / 2) - 35, getSize().getYi() + 16, 70, pages.size());
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, (navBarChange) -> {
			update();
		});
	}

	protected void addPage(GuiComponent pageComponent) {
		if (first) {
			currentActive = pageComponent;
			first = false;
		} else {
			pageComponent.setVisible(false);
		}

		add(pageComponent);
		pages.add(pageComponent);

		navBar.maxPages = pages.size() - 1;
	}

	protected void update() {
		if (currentActive != null) currentActive.setVisible(false);

		currentActive = pages.get(navBar.getPage());

		if (currentActive != null) currentActive.setVisible(true);
	}
}
