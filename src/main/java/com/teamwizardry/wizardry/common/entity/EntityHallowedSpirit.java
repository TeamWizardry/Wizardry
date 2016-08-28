package com.teamwizardry.wizardry.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/17/2016.
 */
public class EntityHallowedSpirit extends EntityMob {

    private boolean angry = false;

    public EntityHallowedSpirit(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        this.experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.isImmuneToFire = true;
    }

    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 50.0F));
        this.applyEntityAI();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        //this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
    }

    protected void applyEntityAI() {
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    public void collideWithEntity(Entity entity) {
        if (this.getHealth() > 0) {
            if (entity.getName().equals(getName())) return;
            ((EntityLivingBase) entity).motionY += 0.4;
            ((EntityLivingBase) entity).attackEntityAsMob(this);
            ((EntityLivingBase) entity).setRevengeTarget(this);
        }
        entity.fallDistance = 0;

        ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
        glitter.setColor(new Color(0x4DFFFFFF, true));
        glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

        ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY + getEyeHeight(), posZ)), ThreadLocalRandom.current().nextInt(30, 50), 0, (i, build) -> {
            glitter.setMotion(new Vec3d(entity.motionX + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), entity.motionY / 2 + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), entity.motionZ + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
            glitter.disableMotion();
        });
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) return;

        if (ticksExisted % ThreadLocalRandom.current().nextInt(100, 200) == 0)
            playSound(ModSounds.HALLOWED_SPIRIT, ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());

        fallDistance = 0;

        EntityPlayer farPlayer = worldObj.getNearestPlayerNotCreative(this, 50);
        if (farPlayer != null) {
            Vec3d direction = getPositionVector().subtract(farPlayer.getPositionVector()).normalize();
            motionX = direction.xCoord * -0.05;
            motionY = direction.yCoord * -0.05;
            motionZ = direction.zCoord * -0.05;
            rotationYaw = (float) (-Math.atan2(direction.xCoord, direction.zCoord) * 180 / Math.PI - 180) / 2;
        }

        EntityPlayer player = worldObj.getNearestPlayerNotCreative(this, 2);
        EntityPlayer closePlayer = worldObj.getNearestPlayerNotCreative(this, 10);
        angry = player != null;

        ParticleBuilder glitter = new ParticleBuilder(30);
        glitter.disableMotion();
        glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

        ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY + getEyeHeight(), posZ)), 5, 0, (i, build) -> {
            if (closePlayer != null && !angry) {

                double radius = 0.15;
                double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                double r = (u > 1) ? 2 - u : u;
                double x = r * Math.cos(t), z = r * Math.sin(t);

                glitter.setColor(new InterpColorHSV(Color.RED, 50, 20));
                glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
                glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
                glitter.disableMotion();

            } else if (angry) {

                double radius = 0.2;
                double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                double r = (u > 1) ? 2 - u : u;
                double x = r * Math.cos(t), z = r * Math.sin(t);

                glitter.setColor(Color.RED);
                glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
                glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
                glitter.disableMotion();


            } else {

                double radius = 0.15;
                double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                double r = (u > 1) ? 2 - u : u;
                double x = r * Math.cos(t), z = r * Math.sin(t);

                glitter.setColor(new Color(0x4DFFFFFF, true));
                glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
                glitter.addMotion(new Vec3d(-motionX / 10, 0, -motionZ / 10));
                glitter.disableMotion();
            }
        });

        if (angry && player != null) {
            player.attackEntityFrom(DamageSource.generic, 0.15f);
            player.hurtResistantTime = 0;
        }
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 255;
    }

    @Override
    public void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
        if (!getEntityWorld().isRemote) {
            /*for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(2) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(Items.BONE, 1)));
				}
			}
			for (int i = 0; i < 1 + lootingModifier; i++) {
				if (rand.nextInt(3) == 0) {
					getEntityWorld().spawnEntityInWorld(new EntityItem(getEntityWorld(), posX, posY + 0.5, posZ, new ItemStack(MainRegistry.impTallow, 1)));
				}
			}*/
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        super.attackEntityFrom(source, amount);
        ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(100, 150));
        glitter.setColor(new InterpColorHSV(Color.BLUE, 50, 20));
        glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle_blurred"));

        ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(new Vec3d(posX, posY + getEyeHeight(), posZ)), ThreadLocalRandom.current().nextInt(40, 100), 0, (i, build) -> {
            double radius = 0.2;
            double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
            double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
            double r = (u > 1) ? 2 - u : u;
            double x = r * Math.cos(t), z = r * Math.sin(t);

            glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
            glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
            glitter.disableMotion();
        });
        return true;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }
}
