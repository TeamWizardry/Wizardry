package com.teamwizardry.wizardry.multiblock;

import java.nio.IntBuffer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.wizardry.gui.book.util.BlockRenderUtils;
import com.teamwizardry.wizardry.gui.book.util.Color;
import com.teamwizardry.wizardry.multiblock.vanillashade.Template.BlockInfo;

public class StructureRenderUtil {

	private static VertexBuffer blockBuf = new VertexBuffer(50000);
	
	public static int[] render(Structure structure, Predicate<BlockPos> renderMask, Function<BlockPos, EnumFacing[]> sideDrawingOverrides, Color color, float brightness) {
		IBlockAccess access = structure.getBlockAccess();
		
		blockBuf.reset();
        blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        
        // solid blocks first
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
            if (state.getRenderType() == EnumBlockRenderType.INVISIBLE || !renderMask.test(info.pos))
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.SOLID)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a, sideDrawingOverrides.apply(info.pos));
        }
        
        // cutout blocks next
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
        	if (state.getRenderType() == EnumBlockRenderType.INVISIBLE || !renderMask.test(info.pos))
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT && state.getBlock().getBlockLayer() != BlockRenderLayer.CUTOUT_MIPPED)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a, sideDrawingOverrides.apply(info.pos));
        }
        
        // translucent blocks next
        for (BlockInfo info : structure.blockInfos()) {
        	IBlockState state = access.getBlockState(info.pos);
        	if (state.getRenderType() == EnumBlockRenderType.INVISIBLE || !renderMask.test(info.pos))
                continue;
            if (state.getBlock().getBlockLayer() != BlockRenderLayer.TRANSLUCENT)
            	continue;
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.getOrigin()), blockBuf, color.r, color.g, color.b, brightness, color.a, sideDrawingOverrides.apply(info.pos));
        }
        
        blockBuf.finishDrawing();

        IntBuffer intBuf = blockBuf.getByteBuffer().asIntBuffer();
        int[] bufferInts = new int[intBuf.limit()];
        for (int i = 0; i < bufferInts.length; i++) {
            bufferInts[i] = intBuf.get(i);
        }
        return bufferInts;
	}
	
}
