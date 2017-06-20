package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.math.Vec2d;

import static com.teamwizardry.wizardry.client.gui.book.BookGui.*;

public class ComponentNavBar extends GuiComponent<ComponentNavBar> {

	private int page = 0;

	public ComponentNavBar(int posX, int posY, int width, int height, int maxPages) {
		super(posX, posY, width, height);

		ComponentSprite prev = new ComponentSprite(ARROW_PREV_PRESSED, (int) ((getSize().getX() / 2.0) - (ARROW_PREV_PRESSED.getWidth() / 2.0) - 40), (int) ((getSize().getY() / 2.0) - (ARROW_NEXT.getHeight() / 2.0)));
		ComponentSprite next = new ComponentSprite(ARROW_NEXT_PRESSED, (int) ((getSize().getX() / 2.0) - (ARROW_NEXT_PRESSED.getWidth() / 2.0) + 40), (int) ((getSize().getY() / 2.0) - (ARROW_PREV.getHeight() / 2.0)));
		add(prev, next);

		prev.BUS.hook(GuiComponent.ComponentTickEvent.class, event -> {
			prev.setSprite(page <= 0 ? ARROW_PREV : ARROW_PREV_PRESSED);
			event.getComponent().setEnabled(page > 0);
		});
		prev.BUS.hook(GuiComponent.MouseClickEvent.class, event -> {
			EventNavBarChange eventNavBarChange = new EventNavBarChange();
			BUS.fire(eventNavBarChange);
			if (!eventNavBarChange.isCanceled())
				page = page - 1 < 0 ? 0 : page - 1;
		});

		next.BUS.hook(GuiComponent.ComponentTickEvent.class, componentTickEvent -> {
			next.setSprite(page >= maxPages ? ARROW_NEXT : ARROW_NEXT_PRESSED);
			componentTickEvent.getComponent().setEnabled(page < maxPages);
		});
		next.BUS.hook(GuiComponent.MouseClickEvent.class, componentTickEvent -> {
			EventNavBarChange eventNavBarChange = new EventNavBarChange();
			BUS.fire(eventNavBarChange);
			if (!eventNavBarChange.isCanceled()) {
				page = page + 1 >= maxPages ? maxPages : page + 1;
			}
		});
	}


	public int getPage() {
		return page;
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
