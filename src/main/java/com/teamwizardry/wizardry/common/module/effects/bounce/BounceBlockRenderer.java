package com.teamwizardry.wizardry.common.module.effects.bounce;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class BounceBlockRenderer {

	private static Set<BounceTracker> bounceTrackers = new HashSet<>();

	public static void addBounce(World world, BlockPos pos, int expiry) {
		bounceTrackers.add(new BounceTracker(world, pos, expiry));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		Set<BounceTracker> tmp = new HashSet<>(bounceTrackers);

		for (BounceTracker tracker : tmp) {
			if (Minecraft.getMinecraft().world.getTotalWorldTime() - tracker.lastWorldTick > tracker.expiry)
				bounceTrackers.remove(tracker);

			GlStateManager.pushMatrix();
			EntityPlayer player = Minecraft.getMinecraft().player;

			double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

			GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-0.1f, -1000f);
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.disableCull();
			GlStateManager.disableLighting();

			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/blocks/slime.png"));

			AxisAlignedBB aabb = Minecraft.getMinecraft().world.getBlockState(tracker.pos).getCollisionBoundingBox(Minecraft.getMinecraft().world, tracker.pos);
			if (aabb != null) {
				GlStateManager.translate(tracker.pos.getX(), tracker.pos.getY(), tracker.pos.getZ());

				double ix = aabb.minX;
				double iy = aabb.minY;
				double iz = aabb.minZ;
				double ax = aabb.maxX;
				double ay = aabb.maxY;
				double az = aabb.maxZ;

				Tessellator tess = Tessellator.getInstance();
				BufferBuilder bb = tess.getBuffer();
				bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

				bb.pos(ix, iy, iz).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, iy, iz).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, iy, az).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, iy, az).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				bb.pos(ix, iy, iz).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, ay, iz).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, ay, az).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, iy, az).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				bb.pos(ix, iy, iz).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, ay, iz).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, iz).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, iy, iz).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				bb.pos(ix, ay, iz).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, iz).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, az).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, ay, az).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				bb.pos(ax, iy, iz).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, iz).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, az).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, iy, az).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				bb.pos(ix, iy, az).tex(0, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ix, ay, az).tex(1, 0).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, ay, az).tex(1, 1).color(1f, 1f, 1f, 0.8f).endVertex();
				bb.pos(ax, iy, az).tex(0, 1).color(1f, 1f, 1f, 0.8f).endVertex();

				tess.draw();
				GlStateManager.translate(-tracker.pos.getX(), -tracker.pos.getY() - 1, -tracker.pos.getZ());
			}

			GlStateManager.enableTexture2D();
			GlStateManager.disablePolygonOffset();
			GlStateManager.popMatrix();
		}
	}


	private static class BounceTracker {

		public final long lastWorldTick;
		public final float when;
		public final int expiry;
		private BlockPos pos;

		public BounceTracker(World world, BlockPos pos, int expiry) {
			this.lastWorldTick = world.getTotalWorldTime();
			this.pos = pos;
			this.expiry = expiry;
			this.when = ClientTickHandler.getTicksInGame();
		}
	}
}
