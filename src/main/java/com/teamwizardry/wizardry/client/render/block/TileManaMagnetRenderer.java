package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.tile.TileManaMagnet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TileManaMagnetRenderer extends TileEntitySpecialRenderer<TileManaMagnet> {

	private static ResourceLocation manaPearlCubeTexture = new ResourceLocation(Wizardry.MODID, "textures/blocks/mana_orb_cube.png");

	@Override
	public void render(TileManaMagnet te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.manaOrb != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
			GlStateManager.translate(0, MathHelper.sin(ClientTickHandler.getTicks()) / 2.0, 0);
			GlStateManager.disableRescaleNormal();
			{
				GlStateManager.enableRescaleNormal();
				GlStateManager.disableCull();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderHelper.disableStandardItemLighting();

				Minecraft.getMinecraft().getTextureManager().bindTexture(manaPearlCubeTexture);
				TilePearlHolderRenderer.renderCube(0.1, Color.WHITE);

				GlStateManager.disableRescaleNormal();
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
