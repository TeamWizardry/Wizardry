package com.teamwizardry.wizardry.api.util.gui;

import com.teamwizardry.wizardry.api.gui.TextureDefinition;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class DrawingUtil {
	/**
	 * Draws a texture using the definition, doesn't bind the texture for performance reasons
	 */
	public static void drawRect(int x, int y, int width, int height, TextureDefinition def) {
		float minU = (float)def.minU/(float)def.texWidth;
		float maxU = (float)def.maxU/(float)def.texWidth;
		float minV = (float)def.minV/(float)def.texHeight;
		float maxV = (float)def.maxV/(float)def.texHeight;
		
		int minX = x;
		int maxX = x+width;
		int minY = y;
		int maxY = y+height;
		
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		vb.pos(minX, maxY, 0).tex(minU, maxV).endVertex();
		vb.pos(maxX, maxY, 0).tex(maxU, maxV).endVertex();
		vb.pos(maxX, minY, 0).tex(maxU, minV).endVertex();
		vb.pos(minX, minY, 0).tex(minU, minV).endVertex();

		tessellator.draw();
	}
}
