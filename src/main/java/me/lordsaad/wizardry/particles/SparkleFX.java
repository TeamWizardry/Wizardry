package me.lordsaad.wizardry.particles;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Saad on 5/8/2016.
 */
public class SparkleFX extends Particle {

    private static final Random random = new Random();
    public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "entity/sparkle");
    private double symmRange;
    private double xNext, yNext, zNext;
    private double x, y, z;

    public SparkleFX(World worldIn, double x, double y, double z, double xNext, double yNext, double zNext) {
        super(worldIn, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xNext = xNext;
        this.yNext = yNext;
        this.zNext = zNext;
        symmRange = -0.3;
        motionX = -0.3 + (0.3 + 0.3) * random.nextDouble();
        motionX = -0.3 + (0.3 + 0.3) * random.nextDouble();
        motionX = -0.3 + (0.3 + 0.3) * random.nextDouble();
        posX = x;
        posY = y;
        posZ = z;
        particleMaxAge = 10;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;
        if (random.nextInt(4) == 0) this.particleAge--;
        this.particleAlpha = lifeCoeff;
        this.particleScale = lifeCoeff;
    }
}