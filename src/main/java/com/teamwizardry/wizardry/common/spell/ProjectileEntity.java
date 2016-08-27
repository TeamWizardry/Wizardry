package com.teamwizardry.wizardry.common.spell;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ProjectileEntity extends SpellEntity {
    private EntityPlayer player;
    private int ticker = 0;
    private Color trailColor;
    private SpellStack stack;

    public ProjectileEntity(World world, double posX, double posY, double posZ, SpellStack stack) {
        super(world, posX, posY, posZ, stack.spell);
        this.setSize(0.1F, 0.1F);
        this.isImmuneToFire = true;
        this.player = stack.player;
        this.stack = stack;

        if (stack.player.getHeldItemMainhand() != null) {
            ItemStack item = stack.player.getHeldItemMainhand();
            if (item.getItem() instanceof INacreColorable) {
                INacreColorable colorable = (INacreColorable) item.getItem();
                //trailColor = colorable.getColor(item);
            }
        }
    }

    @Override
    public float getEyeHeight() {
        return 0;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        ticker++;
        for (int i = 0; i < 2; i++) {
            double theta = i * Math.toRadians((360.0 / 2) + ticker);
            Vec3d origin = new Vec3d(posX + 0.5 * Math.cos(theta), posY, posZ + 0.5 * Math.sin(theta));

            // TODO: Add motion so they dont just move in the same space
            // TODO: Fix color
            SparkleTrailHelix helix = Wizardry.proxy.spawnParticleSparkleTrailHelix(worldObj, origin, getPositionVector(), 0.5, theta);
            helix.setColor(trailColor);
            helix.setFadeIn();
            helix.setGrow();
            helix.setShrink();
            helix.setMaxAge(50);
            helix.setAlpha(1f);
            helix.setScale(0.5f);
            // helix.addContinuousMotion(new Vec3d(-motionX * 10, -motionY * 10,
            // -motionZ * 10));

            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, getPositionVector());
            fizz.setColor(trailColor);
            fizz.setFadeIn();
            fizz.setGrow();
            fizz.setShrink();
            fizz.setMaxAge(50);
            fizz.setAlpha(1f);
            fizz.setScale(0.5f);
            fizz.setRandomDirection(0.1, 0.1, 0.1);
            fizz.setJitter(10, 0.1, 0.1, 0.1);
        }

        RayTraceResult cast = RaycastUtils.raycast(this.worldObj, this.getPositionVector(), new Vec3d(motionX, motionY, motionZ), Math.min(spell.getDouble(Module.SPEED), 1));

        if (cast != null) {
            if (cast.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = cast.getBlockPos();
                SpellEntity entity = new SpellEntity(worldObj, pos.getX(), pos.getY(), pos.getZ());
                stack.castEffects(entity);
                this.setDead();
            } else if (cast.typeOfHit == RayTraceResult.Type.ENTITY && cast.entityHit != player) {
                stack.castEffects(cast.entityHit);
                this.setDead();
            }
        }

        posX += motionX * 4;
        posY += motionY * 4;
        posZ += motionZ * 4;
        setPosition(posX, posY, posZ);
    }

    public void setDirection(float yaw, float pitch) {
        double speed = spell.getDouble(Module.SPEED) / 10;
        Vec3d dir = this.getVectorForRotation(pitch, yaw);
        this.setVelocity(dir.xCoord * speed, dir.yCoord * speed, dir.zCoord * speed);
    }
}
