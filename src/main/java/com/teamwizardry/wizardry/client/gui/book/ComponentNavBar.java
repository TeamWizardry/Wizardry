package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.util.math.MathHelper;

import static com.teamwizardry.wizardry.client.gui.book.BookGui.*;

public class ComponentNavBar extends GuiComponent<ComponentNavBar> {

	private int page = 0;

	public ComponentNavBar(int posX, int posY, int width, int height, int maxPages) {
		super(posX, posY, width, height);

		ComponentSprite prev = new ComponentSprite(ARROW_PREV_PRESSED, (int) ((getSize().getX() / 2.0) - (ARROW_PREV_PRESSED.getWidth() / 2.0) - 40), (int) ((getSize().getY() / 2.0) - (ARROW_NEXT.getHeight() / 2.0)));
		ComponentSprite next = new ComponentSprite(ARROW_NEXT_PRESSED, (int) ((getSize().getX() / 2.0) - (ARROW_NEXT_PRESSED.getWidth() / 2.0) + 40), (int) ((getSize().getY() / 2.0) - (ARROW_PREV.getHeight() / 2.0)));
		add(prev, next);

		prev.BUS.hook(GuiComponent.ComponentTickEvent.class, event -> {
			int x = MathHelper.clamp(page - 1, 0, maxPages);
			prev.setSprite(page == x ? ARROW_PREV : ARROW_PREV_PRESSED);
			event.getComponent().setEnabled(page == x);
		});
		prev.BUS.hook(GuiComponent.MouseClickEvent.class, event -> {
			if (!event.getComponent().getMouseOver()) return;

			int x = MathHelper.clamp(page - 1, 0, maxPages);
			if (page == x) return;

			page = x;

			EventNavBarChange eventNavBarChange = new EventNavBarChange();
			BUS.fire(eventNavBarChange);
		});

		next.BUS.hook(GuiComponent.ComponentTickEvent.class, event -> {
			int x = MathHelper.clamp(page + 1, 0, maxPages);
			next.setSprite(page == x ? ARROW_NEXT : ARROW_NEXT_PRESSED);
			event.getComponent().setEnabled(page == x);
		});
		next.BUS.hook(GuiComponent.MouseClickEvent.class, event -> {
			if (!event.getComponent().getMouseOver()) return;

			int x = MathHelper.clamp(page + 1, 0, maxPages);
			if (page == x) return;

			page = x;

			EventNavBarChange eventNavBarChange = new EventNavBarChange();
			BUS.fire(eventNavBarChange);
		});
	}


	public int getPage() {
		return page;
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
