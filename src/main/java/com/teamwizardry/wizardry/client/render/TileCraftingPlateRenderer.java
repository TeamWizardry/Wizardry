package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.structure.Structure;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import com.teamwizardry.wizardry.init.ModStructures;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.structure.template.Template;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/11/2016.
 */
public class TileCraftingPlateRenderer extends TileEntitySpecialRenderer<TileCraftingPlate> {

	@Override
	public void renderTileEntityAt(TileCraftingPlate te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos().subtract(new Vec3i(0, 64, 0));
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			Tessellator tes = Tessellator.getInstance();
			VertexBuffer vb = tes.getBuffer();
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			Structure structure = ModStructures.INSTANCE.structures.get(te.structureName());
			for (Template.BlockInfo info : structure.blockInfos())
				Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(info.blockState, pos.add(info.pos.getX(), info.pos.getY(), info.pos.getZ()), te.getWorld(), vb);
			tes.draw();

			GlStateManager.popMatrix();
		}

		for (ClusterObject cluster : te.inventory) {
			double timeDifference = (te.getWorld().getTotalWorldTime() - cluster.worldTime + partialTicks) / cluster.destTime;
			Vec3d current = cluster.origin.add(cluster.dest.subtract(cluster.origin).scale(MathHelper.sin((float) (timeDifference * Math.PI / 2))));

			if (!te.isCrafting && ThreadLocalRandom.current().nextInt(20) == 0)
				LibParticles.CLUSTER_DRAPE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5).add(current));

			if (te.isCrafting && (te.output != null)) {
				if (ThreadLocalRandom.current().nextInt(20) == 0)
					LibParticles.CRAFTING_ALTAR_HELIX(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.25, 0.5));
				if (((ThreadLocalRandom.current().nextInt(10)) != 0)) continue;
				LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5), new InterpBezier3D(current, new Vec3d(0, 0, 0)));
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5 + current.xCoord, y + 0.5 + current.yCoord, z + 0.5 + current.zCoord);
			GlStateManager.scale(0.3, 0.3, 0.3);
			GlStateManager.rotate((float) ClientTickHandler.getTicksInGame(), 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(cluster.stack, TransformType.NONE);
			GlStateManager.popMatrix();
		}

		if (!te.isCrafting && (te.output != null)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1, z + 0.5);
			GlStateManager.scale(0.4, 0.4, 0.4);
			GlStateManager.rotate(te.tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output, TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}

}
