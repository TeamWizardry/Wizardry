package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.*;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentNavBar extends GuiComponent {

	private int page = 0;

	/**
	 * @param navBarHolder The parent of holding this nav bar
	 */
	public ComponentNavBar(GuiBook book, BookGuiComponent navBarHolder, int posX, int posY, int width, int maxPages) {
		super(posX, posY, width, 20);

		ComponentSprite back = new ComponentSprite(ARROW_BACK, 0, (int) ((getSize().getY() / 2.0) - (ARROW_NEXT.getHeight() / 2.0)));
		ComponentSprite home = new ComponentSprite(ARROW_HOME, (int) ((getSize().getX() / 2.0) - (ARROW_HOME.getWidth() / 2.0)), (int) ((getSize().getY() / 2.0) - (ARROW_NEXT.getHeight() / 2.0)));
		ComponentSprite next = new ComponentSprite(ARROW_NEXT, (int) (getSize().getX() - ARROW_NEXT.getWidth()), (int) ((getSize().getY() / 2.0) - (ARROW_BACK.getHeight() / 2.0)));
		add(back, next, home);

		if (maxPages > 1) {
			ComponentText pageStringComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
			pageStringComponent.getUnicode().setValue(false);

			pageStringComponent.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
				String pageString = (page + 1) + "/" + (maxPages + 1);
				pageStringComponent.getText().setValue(pageString);
				pageStringComponent.setPos(new Vec2d((getSize().getX() / 2.0) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(pageString) / 2.0), (int) ((getSize().getY() / 2.0) - (ARROW_NEXT.getHeight() / 2.0)) + 15));
			});
			add(pageStringComponent);
		}

		home.BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {
			home.setSprite(ARROW_HOME_PRESSED);
			home.getColor().setValue(book.mainColor.brighter());
		});
		home.BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {
			home.setSprite(ARROW_HOME);
			home.getColor().setValue(Color.WHITE);
		});
		List<String> homeTooltip = new ArrayList<>();
		homeTooltip.add("Index");
		home.render.getTooltip().setValue(homeTooltip);

		home.BUS.hook(GuiComponentEvents.MouseClickEvent.class, event -> {
			// Make visible the parent of the holder of the nav bar or the main index if shifting
			if (GuiBook.isShiftKeyDown()) {
				book.MAIN_INDEX.setVisible(true);
				book.FOCUSED_COMPONENT = book.MAIN_INDEX;

				// Make the holder of the nav bar invisible
				navBarHolder.setVisible(false);
			} else if (navBarHolder.getLinkingParent() != null) {
				navBarHolder.getLinkingParent().setVisible(true);
				book.FOCUSED_COMPONENT = navBarHolder.getLinkingParent();

				// Make the holder of the nav bar invisible
				navBarHolder.setVisible(false);
			}
		});

		back.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			int x = MathHelper.clamp(page - 1, 0, maxPages);
			if (page == x) back.setVisible(false);
			else back.setVisible(true);

			if (!back.isVisible()) return;

			if (event.component.getMouseOver()) {
				back.setSprite(ARROW_BACK_PRESSED);
				back.getColor().setValue(book.mainColor.brighter());
			} else {
				back.setSprite(ARROW_BACK);
				back.getColor().setValue(Color.WHITE);
			}
		});
		back.BUS.hook(GuiComponentEvents.MouseClickEvent.class, event -> {
			int x = MathHelper.clamp(page - 1, 0, maxPages);
			if (page == x) return;

			page = x;

			EventNavBarChange eventNavBarChange = new EventNavBarChange(page);
			BUS.fire(eventNavBarChange);
		});
		List<String> backTooltip = new ArrayList<>();
		backTooltip.add("Back");
		back.render.getTooltip().setValue(backTooltip);

		next.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			int x = MathHelper.clamp(page + 1, 0, maxPages);
			if (page == x) next.setVisible(false);
			else next.setVisible(true);

			if (!next.isVisible()) return;

			if (event.component.getMouseOver()) {
				next.setSprite(ARROW_NEXT_PRESSED);
				next.getColor().setValue(book.mainColor.brighter());
			} else {
				next.setSprite(ARROW_NEXT);
				next.getColor().setValue(Color.WHITE);
			}
		});
		next.BUS.hook(GuiComponentEvents.MouseClickEvent.class, event -> {
			int x = MathHelper.clamp(page + 1, 0, maxPages);
			if (page == x) return;

			page = x;

			EventNavBarChange eventNavBarChange = new EventNavBarChange(page);
			BUS.fire(eventNavBarChange);
		});
		List<String> nextTooltip = new ArrayList<>();
		nextTooltip.add("Next");
		next.render.getTooltip().setValue(nextTooltip);
	}


	public int getPage() {
		return page;
	}
}
