package com.teamwizardry.wizardry.client.jei;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 5:43 PM on 1/13/18.
 */
@SideOnly(Side.CLIENT)
public class DrawableAtlas implements IDrawable {

	private final TextureAtlasSprite sprite;

	public DrawableAtlas(TextureAtlasSprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public void draw(@Nonnull Minecraft minecraft, int xOffset, int yOffset) {
		minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		float uMin = sprite.getMinU();
		float uMax = sprite.getMaxU();
		float vMin = sprite.getMinV();
		float vMax = sprite.getMaxV();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(xOffset, yOffset + 16, 0).tex(uMin, vMax).endVertex();
		bufferBuilder.pos(xOffset + getWidth(), yOffset + 16, 0).tex(uMax, vMax).endVertex();
		bufferBuilder.pos(xOffset + getWidth(), yOffset, 0).tex(uMax, vMin).endVertex();
		bufferBuilder.pos(xOffset, yOffset, 0).tex(uMin, vMin).endVertex();
		tessellator.draw();

	}
}
