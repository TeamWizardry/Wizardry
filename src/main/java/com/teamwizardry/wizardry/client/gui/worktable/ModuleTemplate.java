package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentCenterAlign;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.template.ComponentTemplate;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.lib.LibSprites;
import com.teamwizardry.wizardry.lib.LibSprites.Worktable;

public class ModuleTemplate extends ComponentTemplate<ComponentCenterAlign> {

	protected Module module;
	protected GuiComponent<?> paper;

	public ModuleTemplate(int posX, int posY, Module constructor, GuiComponent<?> paper) {
		super(new ComponentCenterAlign(posX, posY, false, false));

		module = constructor;
		this.paper = paper;

		ComponentSprite sprite = new ComponentSprite(module.getType().backgroundSprite, 0, 0, 12, 12);
		sprite.addTag("sprite");
		getResult().add(sprite);

		ComponentSprite glow = new ComponentSprite(Worktable.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		sprite.add(glow);


		ComponentSprite icon = new ComponentSprite(module.getStaticIcon(), 2, 2, 8, 8);
		sprite.add(icon);

		getResult().BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
			glow.setVisible(true);
			icon.setSprite(module.getAnimatedIcon());
		});
		getResult().BUS.hook(GuiComponent.MouseInEvent.class, (event) -> {
			glow.setVisible(false);
			icon.setSprite(module.getStaticIcon());
		});
		setSelfData(getClass());
	}

	private <D> void setSelfData(Class<D> klass) {
		getResult().setData(ModuleTemplate.class, "", this);
		getResult().setData(klass, "", (D) this);
	}

}
