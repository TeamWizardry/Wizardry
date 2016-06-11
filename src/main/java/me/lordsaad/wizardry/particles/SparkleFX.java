package me.lordsaad.wizardry.particles;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 5/8/2016.
 */
public class SparkleFX extends Particle {

    private static final Random random = new Random();
    public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "entity/sparkle");

    public SparkleFX(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        motionX += ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        motionY += ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        motionZ += ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        posX = x + ThreadLocalRandom.current().nextDouble(-10, 10);
        posY = y + ThreadLocalRandom.current().nextDouble(-10, 10);
        posZ = z + ThreadLocalRandom.current().nextDouble(-10, 10);
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
        // if (random.nextInt(10) == 0) motionX += ThreadLocalRandom.current().nextDouble(-0.03, 0.03);
        // if (random.nextInt(10) == 0) motionY += ThreadLocalRandom.current().nextDouble(-0.03, 0.03);
        // if (random.nextInt(10) == 0) motionZ += ThreadLocalRandom.current().nextDouble(-0.03, 0.03);
        float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;
        if (random.nextInt(4) == 0) this.particleAge--;
        this.particleAlpha = lifeCoeff;
        this.particleScale = lifeCoeff;
    }
}