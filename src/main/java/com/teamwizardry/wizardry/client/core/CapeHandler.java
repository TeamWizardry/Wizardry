package com.teamwizardry.wizardry.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.librarianlib.ragdoll.cloth.Cloth;
import com.teamwizardry.librarianlib.ragdoll.cloth.Link;

public class CapeHandler {

	public static final CapeHandler INSTANCE = new CapeHandler();
	
	WeakHashMap<EntityLivingBase, Cloth> cloths = new WeakHashMap<>();
	
	private CapeHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END)
			return;
//		Matrix4 matrix = new Matrix4();
//		matrix.rotate(Math.toRadians(1), new Vec3d(0, 1, 0));
//		for (PointMass3D[] column : c.masses) {
//			for (PointMass3D point : column) {
//				if(!point.pin)
//					continue;
//				point.pos = matrix.apply(point.pos);
//			}
//		}
		
		List<EntityLivingBase> keysToRemove = new ArrayList<>();
		
		for (Entry<EntityLivingBase, Cloth> e : cloths.entrySet()) {
			EntityLivingBase entity = e.getKey();
			if(entity.isDead) {
				keysToRemove.add(entity);
				continue;
			}
			List<AxisAlignedBB> aabbs = entity.worldObj.getCollisionBoxes(entity.getEntityBoundingBox().expand(5, 5, 5));
			
			if(e.getValue().masses != null && e.getValue().masses[0] != null) {
				
				Vec3d[] shoulderPoints = new Vec3d[] {
						new Vec3d( 0.4, 1.5,  0),
						new Vec3d( 0.25, 1.5, -0.25),
						new Vec3d( 0,    1.5, -0.25),
						new Vec3d(-0.25, 1.5, -0.25),
						new Vec3d(-0.4, 1.5,  0)
				};
				
				Matrix4 matrix = new Matrix4();
				matrix.translate(entity.getPositionVector());
				matrix.translate(new Vec3d(entity.motionX, entity.motionY, entity.motionZ));
				matrix.rotate(Math.toRadians( entity.rotationYawHead), new Vec3d(0, -1, 0));
				
				for (int i = 0; i < shoulderPoints.length; i++) {
					shoulderPoints[i] = matrix.apply(shoulderPoints[i]);
				}
				
				for (int i = 0; i < e.getValue().masses[0].length && i < shoulderPoints.length; i++) {
					e.getValue().masses[0][i].pos = shoulderPoints[i];
				}
			}
			
			e.getValue().tick(aabbs);
		}
		
		for (EntityLivingBase entity : keysToRemove) {
			cloths.remove(entity);
		}
		
//		c.tick();
	}
	
	@SubscribeEvent
	public void damage(ItemTossEvent event) {
		if(event.getEntityItem().getEntityItem().getItem() == Items.BEEF) {
			cloths.clear();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public void drawPlayer(RenderLivingEvent.Post event) {
		if(!( event.getEntity() instanceof EntityVillager || event.getEntity() instanceof EntityPlayer))
			return;
		
		Vec3d[] shoulderPoints = new Vec3d[] {
				new Vec3d( 0.4, 1.5,  0),
				new Vec3d( 0.25, 1.5, -0.25),
				new Vec3d( 0,    1.5, -0.25),
				new Vec3d(-0.25, 1.5, -0.25),
				new Vec3d(-0.4, 1.5,  0)
		};
		
		Matrix4 matrix = new Matrix4();
		matrix.translate(event.getEntity().getPositionVector());
		matrix.rotate(Math.toRadians( event.getEntity().rotationYawHead), new Vec3d(0, -1, 0));
		
		for (int i = 0; i < shoulderPoints.length; i++) {
			shoulderPoints[i] = matrix.apply(shoulderPoints[i]);
		}
		
		if(!cloths.containsKey(event.getEntity())) {
			cloths.put(event.getEntity(), new Cloth(
					shoulderPoints,
					10,
					new Vec3d(0, 0.2, 0)
			) );
		}
		Cloth c = cloths.get(event.getEntity());
		
//		for (int i = 0; i < c.masses[0].length && i < shoulderPoints.length; i++) {
//			c.masses[0][i].pos = shoulderPoints[i];
//		}
		
		
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer vb = tess.getBuffer();
		
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.translate(event.getX(), event.getY(), event.getZ());
		GlStateManager.translate(-event.getEntity().posX, -event.getEntity().posY, -event.getEntity().posZ);
		GlStateManager.color(1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("Nulleynull"));
//		GlStateManager.disableLighting();
//		GlStateManager.disableTexture2D();
		
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		for (Link link : c.links) {
			vb.pos(
					link.a.prevPos.xCoord, 
					link.a.prevPos.yCoord, 
					link.a.prevPos.zCoord).endVertex();
			vb.pos(link.b.pos.xCoord, link.b.pos.yCoord, link.b.pos.zCoord).endVertex();
		}
		tess.draw();
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}
