package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
    public Multimap<ModuleType, Module> modulesByType = HashMultimap.create();
    public GuiComponent<?> paper;
    public GuiComponent<?> selected;

    public WorktableGui() {
        super(512, 256);

        for (Module module : ModuleRegistry.INSTANCE.modules)
            modulesByType.get(module.getModuleType()).add(module);

        ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE, 0, 0);
        getMainComponents().add(background);

        paper = new ComponentVoid(160, 0, 191, 202);
        paper.setZIndex(100);
        getMainComponents().add(paper);

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

        getMainComponents().add(selected);

        int boxHeight = 200, boxWidth = 100;
        Sprite ringTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/ring_base.png"));
        Sprite staffTexture = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/items/staff_gold.png"));

		/*ComponentRect menu = new ComponentRect(0, 0, getGuiWidth(), getGuiHeight());
        menu.getColor().setValue(new Color(0x804A4A4A));


		ComponentRect ring = new ComponentRect((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2) - (boxHeight / 2), boxWidth, boxHeight);

		ComponentSprite sprite = new ComponentSprite(ringTexture, (boxWidth / 2) - (ringTexture.getWidth() / 2), (boxHeight / 2) - (ringTexture.getHeight() / 2));
		ring.add(sprite);

		new ButtonMixin<>(ring, () -> {
		});

		ring.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch (event.getNewState()) {
				case NORMAL:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()).mul(2));
					sprite.setPos(new Vec2d((boxWidth / 2.0) - ((ringTexture.getWidth() * 2.0) / 2.0), (boxHeight / 2.0) - ((ringTexture.getHeight() * 2.0) / 2.0)));
					ring.getColor().setValue(new Color(0x804A4A4A));
					ring.setSize(new Vec2d(boxWidth, boxHeight));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
					break;
				case HOVER:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()).mul(4));
					sprite.setPos(new Vec2d((boxWidth / 2.0) - ((ringTexture.getWidth() * 4.0) / 2.0), (boxHeight / 2.0) - ((ringTexture.getHeight() * 4.0) / 2.0)));
					ring.getColor().setValue(new Color(0x809A9A9A));
					ring.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55 - 10, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
					break;
				case DISABLED:
					sprite.setSize(new Vec2d(ringTexture.getWidth(), ringTexture.getHeight()));
					sprite.setPos(new Vec2d((boxWidth / 2.0) - (ringTexture.getWidth() / 2.0), (boxHeight / 2.0) - (ringTexture.getHeight() / 2.0)));
					ring.getColor().setValue(new Color(0x80222222));
					ring.setSize(new Vec2d(boxWidth, boxHeight));
					ring.setPos(new Vec2d((getGuiWidth() / 2) - (boxWidth / 2) - 55, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
					break;
			}
		});
		ring.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
			getMainComponents().remove(shapes);
			menu.invalidate();
		});


		ComponentRect staff = new ComponentRect(((getGuiWidth() / 2) - (boxWidth / 2)) + 55, (getGuiHeight() / 2) - (boxHeight / 2), boxWidth, boxHeight);

		ComponentSprite sprite2 = new ComponentSprite(staffTexture, (boxWidth / 2) - (staffTexture.getWidth() / 2), (boxHeight / 2) - (staffTexture.getHeight() / 2));
		staff.add(sprite2);

		new ButtonMixin<>(staff, () -> {
		});

		staff.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch (event.getNewState()) {
				case NORMAL:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()).mul(2));
					sprite2.setPos(new Vec2d((boxWidth / 2.0) - ((staffTexture.getWidth() * 2.0) / 2.0), (boxHeight / 2.0) - ((staffTexture.getHeight() * 2.0) / 2.0)));
					staff.getColor().setValue(new Color(0x804A4A4A));
					staff.setSize(new Vec2d(boxWidth, boxHeight));
					staff.setPos(new Vec2d(((getGuiWidth() / 2.0) - (boxWidth / 2.0)) + 55, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
					break;
				case HOVER:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()).mul(4));
					sprite2.setPos(new Vec2d((boxWidth / 2.0) - ((staffTexture.getWidth() * 4.0) / 2.0), (boxHeight / 2.0) - ((staffTexture.getHeight() * 4.0) / 2.0)));
					staff.getColor().setValue(new Color(0x809A9A9A));
					staff.setSize(new Vec2d(boxWidth + 20, boxHeight + 20));
					staff.setPos(new Vec2d(((getGuiWidth() / 2.0) - (boxWidth + (20.0 / 2.0))) + 55, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
					break;
				case DISABLED:
					sprite2.setSize(new Vec2d(staffTexture.getWidth(), staffTexture.getHeight()));
					sprite2.setPos(new Vec2d((boxWidth / 2.0) - (staffTexture.getWidth() / 2.0), (boxHeight / 2.0) - (staffTexture.getHeight() / 2.0)));
					staff.getColor().setValue(new Color(0x80222222));
					staff.setSize(new Vec2d(boxWidth, boxHeight));
					staff.setPos(new Vec2d(((getGuiWidth() / 2.0) - (boxWidth / 2.0)) + 55, (getGuiHeight() / 2.0) - (boxHeight / 2.0)));
			}
		});

		staff.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
			getMainComponents().remove(shapes);
			menu.invalidate();
		});

		menu.add(staff);
		menu.add(ring);*/
    }

    private void addModules(ComponentVoid parent, ModuleType type, int x, int y, int columns, int rows) {
        ComponentGrid grid = new ComponentGrid(x, y, 12, 12, columns);
        parent.add(grid);

        for (Module module : modulesByType.get(type)) {
            TableModule item = new TableModule(this, module, false);
            grid.add(item.component);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
