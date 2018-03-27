package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.*;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Demoniaque on 6/17/2016.
 */
public class WorktableGui extends GuiBase {

	static final Sprite TABLE_SPRITE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table.png"));
	static final Sprite SIDE_BAR_SHORT = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar.png"));
	static final Sprite SIDE_BAR_LONG = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sidebar_long.png"));
	static final Sprite BUTTON_NORMAL = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button.png"));
	static final Sprite BUTTON_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_highlighted.png"));
	static final Sprite BUTTON_PRESSED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/button_pressed.png"));
	static final Sprite PLATE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));
	static final Sprite PLATE_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate_highlighted.png"));
	static final Sprite STREAK = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/streak.png"));

	final ComponentVoid paper;
	final ComponentText toast;

	public WorktableGui(BlockPos worktablePos) {
		super(480, 224);

		// GRAY OUT BACKGROUND
		ComponentRect rect = new ComponentRect(0, 0, 40000, 40000);
		rect.getColor().setValue(new Color(0x80000000, true));
		rect.getTransform().setTranslateZ(-20);
		getFullscreenComponents().add(rect);

		// TABLE
		ComponentSprite tableComponent = new ComponentSprite(TABLE_SPRITE, 0, 0, 480, 224);
		getMainComponents().add(tableComponent);

		// PAPER PLATE
		paper = new ComponentVoid(181, 22, 180, 184);
		tableComponent.add(paper);

		// PAPER PLATE
		toast = new ComponentText(384, 139, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		toast.setSize(new Vec2d(80, 69));
		tableComponent.add(toast);

		ComponentSprite shapes = new ComponentSprite(SIDE_BAR_SHORT, 29, 31, 48, 80);
		addModules(shapes, ModuleType.SHAPE);
		tableComponent.add(shapes);

		ComponentSprite effects = new ComponentSprite(SIDE_BAR_LONG, 93, 37, 48, 160);
		addModules(effects, ModuleType.EFFECT);
		tableComponent.add(effects);

		ComponentSprite events = new ComponentSprite(SIDE_BAR_SHORT, 29, 123, 48, 80);
		addModules(events, ModuleType.EVENT);
		tableComponent.add(events);

		ComponentSprite save = new ComponentSprite(BUTTON_NORMAL, 395, 30, (int) (88 / 1.5), (int) (24 / 1.5));
		String saveStr = LibrarianLib.PROXY.translate("wizardry.misc.save");
		int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(saveStr);
		int height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		ComponentText textSave = new ComponentText((save.getSize().getXi() / 2) - width / 2, (save.getSize().getYi() / 2) - height / 2);
		textSave.getText().setValue(saveStr);
		save.add(textSave);

		save.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
			List<String> txt = new ArrayList<>();

			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				txt.add(TextFormatting.RED + LibrarianLib.PROXY.translate("wizardry.misc.save_error"));
			}
			return txt;
		});

		save.BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else save.setSprite(BUTTON_HIGHLIGHTED);
		});
		save.BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else save.setSprite(BUTTON_NORMAL);
		});
		save.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else if (event.component.getMouseOver()) {
				save.setSprite(BUTTON_PRESSED);
			}
		});
		save.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				save.setSprite(BUTTON_PRESSED);
			} else if (event.component.getMouseOver()) {
				save.setSprite(BUTTON_HIGHLIGHTED);
			}
		});

		save.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {

			if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) return;

		});
		getMainComponents().add(save);

		//whitelistedModifiers = new ComponentWhitelistedModifiers(this, 384, save.getPos().getYi() + save.getSize().getYi() + 8, 80, 170);
		//whitelistedModifiers.getTransform().setTranslateZ(20);
		//getMainComponents().add(whitelistedModifiers);
	}

	private void addModules(ComponentSprite parent, ModuleType type) {
		ComponentGrid grid = new ComponentGrid(0, 0, 16, 16, 3);
		parent.add(grid);

		for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
			com.teamwizardry.wizardry.client.gui.worktable2.TableModule tableModule = new com.teamwizardry.wizardry.client.gui.worktable2.TableModule(this, module, false);
			grid.add(tableModule);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
