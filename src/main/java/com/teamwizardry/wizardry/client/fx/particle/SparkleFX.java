package com.teamwizardry.wizardry.client.fx.particle;

import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 5/8/2016.
 */
public class SparkleFX extends Particle {

    public ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "particles/sparkle");
    public ResourceLocation texture_blurred = new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred");
    /**
     * The colors to interpolate between.
     */
    public Color toLerp, fromLerp;
    /**
     * Will determine if the particle should transition between 2 colors
     * (toLerp and fromLerp)
     */
    private boolean lerp = false;
    /**
     * The amount of motion to set the particle to setJitter to.
     * It will randomize between the negative and positive values of each axis.
     */
    private Vec3d jitter = new Vec3d(0, 0, 0);

    /**
     * Will determine if the particle will setJitter/change direction every tick.
     */
    private int jitterChance = -1;

    /**
     * The chance for a particle's life to decrease per tick.
     */
    private int decayChance = -1;

    /**
     * Used to control shrink & grow. Ignore this.
     */
    private float defaultScale = 1f;

    /**
     * Used to control fadeIn & fadeOut. Ignore this.
     */
    private float defaultAlpha = 1f;

    /**
     * Will determine if the particle should start from alpha 0 to 1,
     * and if it should end with alpha from 1 to 0.
     */
    private boolean fadeOut = false, fadeIn = false;

    /**
     * Will determine if the particle should start from scale 0 to 1,
     * and if it should end with scale from 1 to 0.
     */
    private boolean shrink = false, grow = false;

    public SparkleFX(World worldIn, Vec3d origin) {
        super(worldIn, origin.xCoord, origin.yCoord, origin.zCoord);
        particleMaxAge = 50;
        particleScale = 0f;
        particleAlpha = 0f;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    public SparkleFX(World worldIn, Vec3d origin, Vec3d range) {
        super(worldIn, origin.xCoord + ThreadLocalRandom.current().nextDouble(-range.xCoord, range.xCoord), origin.yCoord + ThreadLocalRandom.current().nextDouble(-range.yCoord, range.yCoord), origin.zCoord + ThreadLocalRandom.current().nextDouble(-range.zCoord, range.zCoord));
        particleMaxAge = 50;
        particleScale = 0f;
        particleAlpha = 0f;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    /**
     * Will make the particle fly in a random direction of magnitude x, y, and z.
     *
     * @param x The maximum possible magnitude the particle might fly through along the X axis.
     * @param y The maximum possible magnitude the particle might fly through along the Y axis.
     * @param z The maximum possible magnitude the particle might fly through along the Z axis.
     */
    public void setRandomDirection(double x, double y, double z) {
        if (x > 0) motionX = ThreadLocalRandom.current().nextDouble(-x, x);
        if (y > 0) motionY = ThreadLocalRandom.current().nextDouble(-y, y);
        if (z > 0) motionZ = ThreadLocalRandom.current().nextDouble(-z, z);
    }

    /**
     * Will set the motion of the particle to the specified magnitudes x, y, and z.
     *
     * @param x The magnitude the particle should set it's direction to along the X axis.
     * @param y The magnitude the particle should set it's direction to along the Y axis.
     * @param z The magnitude the particle should set it's direction to along the Z axis.
     */
    public void setMotion(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    /**
     * Will add to motion of the particle with the specified magnitudes x, y, and z.
     *
     * @param x The magnitude the particle should add to it's direction along the X axis.
     * @param y The magnitude the particle should add to it's direction along the Y axis.
     * @param z The magnitude the particle should add to it's direction along the Z axis.
     */
    public void addMotion(double x, double y, double z) {
        motionX += x;
        motionY += y;
        motionZ += z;
    }

    /**
     * Will make the particle shift/change direction randomly along x, y, and z.
     *
     * @param chance Will determine if the particle will setJitter/change direction every tick.
     * @param x      Will set the amount to possibly change directions to along the X axis.
     * @param y      Will set the amount to possibly change directions to along the Y axis.
     * @param z      Will set the amount to possibly change directions to along the Z axis.
     */
    public void setJitter(int chance, double x, double y, double z) {
        if (chance <= 0) jitterChance = 10;
        else jitterChance = chance;

        double jitX = 0, jitY = 0, jitZ = 0;
        if (x >= 0.0) jitX = x;
        if (y >= 0.0) jitY = y;
        if (z >= 0.0) jitZ = z;
        jitter = new Vec3d(jitX, jitY, jitZ);
    }

    /**
     * Will set the Color of the particle.
     *
     * @param color The Color to set the particle to.
     */
    public void setColor(Color color) {
        particleRed = color.r;
        particleGreen = color.g;
        particleBlue = color.b;
    }

    /**
     * Will interpolate/slowly change the color of the particle to the specified color.
     *
     * @param toLerp The color to slowly change to.
     */
    public void setLerp(Color toLerp) {
        this.lerp = true;
        this.toLerp = toLerp;
        this.fromLerp = new Color(particleRed, particleGreen, particleBlue);
    }

    /**
     * Will use a blurred texture of the particle.
     */
    public void setBlurred() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_blurred.toString());
        this.setParticleTexture(sprite);
    }

    /**
     * Will set the particle to a color that's similar or close to the default current color set.
     *
     * @param range Will set the maximum/minimum color range to shift to.
     * @param r     Will shift the color slightly on the red range.
     * @param g     Will shift the color slightly on the green range.
     * @param b     Will shift the color slightly on the blue range.
     */
    public void setRandomlyShiftColor(double range, boolean r, boolean g, boolean b) {
        if (range <= 0) range = 0.2;
        if (r && ThreadLocalRandom.current().nextBoolean()) {
            double rand = ThreadLocalRandom.current().nextDouble(-range, range);
            if (particleRed + rand > 1) particleRed = 1;
            else if (particleRed + rand < 0) particleRed = 0;
            else particleRed += rand;
        }

        if (g && ThreadLocalRandom.current().nextBoolean()) {
            double rand = ThreadLocalRandom.current().nextDouble(-range, range);
            if (particleGreen + rand > 1) particleGreen = 1;
            else if (particleGreen + rand < 0) particleGreen = 0;
            else particleGreen += rand;
        }
        if (b && ThreadLocalRandom.current().nextBoolean()) {
            double rand = ThreadLocalRandom.current().nextDouble(-range, range);
            if (particleBlue + rand > 1) particleBlue = 1;
            else if (particleBlue + rand < 0) particleBlue = 0;
            else particleBlue += rand;
        }
    }

    /**
     * Will shrink the particle's size slowly before it dies.
     */
    public void setShrink() {
        shrink = true;
    }

    /**
     * Will expand the particle's size slowly when it spawns.
     */
    public void setGrow() {
        grow = true;
        particleScale = 0f;
    }

    /**
     * Will spawn the particle from alpha 0 and slowly shift to alpha 1.
     */
    public void setFadeIn() {
        fadeIn = true;
        particleAlpha = 0f;
    }

    /**
     * Will end the particle from alpha 1 and slowly shift to alpha 0.
     */
    public void setFadeOut() {
        fadeOut = true;
    }

    /**
     * Will randomize the size of the particle.
     *
     * @param range The minimum or maximum possible scale to use when randomly choosing the particle's size.
     */
    public void setRandomSize(double range) {
        if (range <= 0) range = 1;
        this.defaultScale = 1 + (float) ThreadLocalRandom.current().nextDouble(-range, range);
    }

    /**
     * Will set the transparency of the particle.
     *
     * @param alpha The amount of transparency such that alpha is a float between [0, 1].
     */
    public void setAlpha(float alpha) {
        particleAlpha = alpha;
        defaultAlpha = alpha;
    }

    /**
     * Will set the size of the particle.
     *
     * @param scale The particle's set such that scale is a float between [0, 1].
     */
    public void setScale(float scale) {
        particleScale = scale;
        defaultScale = scale;
    }

    /**
     * Will set the particle to a randomized age within the range.
     *
     * @param minAge The minimum age the particle is allowed to survive in.
     * @param maxAge The maximum age the particle is allowed to survive in.
     */
    public void setRandomizedAge(int minAge, int maxAge) {
        if (minAge > 0 && maxAge > minAge) particleMaxAge = ThreadLocalRandom.current().nextInt(minAge, maxAge);
        else particleMaxAge = 50;
    }

    /**
     * This is an alternative to setRandomizedAge.
     * This method will set a chance that will determine if the particle should decay in
     * age every tick.
     *
     * @param decayChance The chance for a particle's life to decrease per tick.
     */
    public void setChanceOfDecay(int decayChance) {
        if (decayChance < 0) decayChance = 4;
        this.decayChance = decayChance;
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

        float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;

        if (lerp) {
            float t = (float) particleAge / (float) particleMaxAge;
            particleRed = (float) ((1.0 - t) * fromLerp.r + t * toLerp.r);
            particleGreen = (float) ((1.0 - t) * fromLerp.g + t * toLerp.g);
            particleBlue = (float) ((1.0 - t) * fromLerp.b + t * toLerp.b);
        }

        if (jitterChance > 0) {
            if (rand.nextInt(jitterChance) == 0 && jitter.xCoord > 0)
                motionX = ThreadLocalRandom.current().nextDouble(-jitter.xCoord, jitter.xCoord);
            if (rand.nextInt(jitterChance) == 0 && jitter.yCoord > 0)
                motionY = ThreadLocalRandom.current().nextDouble(-jitter.yCoord, jitter.yCoord);
            if (rand.nextInt(jitterChance) == 0 && jitter.zCoord > 0)
                motionZ = ThreadLocalRandom.current().nextDouble(-jitter.zCoord, jitter.zCoord);
        }

        // TODO: Make better math here
        if (particleAge < particleMaxAge / 2) {
            if (grow && particleScale < defaultScale) particleScale += 0.05;
            if (fadeIn && particleAlpha < defaultAlpha) particleAlpha += 0.05;
        } else {
            if (shrink && particleScale > 0) particleScale = lifeCoeff / 2;
            if (fadeOut && particleScale > 0) particleAlpha = lifeCoeff / 2;
        }
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

    /**
     * Will set the maximum age the particle will live for.
     *
     * @param maxAge The maximum age the particle will live for.
     */
    public void setMaxAge(int maxAge) {
        particleMaxAge = maxAge;
    }
}