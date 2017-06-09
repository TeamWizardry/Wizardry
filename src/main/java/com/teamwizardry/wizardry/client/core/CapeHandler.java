package com.teamwizardry.wizardry.client.core;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.cloth.Cloth;
import com.teamwizardry.wizardry.client.cloth.PointMass3D;
import com.teamwizardry.wizardry.client.cloth.Sphere;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.Map.Entry;

public class CapeHandler {

	public static final CapeHandler INSTANCE = new CapeHandler();
	private static Vec3d[] basePoints;

	private final Map<EntityLivingBase, Cloth> cloths = new WeakHashMap<>();

	private CapeHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		basePointsSet();
	}

	private void basePointsSet() {
		double y = 1.4;
		double p = 0.058;
		basePoints = new Vec3d[]{
				new Vec3d(8 * p, y, -2 * p),
				new Vec3d(6 * p, y, -2 * p),
				new Vec3d(4 * p, y, -2 * p),
				new Vec3d(2 * p, y, -2 * p),
				new Vec3d(0.0, y, -2 * p),
				new Vec3d(-2 * p, y, -2 * p),
				new Vec3d(-4 * p, y, -2 * p),
				new Vec3d(-6 * p, y, -2 * p),
				new Vec3d(-8 * p, y, -2 * p),
		};
	}

	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		if (event.phase != Phase.END)
			return;

		basePointsSet();

		List<EntityLivingBase> keysToRemove = new ArrayList<>();

		for (Entry<EntityLivingBase, Cloth> e : cloths.entrySet()) {
			EntityLivingBase entity = e.getKey();
			if (entity.isDead) {
				keysToRemove.add(entity);
				continue;
			}

			if ((e.getValue().masses != null) && (e.getValue().masses[0] != null)) {

				Matrix4 matrix = new Matrix4(), inverse = new Matrix4();
				matrix.translate(entity.getPositionVector());
				matrix.rotate(Math.toRadians(entity.renderYawOffset), new Vec3d(0, -1, 0));

				inverse.rotate(Math.toRadians(entity.renderYawOffset), new Vec3d(0, 1, 0));
				inverse.translate(entity.getPositionVector().scale(-1));

				Vec3d[] shoulderPoints = new Vec3d[basePoints.length];
				for (int i = 0; i < shoulderPoints.length; i++) {
					shoulderPoints[i] = matrix.apply(basePoints[i]);
				}

				for (int i = 0; (i < e.getValue().masses[0].length) && (i < shoulderPoints.length); i++) {
					e.getValue().masses[0][i].origPos = e.getValue().masses[0][i].pos;
					e.getValue().masses[0][i].pos = shoulderPoints[i];
				}

				List<Sphere> spheres = new ArrayList<>();

				Vec3d[] vecs = {
						new Vec3d(0.1, 0, 0),
						new Vec3d(0.1, 0.25, 0),
						new Vec3d(0.1, 0.5, 0),
						new Vec3d(0.1, 0.75, 0),
						new Vec3d(0.1, 1, 0),
						new Vec3d(0.1, 1.25, 0),
						new Vec3d(0.1, 1.5, 0),

						new Vec3d(-0.1, 0, 0),
						new Vec3d(-0.1, 0.25, 0),
						new Vec3d(-0.1, 0.5, 0),
						new Vec3d(-0.1, 0.75, 0),
						new Vec3d(-0.1, 1, 0),
						new Vec3d(-0.1, 1.25, 0),
						new Vec3d(-0.1, 1.5, 0),

						new Vec3d(0.3, 0.75, 0),
						new Vec3d(0.3, 1, 0),
						new Vec3d(0.3, 1.25, 0),

						new Vec3d(-0.3, 0.75, 0),
						new Vec3d(-0.3, 1, 0),
						new Vec3d(-0.3, 1.25, 0),
				};

				for (Vec3d vec : vecs) {
					vec = matrix.apply(vec);
					spheres.add(new Sphere(vec, 0.25));
				}

				e.getValue().tick(entity, spheres);
			}
		}

		for (EntityLivingBase entity : keysToRemove) {
			cloths.remove(entity);
		}
	}

	@SubscribeEvent
	public void damage(ItemTossEvent event) {
		if (event.getEntityItem().getEntityItem().getItem() == Items.BEEF) {
			cloths.clear();
		}
	}

	@SuppressWarnings({"rawtypes"})
	@SubscribeEvent
	public void drawPlayer(RenderLivingEvent.Post event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;

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

		float partialTicks = ClientTickHandler.getPartialTicks();

		if (!cloths.containsKey(event.getEntity())) {
			Vec3d[] shoulderPoints = new Vec3d[basePoints.length];

			Matrix4 matrix = new Matrix4();
			matrix.translate(event.getEntity().getPositionVector());
			matrix.rotate(Math.toRadians(event.getEntity().renderYawOffset), new Vec3d(0, -1, 0));

			for (int i = 0; i < shoulderPoints.length; i++) {
				shoulderPoints[i] = matrix.apply(basePoints[i]);
			}

			cloths.put(event.getEntity(), new Cloth(
					shoulderPoints,
					16,
					new Vec3d(0, 0.1, 0)
			));
		}

		Cloth c = cloths.get(event.getEntity());

		Tessellator tess = Tessellator.getInstance();
		VertexBuffer vb = tess.getBuffer();

		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.translate(event.getX(), event.getY(), event.getZ());
		GlStateManager.translate(
				-(event.getEntity().posX - event.getEntity().lastTickPosX) * partialTicks,
				-(event.getEntity().posY - event.getEntity().lastTickPosY) * partialTicks,
				-(event.getEntity().posZ - event.getEntity().lastTickPosZ) * partialTicks
		);
		GlStateManager.translate(-event.getEntity().lastTickPosX, -event.getEntity().lastTickPosY, -event.getEntity().lastTickPosZ);

		GlStateManager.disableTexture2D();
		GlStateManager.glLineWidth(1.0f);
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1.0f);

		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		tess.draw();

		String cape;
		if (LibrarianLib.PROXY.getResource(Wizardry.MODID, "textures/capes/cape_" + player.getName().toLowerCase() + ".png") == null) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "uuid");
			if (uuid == null) {
				uuid = UUID.randomUUID();
				ItemNBTHelper.setUUID(stack, "uuid", uuid);
			}
			Random r = new Random(uuid.hashCode());
			cape = "cape_normal_" + (1 + r.nextInt(3));
		} else {
			cape = "cape_" + player.getName();
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/capes/" + cape + ".png"));
		GlStateManager.enableTexture2D();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		for (int x = 0; x < (c.masses.length - 1); x++) {
			for (int y = 0; y < (c.masses[x].length - 1); y++) {
				float minU = y / (float) (c.masses[x].length - 1);
				float minV = x / (float) (c.masses.length - 1);
				float maxU = (y + 1) / (float) (c.masses[x].length - 1);
				float maxV = (x + 1) / (float) (c.masses.length - 1);

				PointMass3D mass = c.masses[x][y];
				vecPos(vb, mass.origPos, mass.pos, partialTicks).tex(minU, minV).endVertex();

				mass = c.masses[x + 1][y];
				vecPos(vb, mass.origPos, mass.pos, partialTicks).tex(minU, maxV).endVertex();

				mass = c.masses[x + 1][y + 1];
				vecPos(vb, mass.origPos, mass.pos, partialTicks).tex(maxU, maxV).endVertex();

				mass = c.masses[x][y + 1];
				vecPos(vb, mass.origPos, mass.pos, partialTicks).tex(maxU, minV).endVertex();
			}
		}
		tess.draw();

		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

	private VertexBuffer vecPos(VertexBuffer vb, Vec3d lastTick, Vec3d pos, float partialTicks) {
		if (lastTick == null)
			lastTick = pos;
		if (pos == null)
			pos = lastTick;
		return vb.pos(
				lastTick.xCoord + ((pos.xCoord - lastTick.xCoord) * partialTicks),
				lastTick.yCoord + ((pos.yCoord - lastTick.yCoord) * partialTicks),
				lastTick.zCoord + ((pos.zCoord - lastTick.zCoord) * partialTicks)
		);
	}
}
