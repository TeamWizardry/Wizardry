package com.teamwizardry.wizardry.gui.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;

import com.teamwizardry.wizardry.gui.util.ScissorUtil;

public class GuiComponentContainer extends GuiComponent {

	public GuiComponentContainer(int posX, int posY) {
		this(posX, posY, 100, 100);
	}
	
	public GuiComponentContainer(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
	}
	
	List<GuiComponent> components = new ArrayList<>();
	List<IGuiDrawable> drawables = new ArrayList<>();
	
	public void add(GuiComponent component) {
		components.add(component);
	}
	
	public void add(IGuiDrawable drawable) {
		drawables.add(drawable);
	}
	
	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		boolean wasEnabled = ScissorUtil.enable();
		ScissorUtil.push();
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		/* */
		
		
		
		/* */
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		ScissorUtil.pop();
		if(!wasEnabled)
			ScissorUtil.disable();
	}

}
