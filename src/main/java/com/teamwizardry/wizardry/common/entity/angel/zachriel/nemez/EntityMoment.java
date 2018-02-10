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

import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 3:15 PM on 1/15/18.
 */
public final class EntityMoment {

	public static final EntityMoment EMPTY = new EntityMoment();
	private static final Function1<FoodStats, Object> exhaustionGetter = MethodHandleHelper.wrapperForGetter(FoodStats.class, "field_75126_c", "foodExhaustionLevel");
	private static final Function2<FoodStats, Object, Unit> exhaustionSetter = MethodHandleHelper.wrapperForSetter(FoodStats.class, "field_75126_c", "foodExhaustionLevel");
	private static final Function2<FoodStats, Object, Unit> saturationSetter = MethodHandleHelper.wrapperForSetter(FoodStats.class, "field_75125_b", "foodSaturationLevel");
	@Nullable
	public final Double x, y, z;
	@Nullable
	public final Float yaw, pitch;
	@Nullable
	public final Float health;
	@Nullable
	public final Integer food;
	@Nullable
	public final Float saturation, exhaustion;

	public EntityMoment(@Nullable Double x, @Nullable Double y, @Nullable Double z, @Nullable Float yaw, @Nullable Float pitch, @Nullable Float health, @Nullable Integer food, @Nullable Float saturation, @Nullable Float exhaustion) {
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

	private EntityMoment() {
		this(null, null, null, null, null, null, null, null, null);
	}

	public static EntityMoment fromNBT(NBTTagCompound nbt) {
		return new EntityMoment(nbt.hasKey("x") ? nbt.getDouble("x") : null,
				nbt.hasKey("y") ? nbt.getDouble("y") : null,
				nbt.hasKey("z") ? nbt.getDouble("z") : null,
				nbt.hasKey("yaw") ? nbt.getFloat("yaw") : null,
				nbt.hasKey("pitch") ? nbt.getFloat("pitch") : null,
				nbt.hasKey("health") ? nbt.getFloat("health") : null,
				nbt.hasKey("food") ? nbt.getInteger("food") : null,
				nbt.hasKey("saturation") ? nbt.getFloat("saturation") : null,
				nbt.hasKey("exhaustion") ? nbt.getFloat("exhaustion") : null);
	}

	public static EntityMoment fromPreviousMoment(Entity entity, EntityMoment previous) {
		if (previous == null)
			return new EntityMoment(entity);

		Double x, y, z;
		Float yaw, pitch;
		Float health = null;
		Integer food = null;
		Float saturation = null, exhaustion = null;
		x = (previous.x == null || entity.posX != previous.x) ? entity.posX : null;
		y = (previous.y == null || entity.posY != previous.y) ? entity.posY : null;
		z = (previous.z == null || entity.posZ != previous.z) ? entity.posZ : null;
		yaw = (previous.yaw == null || entity.rotationYaw != previous.yaw) ? entity.rotationYaw : null;
		pitch = (previous.pitch == null || entity.rotationPitch != previous.pitch) ? entity.rotationPitch : null;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			health = (previous.health == null || living.getHealth() != previous.health) ? living.getHealth() : null;
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				food = (previous.food == null || player.getFoodStats().getFoodLevel() != previous.food) ? player.getFoodStats().getFoodLevel() : null;
				saturation = (previous.saturation == null || player.getFoodStats().getSaturationLevel() != previous.saturation) ? player.getFoodStats().getSaturationLevel() : null;
				exhaustion = (previous.exhaustion == null || exhaustionGetter.invoke(player.getFoodStats()) != previous.saturation) ? (Float) exhaustionGetter.invoke(player.getFoodStats()) : null;
			}
		}
		return new EntityMoment(x, y, z, yaw, pitch, health, food, saturation, exhaustion);
	}

	public void apply(Entity entity) {
		entity.setNoGravity(true);
		if (x != null) entity.posX = x;
		if (y != null) entity.posY = y;
		if (z != null) entity.posZ = z;
		if (yaw != null) entity.rotationYaw = yaw;
		if (pitch != null) entity.rotationPitch = pitch;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			if (health != null) living.setHealth(health);
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				if (food != null) player.getFoodStats().setFoodLevel(food);
				if (saturation != null) saturationSetter.invoke(player.getFoodStats(), saturation);
				if (exhaustion != null) exhaustionSetter.invoke(player.getFoodStats(), exhaustion);
			}
		}
	}

	public void apply(Entity entity, EntityMoment nextMoment, float partialTicks) {
		if (partialTicks == 0) {
			apply(entity);
			return;
		}

		entity.setNoGravity(true);
		if (x != null) entity.posX = x + (nextMoment.x != null ? (nextMoment.x - x) * partialTicks : 0);
		if (y != null) entity.posY = y + (nextMoment.y != null ? (nextMoment.y - y) * partialTicks : 0);
		if (z != null) entity.posZ = z + (nextMoment.z != null ? (nextMoment.z - z) * partialTicks : 0);
		if (yaw != null) entity.rotationYaw = yaw +
				(nextMoment.yaw != null ? (((((nextMoment.yaw - yaw) % 360) + 540) % 360) - 180) * partialTicks : 0);
		if (pitch != null)
			entity.rotationPitch = pitch + (nextMoment.pitch != null ? (nextMoment.pitch - pitch) * partialTicks : 0);
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			if (health != null)
				living.setHealth(health + (nextMoment.health != null ? (nextMoment.health - health) * partialTicks : 0));
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				if (food != null)
					player.getFoodStats().setFoodLevel(food + (int) (nextMoment.food != null ? (nextMoment.food - food) * partialTicks : 0));
				if (saturation != null)
					saturationSetter.invoke(player.getFoodStats(), saturation + (nextMoment.saturation != null ? (nextMoment.saturation - saturation) * partialTicks : 0));
				if (exhaustion != null)
					exhaustionSetter.invoke(player.getFoodStats(), exhaustion + (nextMoment.exhaustion != null ? (nextMoment.exhaustion - exhaustion) * partialTicks : 0));
			}
		}
	}

	public EntityMoment withOverride(EntityMoment moment) {
		return new EntityMoment(
				moment.x != null ? moment.x : x,
				moment.y != null ? moment.y : y,
				moment.z != null ? moment.z : z,
				moment.yaw != null ? moment.yaw : yaw,
				moment.pitch != null ? moment.pitch : pitch,
				moment.health != null ? moment.health : health,
				moment.food != null ? moment.food : food,
				moment.saturation != null ? moment.saturation : saturation,
				moment.exhaustion != null ? moment.exhaustion : exhaustion
		);
	}

	public boolean matches(Entity entity) {
		if (x != null && entity.posX != x) return false;
		if (y != null && entity.posY != y) return false;
		if (z != null && entity.posZ != z) return false;
		if (yaw != null && entity.rotationYaw != yaw) return false;
		if (pitch != null && entity.rotationPitch != pitch) return false;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			if (health != null && living.getHealth() != health) return false;
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				if (food != null && player.getFoodStats().getFoodLevel() != food) return false;
				return saturation == null || !(player.getFoodStats().getSaturationLevel() != saturation);
			}
		}

		return true;
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		if (x != null) compound.setDouble("x", x);
		if (y != null) compound.setDouble("y", y);
		if (z != null) compound.setDouble("z", z);
		if (yaw != null) compound.setFloat("yaw", yaw);
		if (pitch != null) compound.setFloat("pitch", pitch);
		if (health != null) compound.setFloat("health", health);
		if (food != null) compound.setByte("food", food.byteValue());
		if (saturation != null) compound.setFloat("saturation", saturation);
		if (exhaustion != null) compound.setFloat("exhaustion", exhaustion);
		return compound;
	}
}
