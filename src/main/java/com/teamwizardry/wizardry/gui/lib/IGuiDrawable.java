package com.teamwizardry.wizardry.gui.lib;

import com.teamwizardry.wizardry.gui.util.Vec2;

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
	public void draw(Vec2 mousePos, float partialTicks);
	
}
