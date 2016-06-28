package com.teamwizardry.wizardry.api.gui.components;

import com.teamwizardry.wizardry.api.gui.EnumMouseButton;
import com.teamwizardry.wizardry.api.gui.GuiComponent;
import com.teamwizardry.wizardry.api.gui.TextureDefinition;
import com.teamwizardry.wizardry.api.gui.events.ButtonClickEvent;
import com.teamwizardry.wizardry.api.util.gui.DrawingUtil;
import com.teamwizardry.wizardry.api.util.gui.Vec2;

public class GuiComponentButton extends GuiComponent {

	public String id;
	TextureDefinition def;
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
