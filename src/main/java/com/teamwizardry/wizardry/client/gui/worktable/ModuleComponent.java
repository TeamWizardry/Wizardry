package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class ModuleComponent {

	public ComponentCenterAlign result;
	
	protected ModuleList.IModuleConstructor constructor;
	protected Module module;
	protected GuiComponent<?> paper;
	
	public ModuleComponent(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		this.constructor = constructor;
		this.module = constructor.construct();
		this.paper = paper;
		
		result = new ComponentCenterAlign(posX, posY, false, false);
		
		ComponentSprite sprite = new ComponentSprite(module.getType().backgroundSprite, 0, 0, 12, 12);
		sprite.addTag("sprite");
		result.add(sprite);
		
		ComponentSprite glow = new ComponentSprite(WorktableGui.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		sprite.add(glow);
		
		
		ComponentSprite icon = new ComponentSprite(this.module.getStaticIcon(), 2, 2, 8, 8);
		sprite.add(icon);
		
		
		result.mouseIn.add( (c, pos) -> {
			glow.setVisible(true);
			icon.setSprite(this.module.getAnimatedIcon());
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			glow.setVisible(false);
			icon.setSprite(this.module.getStaticIcon());
			return false;
		});
		setSelfData(getClass());
	}
	
	private <D> void setSelfData(Class<D> klass) {
		result.setData(ModuleComponent.class, "", this);
		result.setData(klass, "", (D) this);
	}
	
}
