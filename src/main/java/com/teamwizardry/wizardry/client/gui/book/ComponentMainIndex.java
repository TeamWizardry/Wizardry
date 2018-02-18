package com.teamwizardry.wizardry.client.gui.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.BANNER;
import static com.teamwizardry.wizardry.client.gui.book.GuiBook.getJsonFromLink;

public class ComponentMainIndex extends BookGuiComponent {

	private final String location;

	public ComponentMainIndex(int posX, int posY, String location, int width, int height, @Nonnull GuiBook book, @Nullable BookGuiComponent parent) {
		super(posX, posY, width, height, book, parent);
		this.location = location;

		// --------- BANNER --------- //
		{
			ComponentSprite componentBanner = new ComponentSprite(BANNER, -8, 12);
			componentBanner.getColor().setValue(book.mainColor);
			add(componentBanner);

			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			ComponentText componentBannerText = new ComponentText(20, 5, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			componentBannerText.getText().setValue("Lexica Demoniaqa");
			componentBannerText.getColor().setValue(book.highlightColor);

			String subText = "- By Demoniaque";
			ComponentText componentBannerSubText = new ComponentText(componentBanner.getSize().getXi() - 10, 2 + fontRenderer.FONT_HEIGHT, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP);
			componentBannerSubText.getText().setValue(subText);
			componentBannerSubText.getUnicode().setValue(true);
			componentBannerSubText.getColor().setValue(book.highlightColor);

			componentBanner.add(componentBannerText, componentBannerSubText);
		}
		// --------- BANNER --------- //

		// --------- SEARCH BAR --------- //
		{
			ComponentSearchResults searchResultsComponent = new ComponentSearchResults(book, book.FOCUSED_COMPONENT);
			searchResultsComponent.setVisible(false);
			book.COMPONENT_BOOK.add(searchResultsComponent);

			ComponentSearchBar bar = new ComponentSearchBar(book, 0, book.defaultSearchImpl(searchResultsComponent), null);
			book.COMPONENT_BOOK.add(bar);
		}
		// --------- SEARCH BAR --------- //

		// --------- MAIN INDEX --------- //
		{

			ArrayList<GuiComponent> categories = new ArrayList<>();

			JsonElement json = getJsonFromLink(location);
			if (json != null && json.isJsonArray()) {

				for (JsonElement element : json.getAsJsonArray()) {
					if (!element.isJsonPrimitive()) continue;

					JsonElement indexElement = getJsonFromLink(element.getAsJsonPrimitive().getAsString());
					if (indexElement == null || !indexElement.isJsonObject()) continue;

					JsonObject cateogryObject = indexElement.getAsJsonObject();

					ComponentCategory categoryComponent = new ComponentCategory(0, 0, 24, 24, book, cateogryObject);
					add(categoryComponent);
					categories.add(categoryComponent);
				}
			}

			int row = 0;
			int column = 0;
			int buffer = 8;
			int marginX = 28;
			int marginY = 45;
			int itemsPerRow = 3;
			for (GuiComponent button : categories) {

				button.setPos(new Vec2d(
						marginX + (column * button.getSize().getXi()) + (column * buffer),
						marginY + (row * button.getSize().getY()) + (row * buffer)));

				column++;

				if (column >= itemsPerRow) {
					row++;
					column = 0;
				}
			}
		}
		// --------- MAIN INDEX --------- //
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Nullable
	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public void update() {

	}

	@Nonnull
	@Override
	public BookGuiComponent clone() {
		return new ComponentMainIndex(getPos().getXi(), getPos().getYi(), location, getSize().getXi(), getSize().getYi(), getBook(), getLinkingParent());
	}
}
