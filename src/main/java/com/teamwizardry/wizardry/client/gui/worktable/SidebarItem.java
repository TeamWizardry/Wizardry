package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.math.Vec2;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class SidebarItem {

	public ComponentSprite result;
	
	protected ModuleList.IModuleConstructor constructor;
	protected Module module;
	protected GuiComponent<?> paper;
	
	public SidebarItem(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		this.constructor = constructor;
		this.module = constructor.construct();
		this.paper = paper;		
		result = new ComponentSprite(WorktableGui.MODULE_DEFAULT, posX, posY, 12, 12);
		result.mouseIn.add( (c, pos) -> {
			result.setSprite(WorktableGui.MODULE_DEFAULT_GLOW);
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			result.setSprite(WorktableGui.MODULE_DEFAULT);
			return false;
		});
		result.mouseDown.add( (c, pos, button) -> {
			if(c.mouseOverThisFrame) {
				PaperModule m = new PaperModule(pos.xi, pos.yi, constructor, paper);
				m.drag.mouseDown = true;
				paper.add(m.result);
				return true;
			}
			return false;
		});
		
		result.addTag("sidebarItem");
	}
	
	
	
}
