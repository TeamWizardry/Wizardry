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
    private double jitterX, jitterY, jitterZ;
    private int jitterChance;

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age) {
        super(worldIn, x, y, z);
        particleAlpha = alpha;
        particleMaxAge = age;
        particleScale = scale;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ) {
        super(worldIn, x + ThreadLocalRandom.current().nextDouble(-rangeX, rangeX), y + ThreadLocalRandom.current().nextDouble(-rangeY, rangeY), z + ThreadLocalRandom.current().nextDouble(-rangeZ, rangeZ));
        particleAlpha = alpha;
        particleMaxAge = age;
        particleScale = scale;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public void randomDirection(double x, double y, double z) {
        motionX += ThreadLocalRandom.current().nextDouble(-x, x);
        motionY += ThreadLocalRandom.current().nextDouble(-y, y);
        motionZ += ThreadLocalRandom.current().nextDouble(-z, z);
    }

    public void jitter(int chance, double x, double y, double z) {
        jitterChance = chance;
        jitterX = x;
        jitterY = y;
        jitterZ = z;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (random.nextInt(jitterChance) == 0) motionX += ThreadLocalRandom.current().nextDouble(-jitterX, jitterX);
        if (random.nextInt(jitterChance) == 0) motionY += ThreadLocalRandom.current().nextDouble(-jitterY, jitterY);
        if (random.nextInt(jitterChance) == 0) motionZ += ThreadLocalRandom.current().nextDouble(-jitterZ, jitterZ);
        float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;
        if (random.nextInt(4) == 0) this.particleAge--;
        this.particleAlpha = lifeCoeff / 2;
        this.particleScale = lifeCoeff / 2;
    }
}