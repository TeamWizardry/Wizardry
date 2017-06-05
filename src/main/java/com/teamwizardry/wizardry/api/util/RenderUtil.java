package com.teamwizardry.wizardry.api.util;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderUtil {
	/**
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @param textures Order the textures and inversions like so: up (pos Y), down (neg Y), north (neg Z), south (pos Z), west (neg X), east (pos X)
	 */
	public static void addBox(VertexBuffer b, double x1, double y1, double z1, double x2, double y2, double z2, StructUV[] textures, int[] inversions) {
		//BOTTOM FACE
		b.pos(x1, y1, z1).tex(textures[0].minU, textures[0].minV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x1, y1, z2).tex(textures[0].maxU, textures[0].minV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x2, y1, z2).tex(textures[0].maxU, textures[0].maxV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x2, y1, z1).tex(textures[0].minU, textures[0].maxV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		//TOP FACE
		b.pos(x1, y2, z1).tex(textures[1].minU, textures[1].minV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x1, y2, z2).tex(textures[1].maxU, textures[1].minV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x2, y2, z2).tex(textures[1].maxU, textures[1].maxV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x2, y2, z1).tex(textures[1].minU, textures[1].maxV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		//NORTH FACE
		b.pos(x1, y1, z1).tex(textures[2].minU, textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x2, y1, z1).tex(textures[2].maxU, textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x2, y2, z1).tex(textures[2].maxU, textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x1, y2, z1).tex(textures[2].minU, textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		//SOUTH FACE
		b.pos(x1, y1, z2).tex(textures[3].minU, textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x2, y1, z2).tex(textures[3].maxU, textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x2, y2, z2).tex(textures[3].maxU, textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x1, y2, z2).tex(textures[3].minU, textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		//WEST FACE
		b.pos(x1, y1, z1).tex(textures[4].minU, textures[4].minV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y1, z2).tex(textures[4].maxU, textures[4].minV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y2, z2).tex(textures[4].maxU, textures[4].maxV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y2, z1).tex(textures[4].minU, textures[4].maxV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		//EAST FACE
		b.pos(x2, y1, z1).tex(textures[5].minU, textures[5].minV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y1, z2).tex(textures[5].maxU, textures[5].minV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y2, z2).tex(textures[5].maxU, textures[5].maxV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y2, z1).tex(textures[5].minU, textures[5].maxV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
	}

	public static void addBoxWithSprite(VertexBuffer b, double x1, double y1, double z1, double x2, double y2, double z2, TextureAtlasSprite sprite, StructUV[] textures, int[] inversions) {
		float spriteW = sprite.getMaxU() - sprite.getMinU();
		float spriteH = sprite.getMaxV() - sprite.getMinV();

		//BOTTOM FACE
		b.pos(x1, y1, z1).tex(sprite.getMinU() + textures[0].minU * spriteW, sprite.getMinV() + textures[0].minV * spriteH).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x1, y1, z2).tex(sprite.getMinU() + textures[0].maxU * spriteW, sprite.getMinV() + textures[0].minV * spriteH).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x2, y1, z2).tex(sprite.getMinU() + textures[0].maxU * spriteW, sprite.getMinV() + textures[0].maxV * spriteH).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		b.pos(x2, y1, z1).tex(sprite.getMinU() + textures[0].minU * spriteW, sprite.getMinV() + textures[0].maxV * spriteH).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		//TOP FACE
		b.pos(x1, y2, z1).tex(sprite.getMinU() + textures[1].minU * spriteW, sprite.getMinV() + textures[1].minV * spriteH).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x1, y2, z2).tex(sprite.getMinU() + textures[1].maxU * spriteW, sprite.getMinV() + textures[1].minV * spriteH).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x2, y2, z2).tex(sprite.getMinU() + textures[1].maxU * spriteW, sprite.getMinV() + textures[1].maxV * spriteH).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		b.pos(x2, y2, z1).tex(sprite.getMinU() + textures[1].minU * spriteW, sprite.getMinV() + textures[1].maxV * spriteH).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		//NORTH FACE
		b.pos(x1, y1, z1).tex(sprite.getMinU() + textures[2].minU * spriteW, sprite.getMinV() + textures[2].minV * spriteH).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x2, y1, z1).tex(sprite.getMinU() + textures[2].maxU * spriteW, sprite.getMinV() + textures[2].minV * spriteH).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x2, y2, z1).tex(sprite.getMinU() + textures[2].maxU * spriteW, sprite.getMinV() + textures[2].maxV * spriteH).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		b.pos(x1, y2, z1).tex(sprite.getMinU() + textures[2].minU * spriteW, sprite.getMinV() + textures[2].maxV * spriteH).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		//SOUTH FACE
		b.pos(x1, y1, z2).tex(sprite.getMinU() + textures[3].minU * spriteW, sprite.getMinV() + textures[3].minV * spriteH).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x2, y1, z2).tex(sprite.getMinU() + textures[3].maxU * spriteW, sprite.getMinV() + textures[3].minV * spriteH).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x2, y2, z2).tex(sprite.getMinU() + textures[3].maxU * spriteW, sprite.getMinV() + textures[3].maxV * spriteH).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		b.pos(x1, y2, z2).tex(sprite.getMinU() + textures[3].minU * spriteW, sprite.getMinV() + textures[3].maxV * spriteH).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		//WEST FACE
		b.pos(x1, y1, z1).tex(sprite.getMinU() + textures[4].minU * spriteW, sprite.getMinV() + textures[4].minV * spriteH).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y1, z2).tex(sprite.getMinU() + textures[4].maxU * spriteW, sprite.getMinV() + textures[4].minV * spriteH).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y2, z2).tex(sprite.getMinU() + textures[4].maxU * spriteW, sprite.getMinV() + textures[4].maxV * spriteH).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		b.pos(x1, y2, z1).tex(sprite.getMinU() + textures[4].minU * spriteW, sprite.getMinV() + textures[4].maxV * spriteH).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		//EAST FACE
		b.pos(x2, y1, z1).tex(sprite.getMinU() + textures[5].minU * spriteW, sprite.getMinV() + textures[5].minV * spriteH).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y1, z2).tex(sprite.getMinU() + textures[5].maxU * spriteW, sprite.getMinV() + textures[5].minV * spriteH).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y2, z2).tex(sprite.getMinU() + textures[5].maxU * spriteW, sprite.getMinV() + textures[5].maxV * spriteH).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		b.pos(x2, y2, z1).tex(sprite.getMinU() + textures[5].minU * spriteW, sprite.getMinV() + textures[5].maxV * spriteH).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
	}


	public static void addBoxExt(VertexBuffer b, double x1, double y1, double z1, double x2, double y2, double z2, StructUV[] textures, int[] inversions, boolean[] faceToggles) {
		//BOTTOM FACE
		if (faceToggles[0]) {
			b.pos(x1, y1, z1).tex(textures[0].minU, textures[0].minV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
			b.pos(x1, y1, z2).tex(textures[0].maxU, textures[0].minV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
			b.pos(x2, y1, z2).tex(textures[0].maxU, textures[0].maxV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
			b.pos(x2, y1, z1).tex(textures[0].minU, textures[0].maxV).color(255, 255, 255, 255).normal(0, -1 * inversions[0], 0).endVertex();
		}
		//TOP FACE
		if (faceToggles[1]) {
			b.pos(x1, y2, z1).tex(textures[1].minU, textures[1].minV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
			b.pos(x1, y2, z2).tex(textures[1].maxU, textures[1].minV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
			b.pos(x2, y2, z2).tex(textures[1].maxU, textures[1].maxV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
			b.pos(x2, y2, z1).tex(textures[1].minU, textures[1].maxV).color(255, 255, 255, 255).normal(0, 1 * inversions[1], 0).endVertex();
		}
		//NORTH FACE
		if (faceToggles[2]) {
			b.pos(x1, y1, z1).tex(textures[2].minU, textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
			b.pos(x2, y1, z1).tex(textures[2].maxU, textures[2].minV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
			b.pos(x2, y2, z1).tex(textures[2].maxU, textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
			b.pos(x1, y2, z1).tex(textures[2].minU, textures[2].maxV).color(255, 255, 255, 255).normal(0, 0, -1 * inversions[2]).endVertex();
		}
		//SOUTH FACE
		if (faceToggles[3]) {
			b.pos(x1, y1, z2).tex(textures[3].minU, textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
			b.pos(x2, y1, z2).tex(textures[3].maxU, textures[3].minV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
			b.pos(x2, y2, z2).tex(textures[3].maxU, textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
			b.pos(x1, y2, z2).tex(textures[3].minU, textures[3].maxV).color(255, 255, 255, 255).normal(0, 0, 1 * inversions[3]).endVertex();
		}
		//WEST FACE
		if (faceToggles[4]) {
			b.pos(x1, y1, z1).tex(textures[4].minU, textures[4].minV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
			b.pos(x1, y1, z2).tex(textures[4].maxU, textures[4].minV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
			b.pos(x1, y2, z2).tex(textures[4].maxU, textures[4].maxV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
			b.pos(x1, y2, z1).tex(textures[4].minU, textures[4].maxV).color(255, 255, 255, 255).normal(-1 * inversions[4], 0, 0).endVertex();
		}
		//EAST FACE
		if (faceToggles[5]) {
			b.pos(x2, y1, z1).tex(textures[5].minU, textures[5].minV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
			b.pos(x2, y1, z2).tex(textures[5].maxU, textures[5].minV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
			b.pos(x2, y2, z2).tex(textures[5].maxU, textures[5].maxV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
			b.pos(x2, y2, z1).tex(textures[5].minU, textures[5].maxV).color(255, 255, 255, 255).normal(1 * inversions[5], 0, 0).endVertex();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawQuadGui(VertexBuffer vertexbuffer, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int minU, int minV, int maxU, int maxV) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		vertexbuffer.pos((double) (x1 + 0.0F), (double) (y1 + 0.0F), (double) 0).tex(minU, maxV).endVertex();
		vertexbuffer.pos((double) (x2 + 0.0F), (double) (y2 + 0.0F), (double) 0).tex(maxU, maxV).endVertex();
		vertexbuffer.pos((double) (x3 + 0.0F), (double) (y3 + 0.0F), (double) 0).tex(maxU, minV).endVertex();
		vertexbuffer.pos((double) (x4 + 0.0F), (double) (y4 + 0.0F), (double) 0).tex(minU, minV).endVertex();
	}
}
