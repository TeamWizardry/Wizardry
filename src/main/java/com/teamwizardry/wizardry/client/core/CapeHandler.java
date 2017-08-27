package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class CapeHandler {

	public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/capes/cape_elucent_.png");

	public static CapeHandler INSTANCE = new CapeHandler();

	public HashMap<UUID, CapeRenderData> capes = new HashMap<>();

	private CapeHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static int CLOTH_WIDTH = 8;
	private static int CLOTH_HEIGHT = 16;

	@SubscribeEvent
	public void tickPlayer(TickEvent.PlayerTickEvent event) {
		capes.putIfAbsent(event.player.getUniqueID(), new CapeRenderData(event.player));

		if (capes.containsKey(event.player.getUniqueID())) {
			capes.get(event.player.getUniqueID()).update();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void drawPlayer(RenderLivingEvent.Post event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		if (!capes.containsKey(event.getEntity().getUniqueID())) return;
		if (event.getEntity().getHeldItemMainhand().getItem() == Items.BEEF) {
			if (!capes.isEmpty()) capes.clear();
			return;
		}

		ItemStack cape = Wizardry.proxy.getCape((EntityPlayer) event.getEntity());
		if (cape == null) return;

		GlStateManager.pushMatrix();
		GlStateManager.translate(event.getX() - ((EntityPlayer) event.getEntity()).posX, event.getY() - ((EntityPlayer) event.getEntity()).posY, event.getZ() - ((EntityPlayer) event.getEntity()).posZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		// OpenGL configuration set around `tessellator.draw()` statement at bottom for organization sake

		// VERTEX SETUP ================================================================================================
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		double height = 16 + capes.get(event.getEntity().getUniqueID()).length * 8;

		for (int h = 0; h < height; h++)
			for (int w = 0; w < CLOTH_WIDTH; w++) {
				vert(w, h, vb, event.getEntity().getUniqueID());
				vert(w + 1, h, vb, event.getEntity().getUniqueID());
				vert(w + 1, h + 1, vb, event.getEntity().getUniqueID());
				vert(w, h + 1, vb, event.getEntity().getUniqueID());

			}

		// OpenGL ======================================================================================================

		// BEGIN OPENGL CONFIGURATION
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.disableCull();

		tessellator.draw();
		GlStateManager.popMatrix();
	}

	public Vec3d pos(int w, int h, UUID uuid) {
		int i = w + h * (CLOTH_WIDTH + 1);

		if (i >= capes.get(uuid).cloth.points.size()) {
			return Vec3d.ZERO;
		}

		Vertex point = capes.get(uuid).cloth.points.get(i);

		return point.lastPos.add(point.pos.subtract(point.lastPos).scale(ClientTickHandler.getPartialTicks()));
	}

	public void vert(int w, int h, BufferBuilder vb, UUID uuid) {
		Vec3d vec = pos(w, h, uuid);
		vb.pos(vec.x, vec.y, vec.z).tex((w + capes.get(uuid).length * 8) / 64.0, h / 64.0).endVertex();
	}

	public class CapeRenderData {

		private int length = 4;
		private Cape cloth;
		private Cape pendulum;
		private Vec3d down = new Vec3d(0, -1, 0);
		private EntityPlayer player;

		public CapeRenderData(EntityPlayer player) {
			this.player = player;
			length = (int) (Math.min(Math.abs(new Random().nextGaussian() * 2), 4.0));
			cloth = new Cape(new GridCloth(
					player.getPositionVector().subtract(new Vec3d(1, 0, 0).scale(1 / 4.0)),
					new Vec3d(1, 0, 0).scale(1 / 16.0), new Vec3d(0, 0, 1).scale(-1).scale(1 / 16.0),
					CLOTH_WIDTH, 16 + 8 * length));
			pendulum = new Cape(new LineCloth(player.getPositionVector(), new Vec3d(0, -1, 0), 2));
			for (int i = 0; i < CLOTH_WIDTH; i++) {
				cloth.points.get(i).pinned = true;
			}
			pendulum.points.get(0).pinned = true;
			pendulum.dragDampingCoeff = 1.0;
			pendulum.gravity = new Vec3d(0, -0.1, 0);
		}

		public void update() {
			cloth.gravity = new Vec3d(0, -0.01, 0);
			cloth.windVelocity = new Vec3d(0, 0, 0);
			cloth.windForce = 0.1;


			pendulum.points.get(0).pos = player.getPositionVector();
			//pendulum.tick();

			Vec3d forward = new Vec3d(player.motionX, player.motionY, player.motionZ).normalize();
			down = (down.add(pendulum.points.get(1).pos.subtract(pendulum.points.get(0).pos))).normalize();

			Vec3d side = (down.crossProduct(forward));
			double len = side.lengthVector();
			if (len == 0.0) side = new Vec3d(1, 0, 0);
			else side = side.scale(1 / len);

			Vec3d origin = player.getPositionVector().subtract(side.scale(1 / 4.0));
			Vec3d unit = side.scale(1 / 16.0);

			for (int i = 0; i < CLOTH_WIDTH; i++) {
				cloth.points.get(i).pos = origin.add(unit.scale(i));
			}

			//cloth.tick();
		}
	}
}
