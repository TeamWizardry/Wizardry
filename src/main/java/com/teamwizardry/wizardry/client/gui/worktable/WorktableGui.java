package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.gui.GuiBase;
import com.teamwizardry.librarianlib.gui.components.*;
import com.teamwizardry.librarianlib.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.sprite.Texture;
import com.teamwizardry.librarianlib.util.Color;
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
	private boolean setupMode = true;

	public WorktableGui() {
		super(512, 256);

		for (Map<ResourceLocation, Module> hashMap : ModuleRegistry.getInstance().getModules().values())
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

		int boxHeight = 200, boxWidth = 100;
		Sprite ringTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/ringBase.png"));
		Sprite staffTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/staffGold.png"));

		ComponentRect menu = new ComponentRect(0, 0, guiWidth, guiHeight);
		menu.color.setValue(Color.argb(0x804A4A4A));

		ComponentRect ring = new ComponentRect((guiWidth / 2) - (boxWidth / 2) - 55, (guiHeight / 2) - (boxHeight / 2), boxWidth, boxHeight).setup(componentRingBox -> {

			ComponentSprite sprite = new ComponentSprite(ringTexture, (boxWidth / 2) - (ringTexture.width / 2), (boxHeight / 2) - (ringTexture.height / 2));
			componentRingBox.add(sprite);

			new ButtonMixin(componentRingBox,
					() -> {
						sprite.setSize(new Vec2d(ringTexture.width, ringTexture.height).mul(2));
						sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.width * 2 / 2), (boxHeight / 2) - (ringTexture.height * 2 / 2)));
						componentRingBox.color.setValue(Color.argb(0x804A4A4A));
						componentRingBox.setSize(new Vec2d(boxWidth, boxHeight));
						componentRingBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth / 2) - 55, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						sprite.setSize(new Vec2d(ringTexture.width, ringTexture.height).mul(4));
						sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.width * 4 / 2), (boxHeight / 2) - (ringTexture.height * 4 / 2)));
						componentRingBox.color.setValue(Color.argb(0x809A9A9A));
						componentRingBox.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
						componentRingBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth / 2) - 55 - 10, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						sprite.setSize(new Vec2d(ringTexture.width, ringTexture.height));
						sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.width / 2), (boxHeight / 2) - (ringTexture.height / 2)));
						componentRingBox.color.setValue(Color.argb(0x80222222));
						componentRingBox.setSize(new Vec2d(boxWidth, boxHeight));
						componentRingBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth / 2) - 55, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						components.remove(shapes);
						setupMode = false;
						menu.invalidate();
					});
		});

		ComponentRect staff = new ComponentRect((guiWidth / 2) - (boxWidth / 2) + 55, (guiHeight / 2) - (boxHeight / 2), boxWidth, boxHeight).setup(componentStaffBox -> {

			ComponentSprite sprite = new ComponentSprite(staffTexture, (boxWidth / 2) - (staffTexture.width / 2), (boxHeight / 2) - (staffTexture.height / 2));
			componentStaffBox.add(sprite);

			new ButtonMixin(componentStaffBox,
					() -> {
						sprite.setSize(new Vec2d(staffTexture.width, staffTexture.height).mul(2));
						sprite.setPos(new Vec2d((boxWidth / 2) - (staffTexture.width * 2 / 2), (boxHeight / 2) - (staffTexture.height * 2 / 2)));
						componentStaffBox.color.setValue(Color.argb(0x804A4A4A));
						componentStaffBox.setSize(new Vec2d(boxWidth, boxHeight));
						componentStaffBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth / 2) + 55, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						sprite.setSize(new Vec2d(staffTexture.width, staffTexture.height).mul(4));
						sprite.setPos(new Vec2d((boxWidth / 2) - (staffTexture.width * 4 / 2), (boxHeight / 2) - (staffTexture.height * 4 / 2)));
						componentStaffBox.color.setValue(Color.argb(0x809A9A9A));
						componentStaffBox.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
						componentStaffBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth + 20 / 2) + 55, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						sprite.setSize(new Vec2d(staffTexture.width, staffTexture.height));
						sprite.setPos(new Vec2d((boxWidth / 2) - (staffTexture.width / 2), (boxHeight / 2) - (staffTexture.height / 2)));
						componentStaffBox.color.setValue(Color.argb(0x80222222));
						componentStaffBox.setSize(new Vec2d(boxWidth, boxHeight));
						componentStaffBox.setPos(new Vec2d((guiWidth / 2) - (boxWidth / 2) + 55, (guiHeight / 2) - (boxHeight / 2)));
					},
					() -> {
						components.remove(shapes);
						setupMode = false;
						menu.invalidate();
					});
		});

		menu.add(staff);
		menu.add(ring);

		components.add(menu);
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