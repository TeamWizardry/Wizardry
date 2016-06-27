package com.teamwizardry.wizardry.gui.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TextureDefinition {

	public final ResourceLocation loc;
	public final int texWidth, texHeight;
	public final int u, v, width, height;
	public final int minU, minV, maxU, maxV;
	
	public TextureDefinition(ResourceLocation loc, int texWidth, int texHeight, int u, int v, int width, int height) {
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		
		this.minU = u;
		this.minV = v;
		this.maxU = u+width;
		this.maxV = v+height;
		
		this.loc = loc;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
	}
	
	public TextureDefinition sub(int u, int v, int width, int height) {
		return new TextureDefinition(loc, texWidth, texHeight, this.u+u, this.v+v, width, height);
	}
	
	public void bind() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
	}
}
