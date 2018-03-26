package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.CachedStructure;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.core.StructureErrorRenderer;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Wizardry.MODID)
public class TileManaBatteryRenderer extends TileRenderHandler<TileManaBattery> {

	private static IBakedModel modelRing, modelCrystal, modelRingOuter;
	private CachedStructure cachedStructure;

	public TileManaBatteryRenderer(@Nonnull TileManaBattery manaBattery) {
		super(manaBattery);
		cachedStructure = new CachedStructure(((IStructure) tile.getBlockType()).getStructure().loc, tile.getWorld());
	}

	static {
		ClientRunnable.registerReloadHandler(() -> {
			modelRing = null;
			modelCrystal = null;
			modelRingOuter = null;
		});
	}

	private static boolean getBakedModels() {
		IModel model;
		if (modelRing == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal_ring"));
				modelRing = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (modelRingOuter == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal_ring_outer"));
				modelRingOuter = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (modelCrystal == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal"));
				modelCrystal = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return modelRing != null && modelRingOuter != null && modelCrystal != null;
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {
		super.render(partialTicks, destroyStage, alpha);

		if (!getBakedModels()) return;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TextureManager texturemanager = Minecraft.getMinecraft().renderEngine;

		if (texturemanager != null) {
			texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		}

		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else GlStateManager.shadeModel(GL11.GL_FLAT);

		CapManager manager = new CapManager(tile.getWizardryCap());
		float fill = (float) (manager.getMana() / manager.getMaxMana()) / 50.0f;


		GlStateManager.translate(0, 0.5, 0);
		GlStateManager.disableRescaleNormal();

		{
			double x = RandUtil.nextDouble(-fill, fill), y = RandUtil.nextDouble(-fill, fill), z = RandUtil.nextDouble(-fill, fill);
			GlStateManager.translate(0, Math.sin((tile.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks()) / 40) / 8, 0);

			GlStateManager.translate(x, y, z);
			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelCrystal, 1.0F, 1, 1, 1);
			GlStateManager.translate(-x, -y, -z);
		}

		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(tile.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, 1, 0);
		GlStateManager.translate(-0.5, 0, -0.5);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelRing, 1.0F, 1, 1, 1);

		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(tile.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, -1, 0);
		GlStateManager.rotate(tile.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, -1, 0);
		GlStateManager.translate(-0.5, 0, -0.5);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelRingOuter, 1.0F, 1, 1, 1);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		HashSet<BlockPos> errors = ((IStructure) tile.getBlockType()).getErroredBlocks(tile.getWorld(), tile.getPos());
		if (tile.revealStructure && tile.getBlockType() instanceof IStructure && !errors.isEmpty()) {

			IStructure structure = ((IStructure) tile.getBlockType());

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(1f, -0.05f);

			GlStateManager.translate(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ());
			Minecraft mc = Minecraft.getMinecraft();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buffer = tes.getBuffer();

			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			for (BlockRenderLayer layer : cachedStructure.blocks.keySet()) {
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
				buffer.addVertexData(cachedStructure.vboCaches.get(layer));

				for (int i = 0; i < buffer.getVertexCount(); i++) {
					int idx = buffer.getColorIndex(i + 1);
					buffer.putColorRGBA(idx, 255, 255, 255, 200);
				}
				tes.draw();
			}

			GlStateManager.disablePolygonOffset();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();

		} else if (!tile.revealStructure && !errors.isEmpty()) {
			for (BlockPos error : errors)
				StructureErrorRenderer.INSTANCE.addError(error);
		}

		if (tile.getBlockType() == ModBlocks.CREATIVE_MANA_BATTERY) {
			double angle = tile.getWorld().getTotalWorldTime() / 10.0;
			double x1 = Math.cos((float) angle);
			double y1 = Math.sin((float) angle);

			ParticleBuilder builder = new ParticleBuilder(10);
			builder.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			builder.setCollision(true);
			builder.disableRandom();
			builder.disableMotionCalculation();

			ParticleSpawner.spawn(builder, tile.getWorld(), new InterpCircle(new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5), new Vec3d(x1, x1, y1), 1.5f), 20, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setScale(0.5f);
				particleBuilder.setColor(new Color(0xd600d2));
				particleBuilder.setAlphaFunction(new InterpFadeInOut(1, 1));
				particleBuilder.setLifetime(RandUtil.nextInt(5, 10));
			});
		}
	}
}
