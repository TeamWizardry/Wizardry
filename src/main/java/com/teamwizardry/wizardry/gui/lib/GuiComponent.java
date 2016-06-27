package com.teamwizardry.wizardry.gui.lib;

public abstract class GuiComponent implements IGuiDrawable {
	
	protected int posX, posY, width, height;
	
	public GuiComponent(int posX, int posY) {
		this(posX, posY, 0, 0);
	}
	
	public GuiComponent(int posX, int posY, int width, int height) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Test if the mouse is over this component. mouseX and mouseY are relative to the position of the element.
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height;
	}
	
	/**
	 * Called when the mouse is pressed. mouseX and mouseY are relative to the position of this component.
	 * @param mouseX
	 * @param mouseY
	 */
	public void mouseDown(int mouseX, int mouseY, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is released. mouseX and mouseY are relative to the position of this component.
	 * @param mouseX
	 * @param mouseY
	 * @param button
	 */
	public void mouseUp(int mouseX, int mouseY, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is moved while pressed. mouseX and mouseY are relative to the position of this component.
	 * @param mouseX
	 * @param mouseY
	 * @param button
	 */
	public void mouseDrag(int mouseX, int mouseY, EnumMouseButton button) {}
	
	/**
	 * Called when a key is pressed in the parent component.
	 * @param keyCode
	 */
	public void keyPressed(int keyCode) {}
	
	/**
	 * Called when a key is released in the parent component.
	 * @param keyCode
	 */
	public void keyReleased(int keyCode) {}
	
}
