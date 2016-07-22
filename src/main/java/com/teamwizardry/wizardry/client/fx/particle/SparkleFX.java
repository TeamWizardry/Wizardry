package com.teamwizardry.wizardry.client.fx.particle;

import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
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
    public ResourceLocation texture_blurred = new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred");
    public Color toLerp, fromLerp;
    private double jitterX, jitterY, jitterZ;
    private int jitterChance;
    private boolean fadeOut = true, randomSizes = false, lerp = false;

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age, boolean fadeOut) {
        super(worldIn, x, y, z);
        particleAlpha = alpha;
        this.fadeOut = fadeOut;
        particleMaxAge = age * Config.particlePercentage / 100;
        particleScale = scale;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public SparkleFX(World worldIn, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ, boolean fadeOut) {
        super(worldIn, x + ThreadLocalRandom.current().nextDouble(-rangeX, rangeX), y + ThreadLocalRandom.current().nextDouble(-rangeY, rangeY), z + ThreadLocalRandom.current().nextDouble(-rangeZ, rangeZ));
        particleAlpha = alpha;
        this.fadeOut = fadeOut;
        particleMaxAge = age * Config.particlePercentage / 100;
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

    public void setColor(float r, float g, float b) {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }

    public void lerp(Color toLerp) {
        this.lerp = true;
        this.toLerp = toLerp;
        this.fromLerp = new Color(particleRed, particleGreen, particleBlue);
    }

    public void blur() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_blurred.toString());
        this.setParticleTexture(sprite);
    }

    public void randomlyOscillateColor(boolean r, boolean g, boolean b) {
        if (r && ThreadLocalRandom.current().nextBoolean()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleRed + rand > 1) particleRed = 1;
                else particleRed += rand;
            } else {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleRed - rand < 0) particleRed = 0;
                else particleRed -= rand;
            }
        }

        if (b && ThreadLocalRandom.current().nextBoolean()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleBlue + rand > 1) particleBlue = 1;
                else particleBlue += rand;
            } else {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleBlue - rand < 0) particleBlue = 0;
                else particleBlue -= rand;
            }
        }
        if (g && ThreadLocalRandom.current().nextBoolean()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleGreen + rand > 1) particleGreen = 1;
                else particleGreen += rand;
            } else {
                double rand = ThreadLocalRandom.current().nextDouble(0, 1);
                if (particleGreen - rand < 0) particleGreen = 0;
                else particleGreen -= rand;
            }
        }
    }

    public void randomizeColor(int minRange, int maxRange) {
        this.particleRed = ThreadLocalRandom.current().nextInt(minRange, maxRange);
        this.particleGreen = ThreadLocalRandom.current().nextInt(minRange, maxRange);
        this.particleBlue = ThreadLocalRandom.current().nextInt(minRange, maxRange);
    }

    public void randomizeSizes() {
        this.randomSizes = true;
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

        if (lerp) {
            float t = (float) particleAge / (float) particleMaxAge;
            particleRed = (float) ((1.0 - t) * fromLerp.r + t * toLerp.r);
            particleGreen = (float) ((1.0 - t) * fromLerp.g + t * toLerp.g);
            particleBlue = (float) ((1.0 - t) * fromLerp.b + t * toLerp.b);
        }

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

    public double getX() {
        return posX;
    }

    public void setX(double x) {
        this.posX = x;
    }

    public double getY() {
        return posY;
    }

    public void setY(double y) {
        this.posY = y;
    }

    public double getZ() {
        return posZ;
    }

    public void setZ(double z) {
        this.posZ = z;
    }

    public int getAge() {
        return particleAge;
    }

    public int getMaxAge() {
        return particleMaxAge;
    }

    public void setFadeOut(boolean fadeOut) {
        this.fadeOut = fadeOut;
    }
}