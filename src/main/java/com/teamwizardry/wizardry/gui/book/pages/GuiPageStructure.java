package com.teamwizardry.wizardry.gui.book.pages;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;

import net.minecraftforge.fml.client.config.GuiButtonExt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.wizardry.Matrix4;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.gui.book.util.BlockRenderUtils;
import com.teamwizardry.wizardry.gui.book.util.DataNode;
import com.teamwizardry.wizardry.gui.lib.GuiEvent;
import com.teamwizardry.wizardry.gui.lib.TextureDefinition;
import com.teamwizardry.wizardry.gui.lib.components.GuiComponentButton;
import com.teamwizardry.wizardry.multiblock.Structure;
import com.teamwizardry.wizardry.multiblock.vanillashade.Template.BlockInfo;

/**
 * Displays a structure
 *
 * @author Pierce Corcoran
 */
public class GuiPageStructure extends GuiPageCommon {

	private static VertexBuffer blockBuf = new VertexBuffer(50000);
	private static int[] bufferInts;
	private static int[] transpBufferInts;
	private double rotX, rotY, rotZ;
	private Vec3d center = new Vec3d(-0.5, -0.5, -0.5);
    Structure structure;
    int dragStartX = -1, dragStartY = -1;
    double zoom;
    int dragButton = -1;
    
    GuiButton layerUp, layerDown;
    int layer = -1;
    
    public GuiPageStructure(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
        super(parent, data, globalData, path, page);

        structure = new Structure(data.get("structure").asStringOr("nullStruct"));
        rotX = 22;
        rotY = 45;
        rotZ = 0;
        zoom = 10;

        TextureDefinition def = new TextureDefinition(new ResourceLocation(Wizardry.MODID, "textures/gui/texturesheet/structure.png"), 128, 128, /*unused ->*/ 0, 0, 128, 128);
        
        components.add(new GuiComponentButton("up", 0, 0, 16, 8, def.sub(0, 0, 16, 8)));
        components.add(new GuiComponentButton("dn", 0, 8, 16, 8, def.sub(16, 0, 16, 8)));
        
        initStructure();
    }

    public void initStructure() {
        List<BlockInfo> blockInfoList = structure.blockInfos();
        blockBuf.reset();
        blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        EnumFacing[] alwaysSides = layer == -1 ? new EnumFacing[0] : new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };
        for (BlockInfo info : blockInfoList) {
            if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE)
                continue;
            if (layer != -1 && info.pos.getY() != layer)
            	continue;
            BlockRenderUtils.renderBlockToVB(info.blockState, structure.getBlockAccess(), info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, 1, 1, 1, 1, 1, alwaysSides);
        }
        blockBuf.finishDrawing();

        IntBuffer intBuf = blockBuf.getByteBuffer().asIntBuffer();
        bufferInts = new int[intBuf.limit()];
        for (int i = 0; i < bufferInts.length; i++) {
            bufferInts[i] = intBuf.get(i);
        }
        
        if(layer == -1) {
        	transpBufferInts = null;
        	return;
        }
        
        blockBuf.reset();
        blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        alwaysSides = layer == -1 ? new EnumFacing[0] : new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };
        for (BlockInfo info : blockInfoList) {
            if (info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE)
                continue;
            if (layer != -1 && info.pos.getY() >= layer)
            	continue;
            BlockRenderUtils.renderBlockToVB(info.blockState, structure.getBlockAccess(), info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, 1, 1, 1, 0.5f, 1, alwaysSides);
        }
        blockBuf.finishDrawing();

        intBuf = blockBuf.getByteBuffer().asIntBuffer();
        transpBufferInts = new int[intBuf.limit()];
        for (int i = 0; i < transpBufferInts.length; i++) {
        	transpBufferInts[i] = intBuf.get(i);
        }
    }
    
    @Override
    public void handle(GuiEvent event) {
    	super.handle(event);
    	if(event.component instanceof GuiComponentButton) {
    		String id = ((GuiComponentButton)event.component).id;
    		if("up".equals(id)) {
    			layer++;
    			initStructure();
    		}
    		if("dn".equals(id)) {
    			if(layer > -1) {
    				layer--;
    				initStructure();
    			}
    		}
    	}
    }
    
    @Override
    public void mouseScrollPage(int mouseX, int mouseY, int direction) {
    	if(direction > 0 && zoom < 100) {
    		zoom *= 1.5;
    	}
    	if(direction < 0 && zoom > 1) {
    		zoom /= 1.5;
    	}
    }
    
    @Override
    public void mouseClickedPage(int mouseX, int mouseY, int mouseButton) {
    	if(dragButton != -1)
    		return;
    	
    	dragStartX = mouseX;
    	dragStartY = mouseY;
    	dragButton = mouseButton;
    }

    @Override
    public void mouseReleasedPage(int mouseX, int mouseY, int mouseButton) {
    	if(mouseButton != dragButton)
    		return;
    	
    	if(dragButton == 0) { // left mouse button
    		rotY += (mouseX-dragStartX);
    		rotX += (mouseY-dragStartY);
    	}
    	if(dragButton == 1) {
    		Vec3d offset = new Vec3d(mouseX-dragStartX, mouseY-dragStartY, 0);
        	Matrix4 matrix = new Matrix4();
        	matrix.rotate(-Math.toRadians(rotZ), new Vec3d(0, 0, 1));
        	matrix.rotate(-Math.toRadians(rotY), new Vec3d(0, 1, 0));
        	matrix.rotate(-Math.toRadians(rotX), new Vec3d(1, 0, 0));
        	matrix.scale(new Vec3d(1.0/zoom, -1.0/zoom, 1.0/zoom));
        	offset = matrix.apply(offset);
        	center = center.add(offset);
    	}
    	
    	dragStartX = 0;
    	dragStartY = 0;
    	dragButton = -1;
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer renderBuf = tessellator.getBuffer();


        GlStateManager.translate(this.viewWidth / 2, this.viewHeight / 2, 500);
        
        float dragRotX = (mouseX-dragStartX);
    	if(dragButton != 0) dragRotX = 0;
    	float dragRotY = (mouseY-dragStartY);
    	if(dragButton != 0) dragRotY = 0;
    	
    	GlStateManager.enableRescaleNormal();
    	GlStateManager.scale(zoom, -zoom, zoom);
    	GlStateManager.rotate((float)rotX + dragRotY, 1, 0, 0);
    	GlStateManager.rotate((float)rotY + dragRotX, 0, 1, 0);
    	GlStateManager.rotate((float)rotZ, 0, 0, 1);
    	
    	Vec3d offset = Vec3d.ZERO;
    	if(dragButton == 1) {
    		offset = new Vec3d(mouseX-dragStartX, mouseY-dragStartY, 0);
        	Matrix4 matrix = new Matrix4();
        	matrix.rotate(-Math.toRadians(rotZ), new Vec3d(0, 0, 1));
        	matrix.rotate(-Math.toRadians(rotY), new Vec3d(0, 1, 0));
        	matrix.rotate(-Math.toRadians(rotX), new Vec3d(1, 0, 0));
        	matrix.scale(new Vec3d(1.0/zoom, -1.0/zoom, 1.0/zoom));
        	offset = matrix.apply(offset);
    	}
    	
    	
    	{ // RenderHelper.enableStandardItemLighting but brighter because of different light and ambiant values.
            Vec3d LIGHT0_POS = new Vec3d(0, 1,  0.1).normalize();//(new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
            Vec3d LIGHT1_POS = new Vec3d(0, 1, -0.1).normalize();//(new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

            GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
//            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
            GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

            float light = 0.3F;
            float ambiant = 0.7F;
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.xCoord, (float) LIGHT0_POS.yCoord, (float) LIGHT0_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.xCoord, (float) LIGHT1_POS.yCoord, (float) LIGHT1_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambiant, ambiant, ambiant, 1.0F));
    	}
    	
    	GlStateManager.translate(center.xCoord + offset.xCoord, center.yCoord + offset.yCoord, center.zCoord + offset.zCoord);
    	
    	renderBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
    	renderBuf.addVertexData(bufferInts);
    	tessellator.draw();
    	
    	if(transpBufferInts != null) {
	    	renderBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
	    	renderBuf.addVertexData(transpBufferInts);
	    	tessellator.draw();
    	}
    	
    	RenderHelper.disableStandardItemLighting();
    	GlStateManager.disableRescaleNormal();
//        drawScaledCustomSizeModalRect((viewWidth / 2) - 50, 0, 0, 0, 100, 50, 100, 50, 100, 50);
    }

}
