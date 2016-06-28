package com.teamwizardry.wizardry.api.gui;

import com.teamwizardry.wizardry.api.util.gui.Vec2;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.Set;

public abstract class GuiComponent implements IGuiDrawable {
	
	public int zIndex = 0;
	protected Vec2 pos, size;
	private Set<IGuiEventListener> eventListeners = new HashSet<>();
		
	public GuiComponent(int posX, int posY) {
		this(posX, posY, 0, 0);
	}
	
	public GuiComponent(int posX, int posY, int width, int height) {
		this.pos = new Vec2(posX, posY);
		this.size = new Vec2(width, height);
	}
	
	public Vec2 getPos() { return pos; }

	public void setPos(Vec2 pos) { this.pos = pos; }

	public Vec2 getSize() {
		return size;
	}

	public void setSize(Vec2 size) { this.size = size; }
	/**
	 * Add an event listener
	 * @param listener
	 */
	public void addEventListener(IGuiEventListener listener) {
		eventListeners.add(listener);
	}
	
	/**
	 * Post an event to all listeners
	 * @param event
	 */
	protected void postEvent(GuiEvent event) {
		for (IGuiEventListener listener : eventListeners) {
			listener.handle(event);
		}
	}
	
	/**
	 * Transforms the position passed to be relative to this component's position.
	 * @param pos
	 * @return
	 */
	public Vec2 relativePos(Vec2 pos) {
		return pos.sub(this.pos);
	}
	
	/**
	 * Test if the mouse is over this component. mousePos is relative to the position of the element.
	 * @param mousePos
	 * @return
	 */
	public boolean isMouseOver(Vec2 mousePos) {
		return mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y;
	}
	
	/**
	 * Called when the mouse is pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is released. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDrag(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse wheel is moved.
	 * @param mousePos
	 * @param button
	 */
	public void mouseWheel(Vec2 mousePos, int direction) {}
	
	/**
	 * Called when a key is pressed in the parent component.
	 * @param key The actual character that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyPressed(char key, int keyCode) {}
	
	/**
	 * Called when a key is released in the parent component.
	 * @param key The actual key that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyReleased(char key, int keyCode) {}
	
}
