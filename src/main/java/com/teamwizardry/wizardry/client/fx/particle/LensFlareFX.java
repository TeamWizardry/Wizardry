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
 * Created by saad4 on 21/7/2016.
 */
public class LensFlareFX extends Particle {

    public ResourceLocation texture_hexagon = new ResourceLocation(Wizardry.MODID, "particles/hexagon");
    public ResourceLocation texture_hexagon_blur_1 = new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_1");
    public ResourceLocation texture_hexagon_blur_2 = new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_2");
    public ResourceLocation texture_hexagon_blur_3 = new ResourceLocation(Wizardry.MODID, "particles/hexagon_blur_3");
    public ResourceLocation texture_octagon = new ResourceLocation(Wizardry.MODID, "particles/octagon");
    public ResourceLocation texture_octagon_blur_1 = new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_1");
    public ResourceLocation texture_octagon_blur_2 = new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_2");
    public ResourceLocation texture_octagon_blur_3 = new ResourceLocation(Wizardry.MODID, "particles/octagon_blur_3");

    public LensFlareFX(World world, Vec3d pos, int age, double maxRange) {
        super(world, pos.xCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange), pos.yCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange), pos.zCoord + ThreadLocalRandom.current().nextDouble(-maxRange, maxRange));
        particleAlpha =  0.0001f;
        particleMaxAge = age * Config.particlePercentage / 100;
        particleScale = 1.5f;

        motionX = 0;
        motionY = 0;
        motionZ = 0;

        // Random lens flare texture
        TextureAtlasSprite sprite;

        if (ThreadLocalRandom.current().nextBoolean()) {
        /*    if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_hexagon.toString());
            else if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_hexagon_blur_1.toString());
            else if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_hexagon_blur_2.toString());
            else */sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_hexagon_blur_3.toString());
        } else {
            /*if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_octagon.toString());
            else if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_octagon_blur_1.toString());
            else if (ThreadLocalRandom.current().nextBoolean())
                sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_octagon_blur_2.toString());
            else */sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_octagon_blur_3.toString());
        }
        this.setParticleTexture(sprite);
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
