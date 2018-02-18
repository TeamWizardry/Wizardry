package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
public abstract class BookGuiComponent extends GuiComponent {

	@Nonnull
	private final GuiBook book;
	@Nullable
	private BookGuiComponent parent;

	public BookGuiComponent(int posX, int posY, int width, int height, @Nonnull GuiBook book, @Nullable BookGuiComponent parent) {
		super(posX, posY, width, height);
		this.book = book;
		this.parent = parent;
	}

	public abstract String getTitle();

	public abstract String getDescription();

	@Nonnull
	public GuiBook getBook() {
		return book;
	}

	@Nullable
	public abstract String getIcon();

	@Nullable
	public BookGuiComponent getLinkingParent() {
		return parent == null ? book.MAIN_INDEX : parent;
	}

	public void setLinkingParent(@Nonnull BookGuiComponent component) {
		this.parent = component;
	}

	public abstract void update();

	@Nonnull
	public abstract BookGuiComponent clone();

	public GuiComponent createIndexButton(int indexID, GuiBook book, @Nullable Consumer<GuiComponent> extra) {
		ComponentVoid indexButton = new ComponentVoid(0, 16 * indexID, getSize().getXi(), 16);

		if (extra != null) extra.accept(indexButton);

		indexButton.BUS.hook(GuiComponentEvents.MouseClickEvent.class, event -> {
			book.FOCUSED_COMPONENT.setVisible(false);
			book.FOCUSED_COMPONENT = this;
			book.FOCUSED_COMPONENT.setVisible(true);
			update();
		});

		// SUB INDEX PLATE RENDERING
		{
			ComponentText textComponent = new ComponentText(20, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			textComponent.getUnicode().setValue(true);
			textComponent.getText().setValue(getTitle());
			indexButton.add(textComponent);

			indexButton.BUS.hook(GuiComponentEvents.MouseInEvent.class, (event) -> {
				textComponent.getText().setValue(" " + TextFormatting.ITALIC.toString() + getTitle());
			});

			indexButton.BUS.hook(GuiComponentEvents.MouseOutEvent.class, (event) -> {
				textComponent.getText().setValue(TextFormatting.RESET.toString() + getTitle());
			});

			if (getIcon() == null) return indexButton;

			Sprite iconSprite = null;
			ItemStack stackIcon = ItemStack.EMPTY;
			{
				ResourceLocation iconLocation = new ResourceLocation(getIcon());
				if (ForgeRegistries.ITEMS.containsKey(iconLocation)) {
					Item itemIcon = ForgeRegistries.ITEMS.getValue(iconLocation);
					if (itemIcon != null) stackIcon = new ItemStack(itemIcon);
				} else iconSprite = new Sprite(iconLocation);
			}

			Sprite finalIconSprite = iconSprite;
			ItemStack finalStackIcon = stackIcon;
			indexButton.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (event) -> {
				if (finalIconSprite != null) {

					GlStateManager.pushMatrix();
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.enableBlend();

					finalIconSprite.getTex().bind();
					finalIconSprite.draw((int) ClientTickHandler.getPartialTicks(), 0, 0, 16, 16);

					GlStateManager.popMatrix();

				} else if (finalStackIcon != null && !finalStackIcon.isEmpty()) {

					GlStateManager.pushMatrix();
					GlStateManager.enableBlend();
					GlStateManager.enableRescaleNormal();
					RenderHelper.enableGUIStandardItemLighting();

					RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
					itemRender.renderItemAndEffectIntoGUI(finalStackIcon, 0, 0);
					itemRender.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, finalStackIcon, 0, 0);

					GlStateManager.enableAlpha();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
				}
			});
		}

		return indexButton;
	}

}
