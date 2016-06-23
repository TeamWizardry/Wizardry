package me.lordsaad.wizardry.particles;

import me.lordsaad.wizardry.Matrix4;
import me.lordsaad.wizardry.shader.ShaderCallback;
import me.lordsaad.wizardry.shader.ShaderHelper;
import me.lordsaad.wizardry.shader.shaders.BurstShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Random;

public class MagicBurstFX extends QueuedParticle {

    private static ParticleRenderQueue<MagicBurstFX> QUEUE = new ParticleRenderQueue<MagicBurstFX>() {

        @Override
        public String name() {
            return "magic-burst";
        }

        @Override
        public void dispatchQueuedRenders(Tessellator tessellator) {
            if (renderQueue.isEmpty())
                return;
            GlStateManager.disableTexture2D();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            ShaderHelper.useShader(ShaderHelper.burst, new ShaderCallback<BurstShader>() {
                @Override
                public void call(BurstShader shader) {
//					int count = ARBShaderObjects.glGetUniformLocationARB(shader.getGlName(), "rayFade");
//					ARBShaderObjects.glUniform1iARB(count, 2);
                    if (shader.count != null)
                        shader.count.set(5);
//					shader.count.set(10);
                    if (shader.rotationSpeed != null)
                        shader.rotationSpeed.set(3);
//					shader.glowColor.set(1, 1, 1, 1);
//					shader.centerColor.set(0, 1, 1, 1);
//					shader.rayFade.set(2);
//					shader.glowFade.set(0.5);
//					shader.lengthRandomness.set(0.4);
//					shader.centerRadius.set(0.05);
                }
            });
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (MagicBurstFX fx : renderQueue) {
                fx.renderFlat(tessellator.getBuffer());
            }
            tessellator.draw();
            ShaderHelper.releaseShader();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableTexture2D();
            renderQueue.clear();
        }
    };
    protected int COUNT = 1;
    protected int RAND_COUNT = 4 * COUNT;
    protected FloatBuffer rands = BufferUtils.createFloatBuffer(16);

    public MagicBurstFX(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn);
        setMaxAge(2000);
        particleRed = 1;//(float)Math.random();
        particleGreen = 0.5f;//(float)Math.random();
        particleBlue = 0;//(float)Math.random();
        particleGravity = 0.1f;
//		RAND_COUNT = 1+ (int)( Math.random()*100 );
    }

    @Override
    protected ParticleRenderQueue queue() {
        return QUEUE;
    }

    public void renderFlat(VertexBuffer buffer) {
        float minU = 10 + RAND_COUNT;
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

        float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int light = this.getBrightnessForRender(partialTicks);
        int lightU = light >> 16 & 65535;
        int lightV = light & 65535;

        buffer.pos((double) (posX - rotationX * size - rotationXY * size), (double) (posY - rotationZ * size), (double) (posZ - rotationYZ * size - rotationXZ * size)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX - rotationX * size + rotationXY * size), (double) (posY + rotationZ * size), (double) (posZ - rotationYZ * size + rotationXZ * size)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX + rotationX * size + rotationXY * size), (double) (posY + rotationZ * size), (double) (posZ + rotationYZ * size + rotationXZ * size)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
        buffer.pos((double) (posX + rotationX * size - rotationXY * size), (double) (posY - rotationZ * size), (double) (posZ + rotationYZ * size - rotationXZ * size)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightU, lightV).endVertex();
    }

    public void render(VertexBuffer buffer) {
        Tessellator tessellator = Tessellator.getInstance();
        buffer = tessellator.getBuffer();

        float age = (this.particleAge + partialTicks) / this.particleMaxAge;
        float dying = 0.0F;

        if (age > 0.8F) {
            dying = (age - 0.8F) / 0.2F;
        }

        Random var6 = new Random(432L); // rand with specific seed

        float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.scale(0.1, 0.1, 0.1);
        float r = 1, g = 0.5f, b = 0, a = 1;

        for (int var7 = 0; var7 < 50.0F; ++var7) {
            GlStateManager.rotate(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F); // rand X
            GlStateManager.rotate(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F); // rand Y
            GlStateManager.rotate(var6.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F); // rand Z
            GlStateManager.rotate(var6.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F); // rand X
            GlStateManager.rotate(var6.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F); // rand Y
            GlStateManager.rotate(var6.nextFloat() * 360.0F + age * 90.0F, 0.0F, 0.0F, 1.0F); // rand Z + spin 90 deg along Z

            float var8 = var6.nextFloat() * 2.0F + 2.0F + dying * 0.5F;
            float var9 = var6.nextFloat() * 2.0F + 1.0F + dying * 2.0F;
            tessellator.getBuffer().begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();

            buffer.pos(-0.866D * var9, var8, -0.5F * var9).color(r, g, b, 0).endVertex();
            buffer.pos(0.866D * var9, var8, -0.5F * var9).color(r, g, b, 0).endVertex();
            buffer.pos(0.0D, var8, 1.0F * var9).color(r, g, b, 0).endVertex();
            buffer.pos(-0.866D * var9, var8, -0.5F * var9).color(r, g, b, 0).endVertex();
            tessellator.draw();
        }

        GlStateManager.popMatrix();
    }

    private VertexBuffer posT(VertexBuffer buf, Matrix4 transform, double x, double y, double z) {
        Vec3d vec = new Vec3d(x, y, z);
        vec = transform.apply(vec);

        return buf.pos(vec.xCoord, vec.yCoord, vec.zCoord);
    }

}
