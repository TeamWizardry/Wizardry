package com.teamwizardry.wizardry.gui.lib.components;

import com.teamwizardry.wizardry.gui.lib.EnumMouseButton;
import com.teamwizardry.wizardry.gui.lib.GuiComponent;
import com.teamwizardry.wizardry.gui.lib.TextureDefinition;
import com.teamwizardry.wizardry.gui.lib.events.ButtonClickEvent;
import com.teamwizardry.wizardry.gui.util.DrawingUtil;
import com.teamwizardry.wizardry.gui.util.Vec2;

public class GuiComponentButton extends GuiComponent {

	TextureDefinition def;
	public String id;
	boolean mouseDownInside = false;
	
	public GuiComponentButton(String id, int posX, int posY, int width, int height, TextureDefinition def) {
		super(posX, posY, width, height);
		this.def = def;
		this.id = id;
	}
	
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		def.bind();
		DrawingUtil.drawRect(pos.xi, pos.yi, size.xi, size.yi, def);
	}

	@Override
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		if(isMouseOver(mousePos)) {
			mouseDownInside = true;
		}
	}
	
	@Override
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		if(mouseDownInside && isMouseOver(mousePos)) {
			postEvent(new ButtonClickEvent(this));
		}
		mouseDownInside = false;
	}
	
}
