package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;

/**
 * @author WireSegal
 * Created at 3:15 PM on 1/15/18.
 */
public final class EntityMoment {

    private static final Function1<FoodStats, Object> exhaustionGetter = MethodHandleHelper.wrapperForGetter(FoodStats.class, "field_75126_c", "foodExhaustionLevel");
    private static final Function2<FoodStats, Object, Unit> exhaustionSetter = MethodHandleHelper.wrapperForSetter(FoodStats.class, "field_75126_c", "foodExhaustionLevel");
    private static final Function2<FoodStats, Object, Unit> saturationSetter = MethodHandleHelper.wrapperForSetter(FoodStats.class, "field_75125_b", "foodSaturationLevel");

    public final double x, y, z;
    public final float yaw, pitch;

    public final float health;

    public final int food;
    public final float saturation, exhaustion;

    public EntityMoment(double x, double y, double z, float yaw, float pitch, float health, int food, float saturation, float exhaustion) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.health = health;
        this.food = food;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
    }

    public EntityMoment(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch,
                entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : 0f,
                entity instanceof EntityPlayer ? ((EntityPlayer) entity).getFoodStats().getFoodLevel() : 0,
                entity instanceof EntityPlayer ? ((EntityPlayer) entity).getFoodStats().getSaturationLevel() : 0,
                entity instanceof EntityPlayer ? ((Float) exhaustionGetter.invoke(((EntityPlayer) entity).getFoodStats())) : 0f);
    }

    public void apply(Entity entity) {
        entity.posX = x;
        entity.posY = y;
        entity.posZ = z;
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.setHealth(health);
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) living;
                player.getFoodStats().setFoodLevel(food);
                saturationSetter.invoke(player.getFoodStats(), saturation);
                exhaustionSetter.invoke(player.getFoodStats(), exhaustion);
            }
        }
    }

    public void apply(Entity entity, EntityMoment nextMoment, float partialTicks) {
        if (partialTicks == 0) {
            apply(entity);
            return;
        }

        entity.posX = x + (nextMoment.x - x) * partialTicks;
        entity.posY = y + (nextMoment.y - y) * partialTicks;
        entity.posZ = z + (nextMoment.z - z) * partialTicks;
        entity.rotationYaw = yaw + (((((nextMoment.yaw - yaw) % 360) + 540) % 360) - 180) * partialTicks;
        entity.rotationPitch = pitch + (nextMoment.pitch - pitch) * partialTicks;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.setHealth(health + (nextMoment.health - health) * partialTicks);
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) living;
                player.getFoodStats().setFoodLevel(food + (int) ((nextMoment.food - food) * partialTicks));
                saturationSetter.invoke(player.getFoodStats(), saturation + (nextMoment.saturation - saturation) * partialTicks);
                exhaustionSetter.invoke(player.getFoodStats(), exhaustion + (nextMoment.exhaustion - exhaustion) * partialTicks);
            }
        }
    }

    public boolean matches(Entity entity) {
        if (entity.posX != x) return false;
        if (entity.posY != y) return false;
        if (entity.posZ != z) return false;
        if (entity.rotationYaw != yaw) return false;
        if (entity.rotationPitch != pitch) return false;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            if (living.getHealth() != health) return false;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) living;
                if (player.getFoodStats().getFoodLevel() != food) return false;
                if (player.getFoodStats().getSaturationLevel() != saturation) return false;
            }
        }

        return true;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("x", x);
        compound.setDouble("y", y);
        compound.setDouble("z", z);
        compound.setFloat("yaw", yaw);
        compound.setFloat("pitch", pitch);
        compound.setFloat("health", health);
        compound.setByte("food", (byte) food);
        compound.setFloat("saturation", saturation);
        compound.setFloat("exhaustion", exhaustion);
        return compound;
    }

    public static EntityMoment fromNBT(NBTTagCompound nbt) {
        return new EntityMoment(nbt.getDouble("x"),
                nbt.getDouble("y"),
                nbt.getDouble("z"),
                nbt.getFloat("yaw"),
                nbt.getFloat("pitch"),
                nbt.getFloat("health"),
                nbt.getByte("food"),
                nbt.getFloat("saturation"),
                nbt.getFloat("exhaustion"));
    }
}
