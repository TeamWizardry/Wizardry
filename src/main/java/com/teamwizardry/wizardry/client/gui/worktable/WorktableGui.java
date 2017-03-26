package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentGrid;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableGui extends GuiBase {
	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table_background.png"));
	public static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 512, 256);

	public static final Texture SPRITE_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"));

	static final int iconSize = 12;

	public HashMultimap<GuiComponent<?>, GuiComponent<?>> connections = HashMultimap.create();
	public ComponentModuleLine movingLine;
	public GuiComponent<?> paper;

	public WorktableGui() {
		super(512, 256);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE, 0, 0);
		getMainComponents().add(background);

		paper = new ComponentVoid(160, 0, 191, 202);
		getMainComponents().add(paper);

		getMainComponents().add(movingLine);

		ComponentVoid effects = new ComponentVoid(92, 32, 52, 158);
		addModules(effects, ModuleType.EFFECT, 7, 7, 3, 12);
		getMainComponents().add(effects);

		ComponentVoid shapes = new ComponentVoid(32, 32, 52, 74);
		addModules(shapes, ModuleType.SHAPE, 7, 7, 3, 5);
		getMainComponents().add(shapes);

		ComponentVoid booleans = new ComponentVoid(32, 116, 52, 74);
		addModules(booleans, ModuleType.BOOLEAN, 7, 7, 3, 5);
		getMainComponents().add(booleans);

		ComponentVoid events = new ComponentVoid(368, 31, 52, 87);
		addModules(events, ModuleType.EVENT, 7, 7, 3, 6);
		getMainComponents().add(events);

		ComponentVoid modifiers = new ComponentVoid(428, 31, 52, 87);
		addModules(modifiers, ModuleType.MODIFIER, 7, 7, 3, 6);
		getMainComponents().add(modifiers);

		ComponentVoid selected = new ComponentVoid(0, 0, 12, 12);
		selected.addTag("selected");
		paper.add(selected);

		int boxHeight = 200, boxWidth = 100;
		Sprite ringTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/ring_base.png"));
		Sprite staffTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/staff.png"));
	}

	private void addModules(ComponentVoid parent, ModuleType type, int x, int y, int columns, int rows) {
		ComponentGrid grid = new ComponentGrid(x, y, 12, 12, columns);
		parent.add(grid);

		for (Module module : ModuleRegistry.INSTANCE.getModules(type)) {
			TableModule item = new TableModule(this, module, false);
			grid.add(item.component);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
