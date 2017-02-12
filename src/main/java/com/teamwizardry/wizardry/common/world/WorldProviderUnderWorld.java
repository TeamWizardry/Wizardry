package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad44
 */
public class WorldProviderUnderWorld extends WorldProvider {

	@NotNull
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorUnderWorld(world);
	}

	@NotNull
	@Override
	public DimensionType getDimensionType() {
		return Wizardry.underWorld;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public long getWorldTime() {
		return 16000;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getCloudHeight() {
		return -10000;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getVoidFogYFactor() {
		return 1.0;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getAverageGroundLevel() {
		return 0;
	}

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float p1, float p2) {
		return new Vec3d(0.1, 0.1, 0.2);
	}

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getSkyColor(@NotNull Entity cameraEntity, float partialTicks) {
		return new Vec3d(0.1, 0.1, 0.2);
	}

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer() {
		return UnderworldSky.INSTANCE;
	}

	@NotNull
	@Override
	public String getSaveFolder() {
		return "underworld";
	}

	@SideOnly(Side.CLIENT)
	public static class UnderworldSky extends IRenderHandler {
		public static UnderworldSky INSTANCE = new UnderworldSky();

		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			if (p != null) {
				if (p.getEntityWorld().provider instanceof WorldProviderUnderWorld) {
					ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/misc/underworld_sky.png");
					Minecraft.getMinecraft().renderEngine.bindTexture(texture);
					GlStateManager.pushMatrix();
					GlStateManager.disableCull();
					GlStateManager.disableFog();
					GlStateManager.disableLighting();
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

					GlStateManager.depthMask(false);
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexbuffer = tessellator.getBuffer();

					for (int i = 0; i < 6; ++i) {
						GlStateManager.pushMatrix();

						if (i == 1) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
						if (i == 2) GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
						if (i == 3) GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
						if (i == 4) GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
						if (i == 5) GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);

						vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
						vertexbuffer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).endVertex();
						vertexbuffer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 1.0D).endVertex();
						vertexbuffer.pos(100.0D, -100.0D, 100.0D).tex(1.0D, 1.0D).endVertex();
						vertexbuffer.pos(100.0D, -100.0D, -100.0D).tex(1.0D, 0.0D).endVertex();
						tessellator.draw();
						GlStateManager.popMatrix();
					}

					GlStateManager.depthMask(true);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.enableCull();
					GlStateManager.enableLighting();
					GlStateManager.disableAlpha();
					GlStateManager.enableFog();
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}
			}
		}

	}
}
