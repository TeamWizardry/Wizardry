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
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.*;
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

	private boolean hadBook = false, bookWarnRevised = false;

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
		toast.getTransform().setScale(0.5f);
		toast.getWrap().setValue(160);
		tableComponent.add(toast);

		setCodexToastMessage();

		toast.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			if (!bookWarnRevised && !hadBook && Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
				toast.getColor().setValue(Color.GREEN);
				toast.getText().setValue("Codex found, you can save spells.");
				bookWarnRevised = true;
			}
		});

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
			if (!event.component.getMouseOver()) return;
			save.setSprite(BUTTON_PRESSED);
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

	public void setCodexToastMessage() {
		if (!Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(ModItems.BOOK))) {
			hadBook = false;
			toast.getColor().setValue(Color.RED);
			toast.getText().setValue("You do not have a codex in your inventory! Once you have one, you can save your spell into the book.");
		} else {
			hadBook = true;
			toast.getColor().setValue(Color.GREEN);
			toast.getText().setValue("Codex found, you can save spells.");
		}
	}

	public String getToastHeader() {
		return TextFormatting.YELLOW + getSpellName() + TextFormatting.RESET + "\n\n";
	}

	public String getSpellName() {
		StringBuilder builder = new StringBuilder();
		Deque<TableModule> heads = new ArrayDeque<>(getSpellHeads());

		while (!heads.isEmpty()) {

			TableModule lastModule = heads.pop();

			while (lastModule != null) {
				builder.append(lastModule.getModule().getReadableName());
				lastModule = lastModule.getLinksTo();

				if (lastModule != null)
					builder.append(" > ");
			}

			if (!heads.isEmpty()) {
				builder.append(" | ");
			}
		}
		return builder.toString();
	}

	public Pair<String, Color> getToastMessage() {
		String text = toast.getText().getValue(toast);
		Color color = toast.getColor().getValue(toast);
		return new Pair<>(text, color);
	}

	public void setToastMessage(String text, Color color) {
		toast.getText().setValue(getToastHeader() + text);
		toast.getColor().setValue(color);
	}

	private Set<TableModule> getSpellHeads() {
		Set<TableModule> set = new HashSet<>();

		for (GuiComponent child : paper.getChildren()) {
			if (!(child instanceof TableModule)) continue;
			TableModule childModule = (TableModule) child;

			if (childModule.getLinksTo() != null) {
				boolean linkedToSomehow = false;
				for (GuiComponent subChild : paper.getChildren()) {
					if (!(subChild instanceof TableModule)) continue;
					TableModule subChildModule = (TableModule) subChild;
					if (subChildModule == childModule) continue;

					if (subChildModule.getLinksTo() == childModule) {
						linkedToSomehow = true;
						break;
					}
				}
				if (!linkedToSomehow) {
					set.add(childModule);
				}
			}
		}

		return set;
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
