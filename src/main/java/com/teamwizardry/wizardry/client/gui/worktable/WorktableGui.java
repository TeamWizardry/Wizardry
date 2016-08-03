package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.gui.GuiBase;
import com.teamwizardry.librarianlib.gui.components.*;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

import static com.teamwizardry.wizardry.lib.LibSprites.Worktable.*;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableGui extends GuiBase {
	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table_background.png"));
	public static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 512, 256);

	public static final Texture SPRITE_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"));

	static final int iconSize = 12;
	public Multimap<ModuleType, Module> modulesByType = HashMultimap.create();
	private ComponentVoid paper;

	public WorktableGui() {
		super(512, 256);

		for (Map<Integer, Module> hashMap : ModuleRegistry.getInstance().getModules().values())
			for (Module module : hashMap.values())
				modulesByType.get(module.getType()).add(module);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE, 0, 0);
		components.add(background);

		paper = new ComponentVoid(160, 0, 191, 202);
		paper.zIndex = 100;
		paper.add(new ComponentVoid(0, 0, 191, 202).setup((c) -> c.addTag("tray")));
		paper.add(new ComponentVoid(213, 134, 98, 66).setup((c) -> c.addTag("tray")));
		components.add(paper);

		ComponentVoid effects = new ComponentVoid(92, 32, 52, 158);
		addModules(effects, ModuleType.EFFECT, 7, 7, 3, 12);
		components.add(effects);

		ComponentVoid shapes = new ComponentVoid(32, 32, 52, 74);
		addModules(shapes, ModuleType.SHAPE, 7, 7, 3, 5);
		components.add(shapes);

		ComponentVoid booleans = new ComponentVoid(32, 116, 52, 74);
		addModules(booleans, ModuleType.BOOLEAN, 7, 7, 3, 5);
		components.add(booleans);

		ComponentVoid events = new ComponentVoid(368, 31, 52, 87);
		addModules(events, ModuleType.EVENT, 7, 7, 3, 6);
		components.add(events);

		ComponentVoid modifiers = new ComponentVoid(428, 31, 52, 87);
		addModules(modifiers, ModuleType.MODIFIER, 7, 7, 3, 6);
		components.add(modifiers);
	}

	private void addModules(ComponentVoid parent, ModuleType type, int x, int y, int columns, int rows) {
		ComponentScrolledView view = new ComponentScrolledView(x, y, columns * 12, rows * 12);
		parent.add(view);

		ComponentGrid grid = new ComponentGrid(0, 0, 12, 12, columns);
		view.add(grid);

		int count = 0;
		for (Module constructor : modulesByType.get(type)) {
			SidebarItem item = new SidebarItem(0, 0, constructor, paper);
			grid.add(item.get());
			count++;
		}
		int usedRows = (int) Math.ceil(count / (float) columns);
		if (usedRows > rows) {
			ComponentSpriteCapped scrollSlot = new ComponentSpriteCapped(SCROLL_GROOVE_VERTICAL_TOP, SCROLL_GROOVE_VERTICAL_MIDDLE, SCROLL_GROOVE_VERTICAL_BOTTOM, false, x + columns * 12, y, 12, rows * 12);
			parent.add(scrollSlot);

			ComponentSlider scrollSlider = new ComponentSlider(6, SCROLL_SLIDER_VERTICAL.height / 2 + 2, 0, rows * 12 - SCROLL_SLIDER_VERTICAL.height - 4, 0, usedRows - 3);
			scrollSlider.handle.add(new ComponentSprite(SCROLL_SLIDER_VERTICAL, -SCROLL_SLIDER_VERTICAL.width / 2, -SCROLL_SLIDER_VERTICAL.height / 2));
			scrollSlider.percentageChange.add((p) -> view.scrollToPercent(new Vec2d(0, p)));
			scrollSlot.add(scrollSlider);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}