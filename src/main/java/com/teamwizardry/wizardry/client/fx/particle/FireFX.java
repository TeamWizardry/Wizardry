package com.teamwizardry.wizardry.client.fx.particle;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by saad4 on 22/7/2016.
 */
public class FireFX extends Particle {

    public ResourceLocation fire = new ResourceLocation(Wizardry.MODID, "particles/sprite_sheet");

    public FireFX(World world, Vec3d pos, int age, double maxRange) {
        super(world, pos.xCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange), pos.yCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange), pos.zCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange));
        particleAlpha =  1f;
        particleMaxAge = age * Config.particlePercentage / 100;
        particleScale = 1f;

        motionX = 0;
        motionY = 0;
        motionZ = 0;

        // Random lens flare texture
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fire.toString());
        this.setParticleTexture(sprite);
        this.setParticleTextureIndex(ThreadLocalRandom.current().nextInt(5));
    }

    public void setMotion(double x, double y, double z) {
        motionX += x;
        motionY += y;
        motionZ += z;
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
        if (ThreadLocalRandom.current().nextInt(4) == 0) this.particleAge--;
        this.particleAlpha = lifeCoeff / 2;
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
}
