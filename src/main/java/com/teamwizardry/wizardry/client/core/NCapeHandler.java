package com.teamwizardry.wizardry.client.core;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.UUID;


public class NCapeHandler {

	public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/capes/cape_elucent_.png");

	public static NCapeHandler INSTANCE = new NCapeHandler();

	public HashMap<UUID, CapeRenderData> capes = new HashMap<>();

	private NCapeHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static int CLOTH_WIDTH = 8;
	private static int CLOTH_HEIGHT = 16;

	@SubscribeEvent
	public void tickPlayer(TickEvent.PlayerTickEvent event) {
		capes.putIfAbsent(event.player.getUniqueID(), new CapeRenderData(event.player.getPositionVector()));

		if (capes.containsKey(event.player.getUniqueID())) {
			capes.get(event.player.getUniqueID()).update(new Vec3d(event.player.motionX, event.player.motionY, event.player.motionZ));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void drawPlayer(RenderLivingEvent.Post event) {
		if (!(event.getEntity() instanceof EntityPlayer) && capes.containsKey(event.getEntity().getUniqueID())) return;

		boolean match = false;
		ItemStack stack = null;
		EntityPlayer player = (EntityPlayer) event.getEntity();
		for (ItemStack eq : player.getArmorInventoryList()) {
			if ((eq != null) && (eq.getItem() == ModItems.CAPE)) {
				match = true;
				stack = eq;
				break;
			}
		}
		if (stack == null) {
			if (Loader.isModLoaded("baubles")) {
				IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
				for (int i : BaubleType.BODY.getValidSlots()) {
					ItemStack stack1 = inv.getStackInSlot(i);
					if (stack1.getItem() == ModItems.CAPE) {
						stack = stack1;
						match = true;
						break;
					}
				}
			}
		}
		if (!match) return;

		GlStateManager.pushMatrix();
		GlStateManager.translate(((EntityPlayer) event.getEntity()).posX, ((EntityPlayer) event.getEntity()).posY, ((EntityPlayer) event.getEntity()).posZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		// OpenGL configuration set around `tessellator.draw()` statement at bottom for organization sake

		// VERTEX SETUP ================================================================================================

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		double height = 16 + capes.get(event.getEntity().getUniqueID()).length * 8;

		for (int h = 0; h < height; h++)
			for (int w = 0; w < CLOTH_WIDTH; h++) {
				vert(w, h, vb, event.getEntity().getUniqueID());
				vert(w + 1, h, vb, event.getEntity().getUniqueID());
				vert(w + 1, h + 1, vb, event.getEntity().getUniqueID());
				vert(w, h + 1, vb, event.getEntity().getUniqueID());

			}

		// OpenGL ======================================================================================================

		// BEGIN OPENGL CONFIGURATION
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.disableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		tessellator.draw();

		// BEGIN OPENGL TEARDOWN
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	public Vec3d pos(int w, int h, UUID uuid) {
		int i = w + h * (CLOTH_WIDTH + 1);

		if (i >= capes.get(uuid).cloth.points.size()) {
			return Vec3d.ZERO;
		}

		Vertex point = capes.get(uuid).cloth.points.get(i);

		return point.lastPos.add((point.pos.subtract(point.lastPos)).scale(ClientTickHandler.getPartialTicks()));
	}

	public void vert(int w, int h, BufferBuilder vb, UUID uuid) {
		Vec3d vec = pos(w, h, uuid);
		vb.pos(vec.x, vec.y, vec.z).tex((w + capes.get(uuid).length * 8) / 64.0, h / 64.0).endVertex();
	}

	public class CapeRenderData {

		private Vec3d pos;
		private double length = 4;
		private Cape cloth;
		private Cape pendulum;
		private Vec3d down = new Vec3d(0, -1, 0);

		public CapeRenderData(Vec3d pos) {
			this.pos = pos;

			cloth = new Cape(new GridCloth(pos.subtract(new Vec3d(1, 0, 0)).scale(1 / 4), new Vec3d(1, 0, 0).scale(1 / 16.0), new Vec3d(0, 0, -1).scale(1 / 16.0), CLOTH_WIDTH, (int) (16 + 8 * length)));
			pendulum = new Cape(new LineCloth(pos, new Vec3d(0, -1, 0), 2));

			for (int i = 0; i < CLOTH_WIDTH; i++) {
				cloth.points.get(i).pinned = true;
			}
			pendulum.points.get(0).pinned = true;
			pendulum.dragDampingCoeff = 1.0;
			pendulum.gravity = new Vec3d(0, -0.1, 0);
		}

		public void update(Vec3d motion) {
			cloth.gravity = new Vec3d(0, -0.01, 0);
			cloth.windVelocity = Vec3d.ZERO;
			cloth.windForce = 0.1;


			pendulum.points.get(0).pos = pos;
			pendulum.tick();

			Vec3d forward = motion.normalize();
			down = down.add(pendulum.points.get(1).pos.subtract(pendulum.points.get(0).pos)).normalize();

			Vec3d side = down.crossProduct(forward);
			double length = side.lengthVector();
			if (length == 0.0) {
				side = new Vec3d(1, 0, 0);
			} else side.scale(length);

			Vec3d origin = pos.subtract(side.scale(1 / 4.0));
			Vec3d unit = side.scale(1 / 16.0);
			for (int i = 0; i < CLOTH_WIDTH; i++) {
				cloth.points.get(i).pos = origin.add(unit.scale(i));
			}

			cloth.tick();
		}
	}
}
