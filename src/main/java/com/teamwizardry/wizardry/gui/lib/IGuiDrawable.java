package com.teamwizardry.wizardry.gui.lib;

/**
 * An object that can be drawn to a gui
 * @author Pierce Corcoran
 */
public interface IGuiDrawable {

	/**
	 * Draw this object to the screen.
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	public void draw(int mouseX, int mouseY, float partialTicks);
	
}
