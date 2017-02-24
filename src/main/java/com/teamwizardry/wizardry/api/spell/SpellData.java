package com.teamwizardry.wizardry.api.spell;

import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by LordSaad.
 */
public class SpellData implements INBTSerializable<NBTTagCompound> {

	private static HashMap<Pair, ProcessData.Process> dataProcessor = new HashMap<>();
	@NotNull
	public World world;
	@NotNull
	private HashMap<Pair, Object> data = new HashMap<>();

	public SpellData(@NotNull World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T, E extends NBTBase> Pair<String, Class<T>> constructPair(@NotNull String key, @NotNull Class<?> type, ProcessData.Process<E, T> data) {
		Pair<String, Class<T>> pair = new Pair(key, type);
		dataProcessor.put(pair, data);
		return pair;
	}

	public void addAllData(HashMap<Pair, Object> data) {
		this.data.putAll(data);
	}

	public <T> void addData(@NotNull Pair<String, Class<T>> key, @Nullable T value) {
		this.data.put(key, value);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getData(@NotNull Pair<String, Class<T>> pair) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return null;
	}

	@NotNull
	@SuppressWarnings("unchecked")
	public <T> T getData(@NotNull Pair<String, Class<T>> pair, @NotNull T def) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return def;
	}

	@SuppressWarnings("unchecked")
	public <T> boolean hasData(@NotNull Pair<String, Class<T>> pair) {
		return data.containsKey(pair) && data.get(pair) != null;
	}

	public void crunchData(@NotNull Entity entity, boolean asCaster) {
		if (asCaster) {
			addData(DefaultKeys.ORIGIN, entity.getPositionVector());
			addData(DefaultKeys.CASTER, entity);
			addData(DefaultKeys.YAW, entity.rotationYaw);
			addData(DefaultKeys.PITCH, entity.rotationPitch);
		} else {
			addData(DefaultKeys.TARGET_HIT, entity.getPositionVector().addVector(0, entity.height / 2, 0));
			addData(DefaultKeys.ENTITY_HIT, entity);
		}
	}

	public SpellData copy() {
		SpellData spell = new SpellData(world);
		spell.addAllData(data);
		spell.deserializeNBT(serializeNBT());
		return spell;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (Pair pair : data.keySet()) {
			NBTBase nbtClass = dataProcessor.get(pair).serialize(data.get(pair));
			if (nbtClass != null) {
				compound.setTag(pair.getFirst() + "", nbtClass);
			}
		}
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		for (String key : nbt.getKeySet()) {
			for (Pair pair : dataProcessor.keySet()) {
				if (pair.getFirst().equals(key)) {
					NBTBase nbtType = nbt.getTag(pair.getFirst() + "");
					data.put(pair, dataProcessor.get(pair).deserialize(world, nbtType));
				}
			}
		}
	}

	public static class DefaultKeys {
		@NotNull
		public static final Pair<String, Class<Entity>> CASTER = constructPair("caster", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Override
			public NBTTagInt serialize(Entity object) {
				return new NBTTagInt(object.getEntityId());
			}

			@Override
			public Entity deserialize(World world, NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		@NotNull
		public static final Pair<String, Class<Float>> YAW = constructPair("yaw", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(World world, NBTTagFloat object) {
				return object.getFloat();
			}
		});

		@NotNull
		public static final Pair<String, Class<Float>> PITCH = constructPair("pitch", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(World world, NBTTagFloat object) {
				return object.getFloat();
			}
		});

		@NotNull
		public static final Pair<String, Class<Vec3d>> ORIGIN = constructPair("origin", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.xCoord);
				compound.setDouble("y", object.yCoord);
				compound.setDouble("z", object.zCoord);
				return compound;
			}

			@Override
			public Vec3d deserialize(World world, NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		@NotNull
		public static final Pair<String, Class<Entity>> ENTITY_HIT = constructPair("entity_hit", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Override
			public NBTTagInt serialize(Entity object) {
				return new NBTTagInt(object.getEntityId());
			}

			@Override
			public Entity deserialize(World world, NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		@NotNull
		public static final Pair<String, Class<BlockPos>> BLOCK_HIT = constructPair("block_hit", BlockPos.class, new ProcessData.Process<NBTTagLong, BlockPos>() {
			@Override
			public NBTTagLong serialize(BlockPos object) {
				return new NBTTagLong(object.toLong());
			}

			@Override
			public BlockPos deserialize(World world, NBTTagLong object) {
				return BlockPos.fromLong(object.getLong());
			}
		});

		@NotNull
		public static final Pair<String, Class<Vec3d>> TARGET_HIT = constructPair("target_hit", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.xCoord);
				compound.setDouble("y", object.yCoord);
				compound.setDouble("z", object.zCoord);
				return compound;
			}

			@Override
			public Vec3d deserialize(World world, NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});
	}
}
