package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.sprite.Sprite;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.BOOKMARK;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentBookMark extends ComponentAnimatableVoid {

	private static Set<ComponentBookMark> bookMarks = new HashSet<>();

	private final GuiBook book;
	private final int id;
	private final Sprite box;

	private ComponentSprite bar;

	public ComponentBookMark(GuiBook book, Sprite icon, int id) {
		super(book.COMPONENT_BOOK.getSize().getXi() - 10, 20 + 5 * id + BOOKMARK.getHeight() * id, BOOKMARK.getWidth(), BOOKMARK.getHeight());
		this.book = book;
		this.id = id;

		bookMarks.add(this);

		box = BOOKMARK;

		clipping.setClipToBounds(true);

		animX = -box.getWidth() + 20;

		bar = new ComponentSprite(BOOKMARK, -box.getWidth() + 20, 0);
		bar.getColor().setValue(book.mainColor);
		add(bar);

		ComponentSprite iconComponent = new ComponentSprite(icon, getSize().getXi() - icon.getWidth() - 8, 1);
		bar.add(iconComponent);
	}

	@Nullable
	private static ComponentBookMark getBookMarkFromID(int id) {
		for (ComponentBookMark bookMark : bookMarks) {
			if (bookMark.getId() == id) return bookMark;
		}

		return null;
	}

	public static int getNextId() {
		int largest = 0;
		for (ComponentBookMark bookMark : bookMarks) {
			if (bookMark.getId() > largest) largest = bookMark.getId();
		}

		return ++largest;
	}

	public void slideOutShort() {
		BasicAnimation mouseOutAnim = new BasicAnimation<>(bar, "pos.x");
		mouseOutAnim.setDuration(10);
		mouseOutAnim.setEasing(Easing.easeOutQuart);
		mouseOutAnim.setTo(-40);
		bar.add(mouseOutAnim);
	}

	public void slideOutLong() {
		BasicAnimation mouseOutAnim = new BasicAnimation<>(bar, "pos.x");
		mouseOutAnim.setDuration(10);
		mouseOutAnim.setEasing(Easing.easeOutQuart);
		mouseOutAnim.setTo(0);
		bar.add(mouseOutAnim);
	}

	public void slideIn() {
		BasicAnimation mouseOutAnim = new BasicAnimation<>(bar, "pos.x");
		mouseOutAnim.setDuration(10);
		mouseOutAnim.setEasing(Easing.easeOutQuart);
		mouseOutAnim.setTo(-box.getWidth() + 20);
		bar.add(mouseOutAnim);
	}

	public int getId() {
		return id;
	}

	public GuiBook getBook() {
		return book;
	}
}
