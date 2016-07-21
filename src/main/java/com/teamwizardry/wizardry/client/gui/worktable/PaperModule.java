package com.teamwizardry.wizardry.client.gui.worktable;

import java.util.List;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
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
		ComponentSprite sprite = new ComponentSprite(module.getType().backgroundSprite, posX, posY, 12, 12);
		result.add(sprite);
		sprite.addTag("sprite");
		
		this.drag = new DragMixin<ComponentCenterAlign>(result, (v) -> v);
		this.drag.pickup.add((c, button, pos) -> button != EnumMouseButton.LEFT);
		this.drag.drop.add((c, button, pos) -> {
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
		
		ComponentSprite glow = new ComponentSprite(WorktableGui.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		result.add(glow);
		result.mouseIn.add( (c, pos) -> {
			glow.setVisible(true);
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			glow.setVisible(false);
			return false;
		});
		
		ComponentSprite icon = new ComponentSprite(this.module.getStaticIcon(), 2, 2, 8, 8);
		result.add(icon);
		result.mouseIn.add( (c, pos) -> {
			icon.setSprite(this.module.getAnimatedIcon());
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			icon.setSprite(this.module.getStaticIcon());
			return false;
		});
		result.addTag("module");
	}
	
}
