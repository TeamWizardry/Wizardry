package com.teamwizardry.wizardry.client.fx.particle;

import com.teamwizardry.wizardry.Wizardry;
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
    public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "particles/sparkle");
    private double jitterX, jitterY, jitterZ;
    private int jitterChance;
    private boolean fadeOut = true, randomSizes = false;

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age, boolean fadeOut) {
        super(worldIn, x, y, z);
        particleAlpha = alpha;
        this.fadeOut = fadeOut;
        particleMaxAge = age;
        particleScale = scale;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ, boolean fadeOut) {
        super(worldIn, x + ThreadLocalRandom.current().nextDouble(-rangeX, rangeX), y + ThreadLocalRandom.current().nextDouble(-rangeY, rangeY), z + ThreadLocalRandom.current().nextDouble(-rangeZ, rangeZ));
        particleAlpha = alpha;
        this.fadeOut = fadeOut;
        particleMaxAge = age;
        particleScale = scale;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public void randomDirection(double x, double y, double z) {
        if (x > 0) motionX += ThreadLocalRandom.current().nextDouble(-x, x);
        if (y > 0) motionY += ThreadLocalRandom.current().nextDouble(-y, y);
        if (z > 0) motionZ += ThreadLocalRandom.current().nextDouble(-z, z);
    }

    public void setMotion(double x, double y, double z) {
        motionX += x;
        motionY += y;
        motionZ += z;
    }

    public void jitter(int chance, double x, double y, double z) {
        jitterChance = chance;
        if (x > 0) jitterX = x;
        if (y > 0) jitterY = y;
        if (z > 0) jitterZ = z;
    }

    public void setColor(int r, int g, int b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }

    public void randomlyOscillateColor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            double fraction = ThreadLocalRandom.current().nextDouble(0.25, 1 / 0.25);
            particleRed = (int) Math.round(Math.min(255, particleRed + 255 * fraction));
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            double fraction = ThreadLocalRandom.current().nextDouble(0.25, 1 / 0.25);
            particleGreen = (int) Math.round(Math.min(255, particleGreen + 255 * fraction));
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            double fraction = ThreadLocalRandom.current().nextDouble(0.25, 1 / 0.25);
            particleBlue = (int) Math.round(Math.min(255, particleBlue + 255 * fraction));
        }
    }

    public void randomizeColor(int minRange, int maxRange) {
        this.particleRed = ThreadLocalRandom.current().nextInt(minRange, maxRange);
        this.particleGreen = ThreadLocalRandom.current().nextInt(minRange, maxRange);
        this.particleBlue = ThreadLocalRandom.current().nextInt(minRange, maxRange);
    }

    public void setRandomizedSizes(boolean randomizedSizes) {
        this.randomSizes = randomizedSizes;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (randomSizes) particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        if (jitterX > 0)
            if (random.nextInt(jitterChance) == 0) motionX += ThreadLocalRandom.current().nextDouble(-jitterX, jitterX);
        if (jitterY > 0)
            if (random.nextInt(jitterChance) == 0) motionY += ThreadLocalRandom.current().nextDouble(-jitterY, jitterY);
        if (jitterZ > 0)
            if (random.nextInt(jitterChance) == 0) motionZ += ThreadLocalRandom.current().nextDouble(-jitterZ, jitterZ);
        float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;
        if (random.nextInt(4) == 0) this.particleAge--;
        if (fadeOut) this.particleAlpha = lifeCoeff / 2;
        this.particleScale = lifeCoeff / 2;
    }
}