package me.lordsaad.wizardry.gui.book.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

import me.lordsaad.wizardry.gui.book.pages.GuiPageText;


/**
 * A object that represents a special text element and it's bounds.
 * 
 * This class doesn't render text.
 * @author piercecorcoran
 */
public class TextControl {

	public List<Rectangle> rects = new ArrayList<>();
	public int start, end;
	public String type;
	public String text;
	public DataNode data;
	
	public TextControl(DataNode node) {
		this.type = node.get("type").asString();
		this.text = node.get("text").asString();
		this.data = node;
		
		if("link".equals(type)) {
			text = "§f§n" + text + "§r§0";
		}
	}
	
	public void draw(int mouseX, int mouseY, float partialTicks) {
		boolean hovering = isHovering(mouseX, mouseY);
		int textColor = hovering ? 0xff0000EE : 0xff0F00B0;
		
		// GL_GREATER means that it only renders if it's below something.
		// that means that when I draw it below the text, it only shows up where the text is
		
		GlStateManager.depthFunc(GL11.GL_GREATER);
		// Move it back so it's behind the text
		GlStateManager.translate(0, 0, -20);
		for (Rectangle rect : rects) {
			GuiScreen.drawRect(rect.x, rect.y, rect.x+rect.width, rect.y+rect.height, textColor);
		}
		GlStateManager.translate(0, 0, 20);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		
	}
	
	public boolean isHovering(int mouseX, int mouseY) {
		for (Rectangle rect : rects) {
			if(rect.contains(mouseX, mouseY))
				return true;
		}
		return false;
	}
	
	public void mouseClick(GuiPageText gui, int mouseX, int mouseY, int mouseButton) {
		if("link".equals(type)) {
			String str = data.get("ref").asString();
			int i = str.lastIndexOf(":");
			int page = 0;
			
			try {
				page = Integer.parseInt( str.substring(i) );
			} catch (NumberFormatException e) {
				// TODO: logging
			}
			
			gui.openPageRelative(str.substring(0, i), page);
		}
	}
}
