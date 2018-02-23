package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public class ComponentCategoryPage extends NavBarHolder {

	public ComponentCategoryPage(GuiBook book, Category category) {
		super(16, 16, book.bookComponent.getSize().getXi() - 32, book.bookComponent.getSize().getYi() - 32, book);

		ComponentVoid pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
		add(pageComponent);
		currentActive = pageComponent;

		int itemsPerPage = 9;
		int count = 0;
		int id = 0;
		EntityPlayer player = Minecraft.getMinecraft().player;
		for (Entry entry : category.entries) {
			if (entry.isUnlocked(player)) {
				GuiComponent indexPlate = book.createIndexButton(id++, entry, null);
				if (indexPlate == null) continue;
				pageComponent.add(indexPlate);

				count++;
				if (count >= itemsPerPage) {
					addPage(pageComponent);
					pageComponent = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
					add(pageComponent);
					pageComponent.setVisible(false);
					count = 0;
					id = 0;
				}
			}
		}
	}
}
