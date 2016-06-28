package com.teamwizardry.wizardry.api.util.gui;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import java.nio.ByteOrder;
import java.util.List;

public class BlockRenderUtils {
    public static void transferVB(VertexBuffer from, VertexBuffer to) {
        to.addVertexData(from.getByteBuffer().asIntBuffer().array());
    }

    public static void renderQuadsToBuffer(List<BakedQuad> quads, IBlockState state, IBlockAccess access, BlockPos pos, BlockPos renderPos, VertexBuffer buf, float red, float green, float blue, float brightness, float alpha) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad bakedquad = quads.get(i);
            buf.addVertexData(bakedquad.getVertexData());

            if (bakedquad.hasTintIndex()) {
                int l = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, access, pos, bakedquad.getTintIndex());

                float redMul = (float) (l >> 16 & 255) / 255.0F;
                float blueMul = (float) (l >> 8 & 255) / 255.0F;
                float greenMul = (float) (l & 255) / 255.0F;

                putColorMultiplier(buf, redMul*brightness, blueMul*brightness, greenMul*brightness, alpha, 4);
                putColorMultiplier(buf, redMul*brightness, blueMul*brightness, greenMul*brightness, alpha, 3);
                putColorMultiplier(buf, redMul*brightness, blueMul*brightness, greenMul*brightness, alpha, 2);
                putColorMultiplier(buf, redMul*brightness, blueMul*brightness, greenMul*brightness, alpha, 1);
            } else {
            	putRGBA_F4(buf, red*brightness, green*brightness, blue*brightness, alpha, 4);
            	putRGBA_F4(buf, red*brightness, green*brightness, blue*brightness, alpha, 3);
            	putRGBA_F4(buf, red*brightness, green*brightness, blue*brightness, alpha, 2);
            	putRGBA_F4(buf, red*brightness, green*brightness, blue*brightness, alpha, 1);
            }

            Vec3i normal = bakedquad.getFace().getDirectionVec();
            buf.putNormal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
            buf.putPosition(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        }
    }
    
    private static void putRGBA_F4(VertexBuffer buf, float red, float green, float blue, float alpha, int relIndex) {
    	int index = buf.getColorIndex(relIndex);
        int r = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
        int g = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
        int b = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
        int a = MathHelper.clamp_int((int)(alpha * 255.0F), 0, 255);
        buf.putColorRGBA(index, r, g, b, a);
    }
    
    private static void putColorMultiplier(VertexBuffer buf, float red, float green, float blue, float alpha, int p_178978_4_)
    {
        int index = buf.getColorIndex(p_178978_4_);
        int color = -1;

        color = buf.getByteBuffer().asIntBuffer().get(index);

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            int newRed = (int)((float)(color & 255) * red);
            int newGreen = (int)((float)(color >> 8 & 255) * green);
            int newBlue = (int)((float)(color >> 16 & 255) * blue);
            int newAlpha = (int)((float)(color >> 24 & 255) * alpha);
            
            color = newAlpha << 24 | newBlue << 16 | newGreen << 8 | newRed;
        }
        else
        {
            int newRed = (int)((float)(color >> 24 & 255) * red);
            int newGreen = (int)((float)(color >> 16 & 255) * green);
            int newBlue = (int)((float)(color >> 8 & 255) * blue);
            int newAlpha = (int)((float)(color & 255) * alpha);
            color = newRed << 24 | newGreen << 16 | newBlue << 8 | newAlpha;
        }

        buf.getByteBuffer().asIntBuffer().put(index, color);
    }

    public static void renderBlockToVB(IBlockState state, IBlockAccess access, BlockPos pos, BlockPos renderPos, VertexBuffer buffer, float red, float green, float blue, float brightness, float alpha, EnumFacing[] forceEnableSides) {
        state = state.getActualState(access, pos);

        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);

        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (!state.shouldSideBeRendered(access, pos, enumfacing))
                continue;
            renderQuadsToBuffer(model.getQuads(state, enumfacing, 0L), state, access, pos, renderPos, buffer, red, green, blue, brightness, alpha);
        }
        
        for (EnumFacing enumfacing : forceEnableSides) {
            if (!state.shouldSideBeRendered(access, pos, enumfacing)) // only render them if they haven't been rendered already.
                renderQuadsToBuffer(model.getQuads(state, enumfacing, 0L), state, access, pos, renderPos, buffer, red, green, blue, brightness, alpha);
        }

        renderQuadsToBuffer(model.getQuads(state, null, 0L), state, access, pos, renderPos, buffer, red, green, blue, brightness, alpha);


    }
}
