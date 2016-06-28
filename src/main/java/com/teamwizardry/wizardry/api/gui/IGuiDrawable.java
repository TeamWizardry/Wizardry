package com.teamwizardry.wizardry.api.gui;

import com.teamwizardry.wizardry.api.util.gui.Vec2;

/**
 * An object that can be drawn to a gui
 * @author Pierce Corcoran
 */
public interface IGuiDrawable {

	/**
	 * Draw this object to the screen.
	 * @param mousePos
	 * @param partialTicks
	 */
	void draw(Vec2 mousePos, float partialTicks);
	
}
