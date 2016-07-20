package com.teamwizardry.wizardry.client.gui.worktable;

import java.util.List;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.mixin.DragMixin;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class PaperModule {

	public ComponentCenterAlign result;
	public DragMixin<ComponentCenterAlign> drag;
	
	protected ModuleList.IModuleConstructor constructor;
	protected Module module;
	protected GuiComponent<?> paper;
	
	public PaperModule(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		this.constructor = constructor;
		this.module = constructor.construct();
		this.paper = paper;
		result = new ComponentCenterAlign(posX, posY, true, true);
		ComponentSprite sprite = new ComponentSprite(WorktableGui.MODULE_DEFAULT, posX, posY, 12, 12);
		result.add(sprite);
		sprite.addTag("sprite");
		
		result.mouseIn.add( (c, pos) -> {
			sprite.setSprite(WorktableGui.MODULE_DEFAULT_GLOW);
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			sprite.setSprite(WorktableGui.MODULE_DEFAULT);
			return false;
		});
		
		this.drag = new DragMixin<ComponentCenterAlign>(result, (v) -> v);
		
		this.drag.drop.add((c, pos) -> {
			GuiComponent<?> pep = paper;
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
		});
		
		result.addTag("module");
	}
	
}
