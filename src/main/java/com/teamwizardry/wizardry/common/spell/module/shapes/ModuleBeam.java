package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.librarianlib.math.Raycast;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.concurrent.ThreadLocalRandom;

public class ModuleBeam extends Module implements IContinuousCast {

    private int ticker = 0;

    public ModuleBeam() {
        attributes.addAttribute(Attribute.DISTANCE);
        attributes.addAttribute(Attribute.SCATTER);
        attributes.addAttribute(Attribute.PROJ_COUNT);
        attributes.addAttribute(Attribute.PIERCE);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription() {
        return "Casts a beam that strikes the first target in a raycast.";
    }

    @Override
    public String getDisplayName() {
        return "Beam";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
        compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
        compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
        compound.setInteger(PIERCE, (int) attributes.apply(Attribute.PIERCE, 0));
        return compound;
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell) {

        double distance = spell.getDouble(DISTANCE);
        double pierce = spell.getInteger(PIERCE);
        RayTraceResult raycast = Raycast.cast(caster, distance);
        NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);

        // Beam particles
        double slopeX = 0, slopeY = 0, slopeZ = 0;
        Vec3d cross = caster.getLook(1).crossProduct(new Vec3d(0, caster.getEyeHeight(), 0)).normalize().scale(caster.width / 2);
        slopeX = (raycast.hitVec.xCoord - (caster.posX + cross.xCoord)) / distance;
        slopeY = (raycast.hitVec.yCoord - (caster.posY + caster.getEyeHeight() + cross.yCoord)) / distance;
        slopeZ = (raycast.hitVec.zCoord - (caster.posZ + cross.zCoord)) / distance;

        ticker++;
        for (double i = 0; i < distance; i += distance / 100) {
            double x = slopeX * i + caster.posX + cross.xCoord;
            double y = slopeY * i + caster.posY + caster.getEyeHeight();
            double z = slopeZ * i + caster.posZ + cross.zCoord;

            double theta = Math.toRadians((360.0 / i));
            Vec3d origin = new Vec3d(x + 0.2 * Math.cos(theta), y, z + 0.2 * Math.sin(theta));
            Vec3d center = new Vec3d(x, y, z);

            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(caster.worldObj, x, y, z, 0.5F, 0.5F, 10, true);
            fizz.setRandomizedSizes(true);
            fizz.blur();

            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                SparkleTrailHelix helix = Wizardry.proxy.spawnParticleSparkleTrailHelix(caster.worldObj, origin, center, 0.2, theta, 50, true);
                helix.setRandomizedSizes(true);
                //helix.blur();
            }
        }
        // Beam particles

        do {
            if (raycast.typeOfHit == RayTraceResult.Type.BLOCK) {
                for (int i = 0; i < modules.tagCount(); i++) {
                    Entity entity = new SpellEntity(caster.worldObj, raycast.getBlockPos().getX(), raycast.getBlockPos().getY(), raycast.getBlockPos().getZ());
                    SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
                    MinecraftForge.EVENT_BUS.post(event);
                }
                return true;
            } else if (raycast.typeOfHit == RayTraceResult.Type.ENTITY) {
                for (int i = 0; i < modules.tagCount(); i++) {
                    SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), raycast.entityHit, player);
                    MinecraftForge.EVENT_BUS.post(event);
                }
                pierce--;
                raycast = Raycast.cast(raycast.entityHit, caster.getLookVec(), distance);
            } else return false;
        }
        while (pierce > 0);
        return true;
    }
}