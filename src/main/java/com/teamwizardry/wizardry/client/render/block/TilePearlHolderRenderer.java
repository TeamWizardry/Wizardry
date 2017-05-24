package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TilePearlHolder;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePearlHolderRenderer extends TileEntitySpecialRenderer<TilePearlHolder> {

	private IBakedModel modelManaOrb, modelPearl;

	public TilePearlHolderRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelManaOrb = null;
		modelPearl = null;
	}

	private void getBakedModels() {
		IModel model = null;
		if (modelManaOrb == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_orb"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelManaOrb = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}

		if (modelPearl == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/pearl"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelPearl = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
	}

	@Override
	public void renderTileEntityAt(TilePearlHolder te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.pearl != null && (te.pearl.getItem() == ModItems.MANA_ORB || te.pearl.getItem() == ModItems.PEARL_NACRE)) {

			boolean isPearl = te.pearl.getItem() == ModItems.PEARL_NACRE;

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			getBakedModels();

			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if (Minecraft.isAmbientOcclusionEnabled())
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
			else GlStateManager.shadeModel(GL11.GL_FLAT);

			GlStateManager.translate(x, y - 0.5, z);
			GlStateManager.disableRescaleNormal();

			float sin = (float) Math.sin((te.getWorld().getTotalWorldTime() + partialTicks) / 10.0);
			boolean magnetFound = false;

			if (isPearl) {
				Vec3d direction = Vec3d.ZERO;
				for (int i = -4; i < 4; i++)
					for (int j = -4; j < 4; j++)
						for (int k = -4; k < 4; k++) {
							BlockPos pos = new BlockPos(te.getPos().getX() + i, te.getPos().getY() + j, te.getPos().getZ() + k);
							if (te.getWorld().getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;
							direction = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
							magnetFound = true;
							break;
						}

				if (magnetFound) {
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.translate(sin * direction.xCoord / 8.0, sin * direction.yCoord / 8.0, sin * direction.zCoord / 8.0);
					GlStateManager.translate(-direction.xCoord / 3.0, -direction.yCoord / 3.0, -direction.zCoord / 3.0);
					GlStateManager.translate(-0.5, -0.5, -0.5);
				}
			} else {
				Vec3d directionBattery = Vec3d.ZERO;
				boolean batteryFound = false;
				for (int i = -6; i < 6; i++)
					for (int j = -6; j < 6; j++)
						for (int k = -6; k < 6; k++) {
							BlockPos pos = new BlockPos(te.getPos().getX() + i, te.getPos().getY() + j, te.getPos().getZ() + k);
							if (te.getWorld().getBlockState(pos).getBlock() != ModBlocks.MANA_BATTERY) continue;
							directionBattery = new Vec3d(te.getPos()).subtract(new Vec3d(pos)).normalize();
							batteryFound = true;
							break;
						}
				if (batteryFound) {
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.translate(sin * directionBattery.xCoord / 8.0, sin * directionBattery.yCoord / 8.0, sin * directionBattery.zCoord / 8.0);
					GlStateManager.translate(-directionBattery.xCoord / 3.0, -directionBattery.yCoord / 3.0, -directionBattery.zCoord / 3.0);
					GlStateManager.translate(-0.5, -0.5, -0.5);
				}
			}

			if (!isPearl || !magnetFound)
				GlStateManager.translate(0, sin / 10.0, 0);

			if (isPearl) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(sin * 100.0f, 0, 1, 0);
				GlStateManager.translate(-0.5, 0.5, -0.5);

				if (!magnetFound) {
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.rotate(sin * 360.0f, 1, 0, 1);
					GlStateManager.translate(-0.5, -0.5, -0.5);
				} else {
					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.rotate(sin * 15.0f, 1, 0, 1);
					GlStateManager.translate(-0.5, -0.5, -0.5);
				}
			} else {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate((te.getWorld().getTotalWorldTime() + partialTicks) * 50.0f, 0, 1, 0);
				GlStateManager.translate(-0.5, 0.5, -0.5);

				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(43f, 1, 0, 1);
				GlStateManager.translate(-0.5, -0.5, -0.5);
			}

			Color color = Color.WHITE;
			if (te.pearl.getItem() == ModItems.PEARL_NACRE)
				color = new Color(Minecraft.getMinecraft().getItemColors().getColorFromItemstack(te.pearl, 0));

			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(te.pearl.getItem() == ModItems.MANA_ORB ? modelManaOrb : modelPearl, 1.0F, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
