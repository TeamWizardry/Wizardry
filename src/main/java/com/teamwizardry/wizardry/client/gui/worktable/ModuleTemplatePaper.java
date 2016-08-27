package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.client.gui.mixin.gl.GlMixin;
import com.teamwizardry.wizardry.api.module.Module;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleTemplatePaper extends ModuleTemplate {

    public DragMixin<ComponentCenterAlign> drag;
    public ComponentVoid lines;
    public ComponentModuleLine mouseLine;

    public List<ModuleTemplatePaper> connections = new ArrayList<>();

    public ModuleTemplatePaper(int posX, int posY, Module constructor, GuiComponent<?> paper) {
        super(posX, posY, constructor, paper);

        getResult().setCenterHorizontal(true);
        getResult().setCenterVertical(true);
        GlMixin.INSTANCE.transform(getResult()).setValue(new Vec3d(0, 0, 5));

        drag = new DragMixin<>(getResult(), (v) -> v);
        getResult().BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
            if (event.getButton() != EnumMouseButton.LEFT)
                event.cancel();
        });
        getResult().BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
            if (event.getButton() != EnumMouseButton.LEFT)
                event.cancel();
            List<GuiComponent<?>> trays = paper.getByTag("tray");
            boolean hover = false;
            for (GuiComponent<?> tray : trays) {
                if (tray.getLogicalSize().contains(event.getComponent().getPos().sub(tray.getPos()))) {
                    hover = true;
                    break;
                }
            }
            if (!hover)
                event.getComponent().invalidate();
        });

        lines = new ComponentVoid(0, 0);
        getResult().add(lines);

        mouseLine = new ComponentModuleLine(0, 0);
        mouseLine.endPos.func((c) -> c.getMousePosThisFrame());
        GlMixin.INSTANCE.color(mouseLine).setValue(Color.BLACK);
        mouseLine.setVisible(false);
        GlMixin.INSTANCE.transform(mouseLine).setValue(new Vec3d(0, 0, -2));
        lines.add(mouseLine);
        GlMixin.INSTANCE.transform(lines).setValue(new Vec3d(0, 0, -2));

        getResult().addTag("module");

        if (paper.getData(DraggingFromData.class, "") == null)
            paper.setData(DraggingFromData.class, "", new DraggingFromData());

        DraggingFromData data = paper.getData(DraggingFromData.class, "");

        getResult().BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
            if (event.getComponent().getMouseOver() && event.getButton() == EnumMouseButton.RIGHT) {
                data.draggingFrom = this;
                data.shouldDeleteNextFrame = 0;
                mouseLine.setVisible(true);
            }
        });

        getResult().BUS.hook(GuiComponent.MouseUpEvent.class, (event) -> {
            if (event.getButton() == EnumMouseButton.RIGHT) {
                if (event.getComponent().getMouseOver() && data.draggingFrom != this) {
                    if (this.connections.contains(data.draggingFrom)) {
                        this.connections.remove(data.draggingFrom);
                        this.lines.removeByTag(data.draggingFrom);
                    } else if (data.draggingFrom.connections.contains(this)) {
                        data.draggingFrom.connections.remove(this);
                        data.draggingFrom.lines.removeByTag(this);
                    } else if (!data.draggingFrom.connections.contains(this)) {
                        ModuleTemplatePaper module = data.draggingFrom;
                        module.connections.add(this);
                        ComponentModuleLine line = new ComponentModuleLine(0, 0);
                        line.addTag(this);
                        GlMixin.INSTANCE.color(line).setValue(Color.BLACK);
                        line.endPos.func((_c_) -> {
                            if (this.getResult().getParent() == null) {
                                _c_.invalidate();
                                module.connections.remove(this);
                            }
                            return this.getResult().getPos().sub(module.getResult().getPos());
                        });
                        module.lines.add(line);
                    }
                }
                if (data.draggingFrom == this) {
                    mouseLine.setVisible(false);
                }
            }
        });

    }

    public static class DraggingFromData {
        public ModuleTemplatePaper draggingFrom;
        public int shouldDeleteNextFrame = 0;
    }

}
