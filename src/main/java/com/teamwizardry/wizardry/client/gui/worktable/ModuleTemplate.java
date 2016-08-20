package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.template.ComponentTemplate;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.lib.LibSprites;

public class ModuleTemplate extends ComponentTemplate<ComponentCenterAlign> {

	protected Module module;
	protected GuiComponent<?> paper;
	
	public ModuleTemplate(int posX, int posY, Module constructor, GuiComponent<?> paper) {
		super(new ComponentCenterAlign(posX, posY, false, false));
		
		this.module = constructor;
		this.paper = paper;
		
		ComponentSprite sprite = new ComponentSprite(module.getType().backgroundSprite, 0, 0, 12, 12);
		sprite.addTag("sprite");
		getResult().add(sprite);
		
		ComponentSprite glow = new ComponentSprite(LibSprites.Worktable.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		sprite.add(glow);
		
		
		ComponentSprite icon = new ComponentSprite(this.module.getStaticIcon(), 2, 2, 8, 8);
		sprite.add(icon);

		getResult().BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
			glow.setVisible(true);
			icon.setSprite(this.module.getAnimatedIcon());
		});
		getResult().BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
			glow.setVisible(false);
			icon.setSprite(this.module.getStaticIcon());
		});
		setSelfData(getClass());
	}
	
	private <D> void setSelfData(Class<D> klass) {
		getResult().setData(ModuleTemplate.class, "", this);
		getResult().setData(klass, "", (D) this);
	}
	
}
