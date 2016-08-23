package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.*;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
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
		getComponents().add(background);

		paper = new ComponentVoid(160, 0, 191, 202);
		paper.setZIndex(100);
		ComponentVoid c = new ComponentVoid(0, 0, 191, 202);
		paper.add(c);
		c.addTag("tray");
		c = new ComponentVoid(213, 134, 98, 66);
		paper.add(c);
		c.addTag("tray");
		getComponents().add(paper);

		ComponentVoid effects = new ComponentVoid(92, 32, 52, 158);
		addModules(effects, ModuleType.EFFECT, 7, 7, 3, 12);
		getComponents().add(effects);

		ComponentVoid shapes = new ComponentVoid(32, 32, 52, 74);
		addModules(shapes, ModuleType.SHAPE, 7, 7, 3, 5);
		getComponents().add(shapes);

		ComponentVoid booleans = new ComponentVoid(32, 116, 52, 74);
		addModules(booleans, ModuleType.BOOLEAN, 7, 7, 3, 5);
		getComponents().add(booleans);

		ComponentVoid events = new ComponentVoid(368, 31, 52, 87);
		addModules(events, ModuleType.EVENT, 7, 7, 3, 6);
		getComponents().add(events);

		ComponentVoid modifiers = new ComponentVoid(428, 31, 52, 87);
		addModules(modifiers, ModuleType.MODIFIER, 7, 7, 3, 6);
		
		getComponents().add(modifiers);
		
		int boxHeight = 200, boxWidth = 100;
		Sprite ringTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/ringBase.png"));
		Sprite staffTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/staffGold.png"));

		ComponentRect menu = new ComponentRect(0, 0, getGuiWidth(), getGuiHeight());
		menu.getColor().setValue(new Color(0x804A4A4A));
		

		ComponentRect ring = new ComponentRect((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2) - (boxHeight / 2), boxWidth, boxHeight);

		ComponentSprite sprite = new ComponentSprite(ringTexture, (boxWidth / 2) - (ringTexture.getWidth() / 2), (boxHeight / 2) - (ringTexture.getHeight() / 2));
		ring.add(sprite);

		new ButtonMixin<>(ring, () -> {});
		
		ring.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch(event.getNewState()) {
				case NORMAL:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()).mul(2));
					sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.getWidth() * 2 / 2), (boxHeight / 2) - (ringTexture.getHeight() * 2 / 2)));
					ring.getColor().setValue(new Color(0x804A4A4A));
					ring.setSize(new Vec2d(boxWidth, boxHeight));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2) - (boxHeight / 2)));
					break;
				case HOVER:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()).mul(4));
					sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.getWidth() * 4 / 2), (boxHeight / 2) - (ringTexture.getHeight() * 4 / 2)));
					ring.getColor().setValue(new Color(0x809A9A9A));
					ring.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55 - 10, (getGuiHeight() / 2) - (boxHeight / 2)));
					break;
				case DISABLED:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()));
					sprite.setPos(new Vec2d((boxWidth / 2) - (ringTexture.getWidth() / 2), (boxHeight / 2) - (ringTexture.getHeight() / 2)));
					ring.getColor().setValue(new Color(0x80222222));
					ring.setSize(new Vec2d(boxWidth, boxHeight));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2) - (boxHeight / 2)));
					break;
			}
		});
		ring.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
			getComponents().remove(shapes);
			setupMode = false;
			menu.invalidate();
		});
		

		ComponentRect staff = new ComponentRect((getGuiWidth() / 2) - (boxWidth / 2) + 55, (getGuiHeight() / 2) - (boxHeight / 2), boxWidth, boxHeight);

		ComponentSprite sprite2 = new ComponentSprite(staffTexture, (boxWidth / 2) - (staffTexture.getWidth() / 2), (boxHeight / 2) - (staffTexture.getHeight() / 2));
		staff.add(sprite2);

		new ButtonMixin(staff, () -> {});
		
		staff.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch(event.getNewState()) {
				case NORMAL:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()).mul(2));
					sprite2.setPos(new Vec2d((boxWidth / 2) - (staffTexture.getWidth() * 2 / 2), (boxHeight / 2) - (staffTexture.getHeight() * 2 / 2)));
					staff.getColor().setValue(new Color(0x804A4A4A));
					staff.setSize(new Vec2d(boxWidth, boxHeight));
					staff.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) + 55, (getGuiHeight() / 2) - (boxHeight / 2)));
					break;
				case HOVER:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()).mul(4));
					sprite2.setPos(new Vec2d((boxWidth / 2) - (staffTexture.getWidth() * 4 / 2), (boxHeight / 2) - (staffTexture.getHeight() * 4 / 2)));
					staff.getColor().setValue(new Color(0x809A9A9A));
					staff.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
					staff.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth + 20 / 2) + 55, (getGuiHeight() / 2) - (boxHeight / 2)));
					break;
				case DISABLED:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()));
					sprite2.setPos(new Vec2d((boxWidth / 2) - (staffTexture.getWidth() / 2), (boxHeight / 2) - (staffTexture.getHeight() / 2)));
					staff.getColor().setValue(new Color(0x80222222));
					staff.setSize(new Vec2d(boxWidth, boxHeight));
					staff.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) + 55, (getGuiHeight() / 2) - (boxHeight / 2)));
			}
		});
		
		staff.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
			getComponents().remove(shapes);
			setupMode = false;
			menu.invalidate();
		});
		
		menu.add(staff);
		menu.add(ring);
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
			if(count == 0) {
				item.get().BUS.hook(GuiComponent.MouseOverEvent.class, (event) -> {
					if(event.isOver())
						event.setOver(true);
				});
			}
			count++;
		}
		int usedRows = (int) Math.ceil(count / (float) columns);
		if (usedRows > rows) {
			ComponentSpriteCapped scrollSlot = new ComponentSpriteCapped(SCROLL_GROOVE_VERTICAL_TOP, SCROLL_GROOVE_VERTICAL_MIDDLE, SCROLL_GROOVE_VERTICAL_BOTTOM, false, x + columns * 12, y, 12, rows * 12);
			parent.add(scrollSlot);

			ComponentSlider scrollSlider = new ComponentSlider(6, SCROLL_SLIDER_VERTICAL.getHeight() / 2 + 2, 0, rows * 12 - SCROLL_SLIDER_VERTICAL.getHeight() - 4, 0, usedRows - 3);
			scrollSlider.getHandle().add(new ComponentSprite(SCROLL_SLIDER_VERTICAL, -SCROLL_SLIDER_VERTICAL.getWidth() / 2, -SCROLL_SLIDER_VERTICAL.getHeight() / 2));
			scrollSlider.getPercentageChange().add((p) -> view.scrollToPercent(new Vec2d(0, p)));
			scrollSlot.add(scrollSlider);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}