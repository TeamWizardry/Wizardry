package me.lordsaad.wizardry.gui.book.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

import me.lordsaad.wizardry.gui.book.Tip;
import me.lordsaad.wizardry.gui.book.Tippable;
import me.lordsaad.wizardry.gui.book.pages.GuiPageCommon;
import me.lordsaad.wizardry.gui.book.pages.GuiPageText;


/**
 * A object that represents a special text element and it's bounds.
 * <p>
 * This class doesn't render text.
 *
 * @author piercecorcoran
 */
public class TextControl {
	
	public List<Rectangle> rects = new ArrayList<>();
	public int start, end;
	public String type;
	public String text;
	public DataNode data;
	public int tipID;
	
	public TextControl(DataNode node) {
		this.type = node.get("type").asString();
		this.text = "§n" + node.get("text").asString() + "§r";
		this.data = node;
	}
	
	public void draw(GuiPageCommon page, int mouseX, int mouseY, float partialTicks) {
		boolean hovering = isHovering(mouseX, mouseY);
		int textColor = 0xFF000000;
		
		if("link".equals(type))
			textColor = hovering ? 0xff0000EE : 0xff0F00B0;
		
		
		// GL_GREATER means that it only renders if it's below something.
		// that means that when I draw it below the text, it only shows up where the text is
		
		GlStateManager.depthFunc(GL11.GL_GREATER);
		// Move it back so it's behind the text
		GlStateManager.translate(0, 0, -20);
		int minY = Integer.MAX_VALUE;
		for (Rectangle rect : rects) {
			minY = Math.min(minY, rect.y);
			GuiScreen.drawRect(rect.x, rect.y, rect.x+rect.width, rect.y+rect.height, textColor);
		}
		GlStateManager.translate(0, 0, 20);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		
		if("tip".equals(type)) {
			if (hovering) {
				int id = Tippable.setTip(Tip.from(data.get("tip")));
				if(id != -1)
					tipID = id;
	        } else {
	            Tippable.removeTip(tipID);
	        }
		}
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
			
			if(i != -1) {
				try {
					page = Integer.parseInt( str.substring(i) );
				} catch (NumberFormatException e) {
					// TODO: logging
				}
			}
			
			gui.openPageRelative(str.substring(0, i == -1 ? str.length() : i), page);
		}
	}
}
