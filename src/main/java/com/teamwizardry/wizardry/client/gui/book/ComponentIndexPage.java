package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.getJsonFromLink;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentIndexPage extends BookGuiComponent {

	private final HashMap<Integer, GuiComponent> pages = new HashMap<>();
	private final JsonObject indexObject;
	private String description;
	private String title;
	@Nullable
	private String icon;

	private GuiComponent currentActive;
	private ComponentNavBar navBar;

	public ComponentIndexPage(GuiBook book, BookGuiComponent parent, JsonObject indexObject) {
		super(16, 16, book.COMPONENT_BOOK.getSize().getXi() - 32, book.COMPONENT_BOOK.getSize().getYi() - 32, book, parent);
		this.indexObject = indexObject;

		if (indexObject.has("title") && indexObject.get("title").isJsonPrimitive()
				&& indexObject.has("icon") && indexObject.get("icon").isJsonPrimitive()
				&& indexObject.has("description") && indexObject.get("description").isJsonPrimitive()
				&& indexObject.has("type") && indexObject.get("type").isJsonPrimitive()
				&& indexObject.has("content") && indexObject.get("content").isJsonArray()) {

			String type = indexObject.getAsJsonPrimitive("type").getAsString();
			if (!type.equals("index")) return;

			String icon = indexObject.getAsJsonPrimitive("icon").getAsString();
			String title = indexObject.getAsJsonPrimitive("title").getAsString();
			String description = indexObject.getAsJsonPrimitive("description").getAsString();
			JsonArray contentArray = indexObject.getAsJsonArray("content");

			this.title = title;
			this.description = description;
			this.icon = icon;

			ComponentVoid pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
			add(pageComponent);
			currentActive = pageComponent;

			int itemsPerPage = 9;
			int page = 0;
			int count = 0;
			for (JsonElement element : contentArray) {
				if (!element.isJsonPrimitive()) continue;

				String contentLink = element.getAsJsonPrimitive().getAsString();

				JsonElement contentElement = getJsonFromLink(contentLink);
				if (contentElement == null || !contentElement.isJsonObject()) continue;

				JsonObject contentObject = contentElement.getAsJsonObject();
				if (indexObject.has("title") && indexObject.get("title").isJsonPrimitive()
						&& indexObject.has("icon") && indexObject.get("icon").isJsonPrimitive()
						&& indexObject.has("description") && indexObject.get("description").isJsonPrimitive()
						&& indexObject.has("type") && indexObject.get("type").isJsonPrimitive()
						&& contentObject.has("content") && contentObject.get("content").isJsonArray()) {

					String contentType = contentObject.getAsJsonPrimitive("type").getAsString();

					BookGuiComponent contentComponent = null;
					if (contentType.equals("index")) {
						contentComponent = new ComponentIndexPage(book, this, contentObject);
					} else if (contentType.equals("entry")) {
						contentComponent = new ComponentEntryPage(book, this, contentObject, true);
					}

					if (contentComponent != null) {
						contentComponent.setLinkingParent(this);
						contentComponent.setVisible(false);
						book.COMPONENT_BOOK.add(contentComponent);

						GuiComponent indexButton = contentComponent.createIndexButton(count, book, null);
						pageComponent.add(indexButton);
						indexButton.setVisible(page == 0);

						count++;
						if (count >= itemsPerPage) {
							pages.put(page++, pageComponent);
							pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
							add(pageComponent);
							pageComponent.setVisible(false);
							count = 0;
						}
					}
				}
			}
		}

		navBar = new ComponentNavBar(book, this, (getSize().getXi() / 2) - 35, getSize().getYi() + 16, 70, pages.size());
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, (navBarChange) -> {
			update();
		});
	}

	@Nullable
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Nullable
	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public void update() {
		if (currentActive != null) currentActive.setVisible(false);

		currentActive = pages.get(navBar.getPage());

		if (currentActive != null) currentActive.setVisible(true);
	}

	@Nonnull
	@Override
	public BookGuiComponent clone() {
		return new ComponentIndexPage(getBook(), getLinkingParent(), indexObject);
	}
}
