package com.teamwizardry.wizardry.gui.book.pages;

import java.nio.IntBuffer;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.wizardry.Logs;
import com.teamwizardry.wizardry.gui.book.util.BlockRenderUtils;
import com.teamwizardry.wizardry.gui.book.util.DataNode;
import com.teamwizardry.wizardry.multiblock.Structure;
import com.teamwizardry.wizardry.multiblock.vanillashade.Template.BlockInfo;

/**
 * Displays a structure
 * @author Pierce Corcoran
 */
public class GuiPageStructure extends GuiPageCommon {

	private static VertexBuffer blockBuf = new VertexBuffer(50000);
	private static int[] bufferInts;
	
    Structure structure;

    public GuiPageStructure(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
        super(parent, data, globalData, path, page);
        
        structure = new Structure(data.get("structure").asStringOr("nullStruct"));
        initStructure();
    }

    public void initStructure() {
    	List<BlockInfo> blockInfoList = structure.blockInfos();
    	blockBuf.reset();
    	blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
    	for (BlockInfo info : blockInfoList) {
    		if(info.blockState.getRenderType() == EnumBlockRenderType.INVISIBLE)
    			continue;
    		
    		BlockRenderUtils.renderBlockToVB(info.blockState, structure.getBlockAccess(), info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, 1, 1, 1, 1);
		}
    	blockBuf.finishDrawing();
    	
    	IntBuffer intBuf = blockBuf.getByteBuffer().asIntBuffer();
    	bufferInts = new int[intBuf.limit()];
    	for (int i = 0; i < bufferInts.length; i++) {
			bufferInts[i] = intBuf.get(i);
		}
    	Logs.debug("HI!");
    	
    }
    
    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
    	
    	Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	
    	Tessellator tessellator = Tessellator.getInstance();
    	VertexBuffer renderBuf = tessellator.getBuffer();
    	
//    	IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.STONE.getDefaultState());
//		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(model, 1.0F, 1, 1, 1);
//    	GlStateManager.per
    	
    	
    	GlStateManager.translate(this.viewWidth/2, this.viewHeight/2, 0);
    	
//    	RenderHelper.enableStandardItemLighting();
    	
    	{
    		Vec3d LIGHT0_POS = (new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    	    Vec3d LIGHT1_POS = (new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();
    	    
    		GlStateManager.enableLighting();
            GlStateManager.enableLight(0);
            GlStateManager.enableLight(1);
            GlStateManager.enableColorMaterial();
            GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
            
            float light = 1F;
            float ambiant = 0.75F;
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float)LIGHT0_POS.xCoord, (float)LIGHT0_POS.yCoord, (float)LIGHT0_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE,  RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT,  RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float)LIGHT1_POS.xCoord, (float)LIGHT1_POS.yCoord, (float)LIGHT1_POS.zCoord, 0.0f));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE,  RenderHelper.setColorBuffer(light, light, light, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT,  RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambiant, ambiant, ambiant, 1.0F));
    	}
    	
    	
    	double s = 10;
    	GlStateManager.scale(s, -s, s);
    	GlStateManager.translate(-0.5, -0.5, -0.5);
    	GlStateManager.rotate(22.5f, 1, 0, 0);
    	GlStateManager.rotate(45, 0, 1, 0);
    	
//    	RenderHelper.enableStandardItemLighting();

//    	Project.gluPerspective(0, (float)this.mc.displayWidth / (float)this.mc.displayHeight, 1.0F, 3000.0F);
    	renderBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
    	renderBuf.addVertexData(bufferInts);
    	//BlockRenderUtils.transferVB(blockBuf, renderBuf);
    	tessellator.draw();
    	
    	RenderHelper.disableStandardItemLighting();
    	
//        drawScaledCustomSizeModalRect((viewWidth / 2) - 50, 0, 0, 0, 100, 50, 100, 50, 100, 50);
    }

}
