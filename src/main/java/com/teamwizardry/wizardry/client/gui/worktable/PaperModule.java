package com.teamwizardry.wizardry.client.gui.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.api.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.api.gui.components.mixin.DragMixin;
import com.teamwizardry.librarianlib.api.gui.components.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class PaperModule extends ModuleComponent {

	public DragMixin<ComponentCenterAlign> drag;
	public ComponentVoid lines;
	public ComponentModuleLine mouseLine;
	
	public List<PaperModule> connections = new ArrayList<>();
	
	public PaperModule(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		super(posX, posY, constructor, paper);

		result.centerHorizontal = true;
		result.centerVertical = true;
		GlMixin.transform(result).setValue(new Vec3d(0, 0, 5));
		
		drag = new DragMixin<ComponentCenterAlign>(result, (v) -> v);
		drag.pickup.add((c, button, pos) -> button != EnumMouseButton.LEFT);
		drag.drop.add((c, button, pos) -> {
			if(button != EnumMouseButton.LEFT)
				return true;
			List<GuiComponent<?>> trays = paper.getByTag("tray");
			boolean hover = false;
			for (GuiComponent<?> tray : trays) {
				if(tray.isMouseOver(tray.relativePos(c.getPos()))) {
					hover = true;
					break;
				}
			}
			if(!hover)
				c.invalidate();
			return false;
		});
		
		lines = new ComponentVoid(0, 0);
		result.add(lines);

		mouseLine = new ComponentModuleLine(0, 0);
		mouseLine.endPos.func((c) -> c.mousePosThisFrame);
		GlMixin.color(mouseLine).setValue(Color.BLACK);
		mouseLine.setVisible(false);
		GlMixin.transform(mouseLine).setValue(new Vec3d(0, 0, -2));
		lines.add(mouseLine);
		GlMixin.transform(lines).setValue(new Vec3d(0, 0, -2));
		
		result.addTag("module");
		
		if(paper.getData(DraggingFromData.class) == null)
			paper.setData(DraggingFromData.class, new DraggingFromData());
		
		DraggingFromData data = paper.getData(DraggingFromData.class);
		
		result.mouseDown.add((c, pos, button) -> {
			if(c.mouseOverThisFrame && button == EnumMouseButton.RIGHT) {
				data.draggingFrom = this;
				data.shouldDeleteNextFrame = 0;
				mouseLine.setVisible(true);
			}
			return false;
		});
		
		result.mouseUp.add((c, pos, button) -> {
			if(button == EnumMouseButton.RIGHT) {
				if(c.mouseOverThisFrame && data.draggingFrom != this) {
					if(this.connections.contains(data.draggingFrom)) {
						this.connections.remove(data.draggingFrom);
						this.lines.removeByTag(data.draggingFrom);
					} else if(data.draggingFrom.connections.contains(this)) {
						data.draggingFrom.connections.remove(this);
						data.draggingFrom.lines.removeByTag(this);
					} else if(!data.draggingFrom.connections.contains(this)){
						PaperModule module = data.draggingFrom;
						module.connections.add(this);
						ComponentModuleLine line = new ComponentModuleLine(0,0);
						line.addTag(this);
						GlMixin.color(line).setValue(Color.BLACK);
						line.endPos.func((_c_) -> {
							if(this.result.getParent() == null) {
								_c_.invalidate();
								module.connections.remove(this);
							}
							return this.result.getPos().sub(module.result.getPos());
						});
						module.lines.add(line);
					}
				}
				if(data.draggingFrom == this) {
					mouseLine.setVisible(false);
				}
			}
			return false;
		});
		
	}
	
	public static class DraggingFromData {
		public PaperModule draggingFrom;
		public int shouldDeleteNextFrame = 0;
	}
	
}
