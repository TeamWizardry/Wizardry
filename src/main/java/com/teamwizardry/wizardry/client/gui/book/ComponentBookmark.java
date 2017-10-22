package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;

import static com.teamwizardry.wizardry.client.gui.book.BookGui.BOOKMARK;
import static com.teamwizardry.wizardry.client.gui.book.BookGui.BOOKMARK_EXTENDED;

public class ComponentBookmark extends GuiComponent<ComponentBookmark> {


	public ComponentBookmark(Vec2d pos, BookGui bookGui, GuiComponent<?> parent, int index, GuiComponent<?> link, String title, boolean isActive) {
		super(pos.getXi(), pos.getYi(), 200, 300);

		if (!isActive) {
			link.setEnabled(false);
			link.setVisible(false);
		} else {
			bookGui.activeComponent = link;
			link.setEnabled(true);
			link.setVisible(true);
		}
		parent.add(link);

		ComponentSprite bookmark = new ComponentSprite(BOOKMARK, 453, 15 + index * 25, BOOKMARK.getWidth(), BOOKMARK.getHeight());
		add(bookmark);

		ComponentText bookmarkText = new ComponentText((int) (BOOKMARK.getWidth() / 2.0), (int) (BOOKMARK.getHeight() / 2.0), ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.MIDDLE);
		bookmarkText.getScale().setValue(2f);
		bookmarkText.getText().setValue(title);
		bookmark.add(bookmarkText);

		bookmark.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			if (event.getComponent().getMouseOver() || bookGui.activeComponent == null || bookGui.activeComponent.equals(link)) {
				if (bookmark.getSprite() == null || !bookmark.getSprite().equals(BOOKMARK_EXTENDED)) {
					bookmark.setSprite(BOOKMARK_EXTENDED);
					bookmark.setSize(new Vec2d(BOOKMARK_EXTENDED.getWidth(), BOOKMARK_EXTENDED.getHeight()));
					bookmarkText.setPos(new Vec2d(BOOKMARK_EXTENDED.getWidth() / 2.0, BOOKMARK_EXTENDED.getHeight() / 2.0));
				}
			} else {
				if (bookmark.getSprite() == null || !bookmark.getSprite().equals(BOOKMARK)) {
					bookmark.setSprite(BOOKMARK);
					bookmark.setSize(new Vec2d(BOOKMARK.getWidth(), BOOKMARK.getHeight()));
					bookmarkText.setPos(new Vec2d(BOOKMARK.getWidth() / 2.0, BOOKMARK.getHeight() / 2.0));
				}
			}

			if (bookGui.activeComponent != null)
				if (!bookGui.activeComponent.equals(link) && (link.isVisible() || link.getEnabled())) {
					link.setEnabled(false);
					link.setVisible(false);
				}
		});

		bookmark.BUS.hook(GuiComponent.MouseClickEvent.class, (event) -> {
			if (event.getComponent().getMouseOver()) {

				if (!bookGui.componentLogo.isInvalid()) bookGui.componentLogo.invalidate();

				if (bookGui.activeComponent == null || !bookGui.activeComponent.equals(link)) {
					bookGui.activeComponent = link;
					link.setEnabled(true);
					link.setVisible(true);
				}
			}
		});
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
