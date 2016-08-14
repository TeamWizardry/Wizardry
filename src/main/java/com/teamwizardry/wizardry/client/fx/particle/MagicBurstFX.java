package com.teamwizardry.wizardry.client.fx.particle;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.fx.particle.ParticleRenderQueue;
import com.teamwizardry.librarianlib.fx.particle.QueuedParticle;
import com.teamwizardry.librarianlib.fx.shader.ShaderHelper;
import com.teamwizardry.wizardry.client.fx.Shaders;

public class MagicBurstFX extends QueuedParticle {

    private static ParticleRenderQueue<MagicBurstFX> QUEUE = new ParticleRenderQueue<MagicBurstFX>(true) {

        @Override
        public String name() {
            return "magic-burst";
        }

        @Override
        public void renderParticles(Tessellator tessellator) {
            if (getRenderQueue().isEmpty())
                return;
            GlStateManager.disableTexture2D();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            ShaderHelper.Companion.useShader(Shaders.burst, shader -> {
            });
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (MagicBurstFX fx : getRenderQueue()) {
                fx.render(tessellator.getBuffer());
            }
            tessellator.draw();
            ShaderHelper.Companion.releaseShader();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableTexture2D();
            getRenderQueue().clear();
        }
    };
    
    protected float rand;

    public MagicBurstFX(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn);
        setMaxAge(2000);
        particleRed = (float)Math.random();
        particleGreen = (float)Math.random();
        particleBlue = (float)Math.random();
        particleGravity = 0.1f;
		rand = (int)( Math.random()*1000 );
    }

    @Override
    protected ParticleRenderQueue queue() {
        return QUEUE;
    }

    public void render(VertexBuffer buffer) {
        float minU = 1 + rand;
        float maxU = minU + 1;
        float minV = 0;
        float maxV = 1;
        float size = 0.4F * this.particleScale;

        if (this.particleTexture != null) {
            minU = this.particleTexture.getMinU();
            maxU = this.particleTexture.getMaxU();
            minV = this.particleTexture.getMinV();
            maxV = this.particleTexture.getMaxV();
        }

        float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) getPartialTicks() - interpPosX);
        float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) getPartialTicks() - interpPosY);
        float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) getPartialTicks() - interpPosZ);
        int light = this.getBrightnessForRender(getPartialTicks());
        int lightU = light >> 16 & 65535;
        int lightV = light & 65535;

        buffer.pos((double) (posX - getRotationX() * size - getRotationXY() * size), (double) (posY - getRotationZ() * size), (double) (posZ - getRotationYZ() * size - getRotationXZ() * size)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX - getRotationX() * size + getRotationXY() * size), (double) (posY + getRotationZ() * size), (double) (posZ - getRotationYZ() * size + getRotationXZ() * size)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX + getRotationX() * size + getRotationXY() * size), (double) (posY + getRotationZ() * size), (double) (posZ + getRotationYZ() * size + getRotationXZ() * size)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX + getRotationX() * size - getRotationXY() * size), (double) (posY - getRotationZ() * size), (double) (posZ + getRotationYZ() * size - getRotationXZ() * size)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
    }

}
