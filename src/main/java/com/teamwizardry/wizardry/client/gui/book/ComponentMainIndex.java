package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static com.teamwizardry.wizardry.client.gui.book.GuiBook.BANNER;

public class ComponentMainIndex extends NavBarHolder {

	public ComponentMainIndex(@Nonnull GuiBook book) {
		super(0, 0, book.bookComponent.getSize().getXi(), book.bookComponent.getSize().getYi() - 16, book);

		// --------- BANNER --------- //
		{
			ComponentSprite componentBanner = new ComponentSprite(BANNER, -8, 12);
			componentBanner.getColor().setValue(book.mainColor);
			add(componentBanner);

			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			ComponentText componentBannerText = new ComponentText(20, 5, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			componentBannerText.getText().setValue(I18n.format(book.book.headerKey));
			componentBannerText.getColor().setValue(book.highlightColor);

			String subText = I18n.format(book.book.subtitleKey);
			ComponentText componentBannerSubText = new ComponentText(componentBanner.getSize().getXi() - 10, 2 + fontRenderer.FONT_HEIGHT, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.TOP);
			componentBannerSubText.getText().setValue(subText);
			componentBannerSubText.getUnicode().setValue(true);
			componentBannerSubText.getColor().setValue(book.highlightColor);

			componentBanner.add(componentBannerText, componentBannerSubText);
		}
		// --------- BANNER --------- //

		// --------- MAIN INDEX --------- //
		{

			ArrayList<GuiComponent> categories = new ArrayList<>();
			EntityPlayer player = Minecraft.getMinecraft().player;
			for (Category category : book.book.categories) {
				if (category.anyUnlocked(player)) {
					ComponentCategoryButton component = new ComponentCategoryButton(0, 0, 24, 24, book, category);
					add(component);
					categories.add(component);
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
}
