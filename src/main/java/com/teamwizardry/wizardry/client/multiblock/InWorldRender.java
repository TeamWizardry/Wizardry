package com.teamwizardry.wizardry.client.multiblock;

import com.teamwizardry.wizardry.api.util.gui.Color;
import com.teamwizardry.wizardry.client.multiblock.vanillashade.Template;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public enum InWorldRender {
	INSTANCE;
	
	protected BlockPos pos;
	protected Rotation rot;
	protected Structure structure;
	protected int[] verts;
	protected StructureMatchResult match;

	InWorldRender() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void worldLast(RenderWorldLastEvent event) {
		if(verts == null)
			return;
		
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		
		GlStateManager.enableBlend();
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		GlStateManager.translate(-x, -y, -z);
		
		GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		vb.addVertexData(verts);
		tessellator.draw();
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
	
	@SubscribeEvent
	public void blockPlace(BlockEvent.PlaceEvent event) {
		if(match == null)
			return;
		BlockPos pos = worldToStructure(event.getPos());
		if(match.allErrors.contains(pos))
			refreshVerts();
	}
	
	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {
		if(match == null)
			return;
		BlockPos pos = worldToStructure(event.getPos());
		if(match.matches.contains(pos) || match.allErrors.contains(pos))
			refreshVerts();
	}
	
	private BlockPos worldToStructure(BlockPos pos) {
		return Template.inverseTransformedBlockPos(pos.subtract(this.pos), Mirror.NONE, match.rotation).add(structure.origin);
	}
	
	public void unsetStructure() {
		structure = null;
		pos = null;
		verts = null;
		match = null;
	}
	
	public void setStructure(Structure structure, BlockPos pos) {
		this.structure = structure;
		this.pos = pos;
		this.refreshVerts();
	}
	
	protected void refreshVerts() {
		StructureMatchResult match = structure.match(Minecraft.getMinecraft().theWorld, pos);
		this.match = match;
		rot = structure.matchedRotation;
		structure.getBlockAccess().addOverride(structure.getOrigin(), Blocks.AIR.getDefaultState());
		
		for (BlockPos pos : match.matches) {
			structure.getBlockAccess().addOverride(pos, Blocks.AIR.getDefaultState());
		}
		for (BlockPos pos : match.nonAirErrors) {
			structure.getBlockAccess().addOverride(pos, Blocks.AIR.getDefaultState());
		}
		for (BlockPos pos : match.propertyErrors) {
			structure.getBlockAccess().addOverride(pos, Blocks.AIR.getDefaultState());
		}
		
		verts = StructureRenderUtil.render(structure, (check) -> true, (check) -> EnumFacing.values(), new Color(1, 1, 1, 0.75f), 0.75f);
		
		structure.getBlockAccess().clearOverrides();
	}
	
}
