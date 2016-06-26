package com.teamwizardry.wizardry.gui.book.util;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockRenderUtils {    
	public static void transferVB(VertexBuffer from, VertexBuffer to) {
		to.addVertexData(from.getByteBuffer().asIntBuffer().array());
	}

	public static void renderQuadsToBuffer(List<BakedQuad> quads, IBlockState state, IBlockAccess access, BlockPos pos, BlockPos renderPos, VertexBuffer buf, float red, float green, float blue, float brightness) {
        for (int i = 0; i < quads.size(); i++)
        {
            BakedQuad bakedquad = quads.get(i);
            buf.addVertexData(bakedquad.getVertexData());
            
            if (bakedquad.hasTintIndex())
            {
            	int l = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, access, pos, bakedquad.getTintIndex());

                float f = (float)(l >> 16 & 255) / 255.0F;
                float f1 = (float)(l >> 8 & 255) / 255.0F;
                float f2 = (float)(l & 255) / 255.0F;
                
                buf.putColorMultiplier(f, f1, f2, 4);
                buf.putColorMultiplier(f, f1, f2, 3);
                buf.putColorMultiplier(f, f1, f2, 2);
                buf.putColorMultiplier(f, f1, f2, 1);
            	
//                buf.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                buf.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i normal = bakedquad.getFace().getDirectionVec();
            buf.putNormal((float)normal.getX(), (float)normal.getY(), (float)normal.getZ());
            buf.putPosition(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        }
	}
	
	public static void renderBlockToVB(IBlockState state, IBlockAccess access, BlockPos pos, BlockPos renderPos, VertexBuffer buffer, float red, float green, float blue, float brightness) {
		state = state.getActualState(access, pos);
		
		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
		
		for (EnumFacing enumfacing : EnumFacing.values())
        {
			if(!state.shouldSideBeRendered(access, pos, enumfacing))
				continue;
			renderQuadsToBuffer(model.getQuads(state, enumfacing, 0L), state, access, pos, renderPos, buffer, red, green, blue, brightness);
        }

		renderQuadsToBuffer(model.getQuads(state, null, 0L), state, access, pos, renderPos, buffer, red, green, blue, brightness);
        
		
	}
}
