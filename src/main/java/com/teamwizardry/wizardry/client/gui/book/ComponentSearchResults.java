package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ComponentSearchResults extends BookGuiComponent {

	private final HashMap<Integer, GuiComponent> pages = new HashMap<>();
	private final int margin = 16;
	private GuiComponent currentActive;
	private ComponentNavBar navBar = null;
	private ComponentText pageHeader;
	private ComponentVoid resultSection;

	public ComponentSearchResults(GuiBook book, BookGuiComponent parent) {
		super(16, 16, book.COMPONENT_BOOK.getSize().getXi() - 32, book.COMPONENT_BOOK.getSize().getYi() - 32, book, parent);

		pageHeader = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		pageHeader.getText().setValue("No results found!");
		pageHeader.getUnicode().setValue(true);
		pageHeader.getWrap().setValue(getSize().getXi());
		add(pageHeader);

		resultSection = new ComponentVoid(0, margin, getSize().getXi(), getSize().getYi() - margin);
		add(resultSection);
	}

	public void updateTfidfSearches(List<GuiBook.TfidfSearchResult> results) {
		reset();

		pageHeader.getText().setValue("Found " + results.size() + " results!");

		Collections.sort(results);

		ComponentVoid pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
		resultSection.add(pageComponent);

		double largestTFIDF = 0, smallestTFIDF = Integer.MAX_VALUE;
		for (GuiBook.TfidfSearchResult resultItem2 : results) {
			largestTFIDF = resultItem2.getTfidfrequency() > largestTFIDF ? resultItem2.getTfidfrequency() : largestTFIDF;
			smallestTFIDF = resultItem2.getTfidfrequency() < smallestTFIDF ? resultItem2.getTfidfrequency() : smallestTFIDF;
		}

		int itemsPerPage = 8;
		int page = 0;
		int count = 0;
		for (GuiBook.TfidfSearchResult resultItem : results) {

			double matchPercentage = results.size() == 1 ? 100 : Math.round((resultItem.getTfidfrequency() - smallestTFIDF) / (largestTFIDF - smallestTFIDF) * 100);
			if (matchPercentage <= 0) continue;

			BookGuiComponent resultComponent = resultItem.getResultComponent().clone();
			resultComponent.setLinkingParent(this);

			ComponentText textComponent = new ComponentText(25, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);

			GuiComponent indexButton = resultComponent.createIndexButton(count, getBook(), plate -> plate.add(textComponent));
			pageComponent.add(indexButton);

			// --------- HANDLE EXTRA TEXT COMPONENT --------- //
			{

				final TextFormatting color;
				if (matchPercentage > 25) {
					if (matchPercentage <= 50) {
						color = TextFormatting.YELLOW;
					} else if (matchPercentage <= 75) {
						color = TextFormatting.GREEN;
					} else if (matchPercentage <= 100) {
						color = TextFormatting.DARK_GREEN;
					} else color = TextFormatting.DARK_RED;
				} else color = TextFormatting.DARK_RED;


				textComponent.getUnicode().setValue(true);
				textComponent.getText().setValue("| " + color + matchPercentage + "% match");

				double finalMatchPercentage = Math.round(matchPercentage);
				indexButton.BUS.hook(GuiComponentEvents.MouseInEvent.class, (event) -> {
					textComponent.getText().setValue("  | " + color + TextFormatting.ITALIC + finalMatchPercentage + "% match");
				});

				indexButton.BUS.hook(GuiComponentEvents.MouseOutEvent.class, (event) -> {
					textComponent.getText().setValue("| " + color + finalMatchPercentage + "% match");
				});
			}
			// --------- HANDLE EXTRA TEXT COMPONENT --------- //

			count++;
			if (count >= itemsPerPage) {
				pages.put(page++, pageComponent);
				pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
				resultSection.add(pageComponent);
				pageComponent.setVisible(false);
				count = 0;
			}
		}

		navBar = new ComponentNavBar(getBook(), this, (getSize().getXi() / 2) - 35, getSize().getYi() + 16, 70, pages.size());
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, (navBarChange) -> {
			update();
		});
	}

	public void updateMatchCountSearches(List<GuiBook.MatchCountSearchResult> results) {
		reset();

		pageHeader.getText().setValue("Search too broad! Found " + results.size() + " results with your keywords");

		Collections.sort(results);

		ComponentVoid pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
		resultSection.add(pageComponent);

		int itemsPerPage = 8;
		int page = 0;
		int count = 0;
		for (GuiBook.MatchCountSearchResult resultItem : results) {

			BookGuiComponent resultComponent = resultItem.getResultComponent().clone();
			resultComponent.setLinkingParent(this);

			ComponentText textComponent = new ComponentText(25, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);

			GuiComponent indexButton = resultComponent.createIndexButton(count, getBook(), plate -> plate.add(textComponent));
			pageComponent.add(indexButton);

			// --------- HANDLE EXTRA TEXT COMPONENT --------- //
			{

				textComponent.getUnicode().setValue(true);
				textComponent.getText().setValue("| " + resultItem.getMatchCount() + " matched keywords");

				indexButton.BUS.hook(GuiComponentEvents.MouseInEvent.class, (event) -> {
					textComponent.getText().setValue("  | " + TextFormatting.ITALIC.toString() + resultItem.getMatchCount() + " matched keywords");
				});

				indexButton.BUS.hook(GuiComponentEvents.MouseOutEvent.class, (event) -> {
					textComponent.getText().setValue("| " + TextFormatting.RESET.toString() + resultItem.getMatchCount() + " matched keywords");
				});
			}
			// --------- HANDLE EXTRA TEXT COMPONENT --------- //

			count++;
			if (count >= itemsPerPage) {
				pages.put(page++, pageComponent);
				pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
				resultSection.add(pageComponent);
				pageComponent.setVisible(false);
				count = 0;
			}

		}

		navBar = new ComponentNavBar(getBook(), this, (getSize().getXi() / 2) - 35, getSize().getYi() + 16, 70, pages.size());
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, (navBarChange) -> {
			update();
		});
	}

	public void setAsBadSearch() {
		reset();
		pageHeader.getText().setValue("No results found!");

		navBar = new ComponentNavBar(getBook(), this, (getSize().getXi() / 2) - 35, getSize().getYi() + 16, 70, pages.size());
		add(navBar);

		navBar.BUS.hook(EventNavBarChange.class, (navBarChange) -> {
			update();
		});
	}

	private void reset() {
		resultSection.invalidate();
		resultSection = new ComponentVoid(0, margin, getSize().getXi(), getSize().getYi() - margin);
		add(resultSection);
		pages.clear();
		if (navBar != null) navBar.invalidate();
	}

	@Override
	public String getTitle() {
		return "Search Results";
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
		if (currentActive != null) currentActive.setVisible(false);

		currentActive = pages.get(navBar.getPage());

		if (currentActive != null) currentActive.setVisible(true);
	}

	@Nonnull
	@Override
	public BookGuiComponent clone() {
		return new ComponentSearchResults(getBook(), getLinkingParent());
	}
}
