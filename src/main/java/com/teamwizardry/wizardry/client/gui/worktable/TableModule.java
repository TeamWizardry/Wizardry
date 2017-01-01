package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.lib.LibSprites;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class TableModule {

    public ComponentSprite component;

    public TableModule(WorktableGui table, Module module, boolean draggable) {
        ComponentSprite sprite = new ComponentSprite(module.backgroundSprite, 0, 0, 12, 12);
        if (draggable) sprite.addTag("draggable");

        ComponentSprite glow = new ComponentSprite(LibSprites.Worktable.IModule_DEFAULT_GLOW, 0, 0, 12, 12);
        glow.setVisible(false);
        sprite.add(glow);

        ComponentSprite icon = new ComponentSprite(module.getStaticIcon(), 2, 2, 8, 8);
        sprite.add(icon);

        sprite.BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
            glow.setVisible(true);
            icon.setSprite(module.getAnimatedIcon());
        });

        sprite.BUS.hook(GuiComponent.MouseOutEvent.class, (event) -> {
            glow.setVisible(false);
            icon.setSprite(module.getStaticIcon());
        });

        sprite.BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                if (!draggable) {
                    TableIModule item = new TableIModule(table, module, true);
                    item.component.setPos(new Vec2d(50, 50));
                    table.selected = item.component;
                } else {
                    sprite.setPos(event.getMousePos().sub(-6, -6));
                }
            }
        });

        sprite.BUS.hook(GuiComponent.MouseUpEvent.class, (event) -> {
            if (draggable && table.paper.getMouseOver()) table.paper.add(sprite);
        });

        sprite.BUS.hook(GuiComponent.MouseDragEvent.class, (event) -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                if (draggable) sprite.setPos(event.getMousePos().sub(-6, -6));
            }
        });

        sprite.BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
            if (event.getComponent().getMouseOver()) {
                List<String> txt = new ArrayList<>();
                txt.add(TextFormatting.GOLD + module.getDisplayName());
                event.getComponent().setTooltip(txt);
            }
        });

        component = sprite;
    }
}
