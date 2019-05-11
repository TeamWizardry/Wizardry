package com.teamwizardry.wizardry.common.world.trickery;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.world.biome.BiomeTorikki;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque44
 */
public class WorldProviderTorikki extends WorldProvider {

	@Nonnull
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorTorikki(world, getSeed());
	}

	@Nonnull
	@Override
	public DimensionType getDimensionType() {
		return Wizardry.torikki;
	}

	@Nonnull
	@Override
	public Biome getBiomeForCoords(@Nonnull BlockPos pos) {
		return new BiomeTorikki(new Biome.BiomeProperties("torikki"));
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
		return 256;
	}

	@Override
	public int getAverageGroundLevel() {
		return 0;
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float p1, float p2) {
		return new Vec3d(0.1, 0.1, 0.2);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getSkyColor(@Nonnull Entity cameraEntity, float partialTicks) {
		return new Vec3d(0.1, 0.1, 0.2);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer() {
		return TorikkiSky.INSTANCE;
	}

	@Nonnull
	@Override
	public String getSaveFolder() {
		return "torikki";
	}
	
	@Override
	protected void generateLightBrightnessTable()
	{
		// Fixed vanilla algorithm to use correct scaling
		float offset = 2/9F;
		
		for (int i = 0; i < 16; i++)
			this.lightBrightnessTable[i] = i / (60F - 3*i) * (1 - offset) + offset;
	}

	@SideOnly(Side.CLIENT)
	public static class TorikkiSky extends IRenderHandler {
		public static TorikkiSky INSTANCE = new TorikkiSky();

		@Override
		public void render(float partialTicks, WorldClient world, Minecraft mc) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			if (p != null) {
				if (p.getEntityWorld().provider instanceof WorldProviderTorikki) {
					ResourceLocation img = new ResourceLocation(Wizardry.MODID, "textures/misc/torikki_sky.png");
					Minecraft.getMinecraft().renderEngine.bindTexture(img);
					GlStateManager.pushMatrix();
					GlStateManager.disableCull();
					GlStateManager.disableFog();
					GlStateManager.disableLighting();

					GlStateManager.depthMask(false);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder vertexbuffer = tessellator.getBuffer();

					for (int i = 0; i < 6; ++i) {
						GlStateManager.pushMatrix();

						Minecraft.getMinecraft().renderEngine.bindTexture(img);
						if (i == 3) {
							Minecraft.getMinecraft().renderEngine.bindTexture(img);
							GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
						}
						if (i == 1) {
							Minecraft.getMinecraft().renderEngine.bindTexture(img);
							GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
						}
						if (i == 2) {
							Minecraft.getMinecraft().renderEngine.bindTexture(img);
							GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
							GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
						}
						if (i == 4) {
							Minecraft.getMinecraft().renderEngine.bindTexture(img);
							GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
							GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
						}
						if (i == 5) {
							Minecraft.getMinecraft().renderEngine.bindTexture(img);
							GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
							GlStateManager.rotate(-270.0F, 0.0F, 1.0F, 0.0F);
						}

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
