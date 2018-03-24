package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.wizardry.api.capability.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Created by Demoniaque.
 */
@Savable
@SuppressWarnings("rawtypes")
public class SpellData implements INBTSerializable<NBTTagCompound> {

	private static HashMap<Pair, ProcessData.Process> dataProcessor = new HashMap<>();

	@Nonnull
	public final World world;
	@Nonnull
	private final HashMap<Pair, Object> data = new HashMap<>();

	public SpellData(@Nonnull World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T, E extends NBTBase> Pair<String, Class<T>> constructPair(@Nonnull String key, @Nonnull Class<?> type, ProcessData.Process<E, T> data) {
		Pair<String, Class<T>> pair = new Pair(key, type);
		dataProcessor.put(pair, data);
		return pair;
	}

	public void addAllData(HashMap<Pair, Object> data) {
		this.data.putAll(data);
	}

	public <T> void addData(@Nonnull Pair<String, Class<T>> key, @Nullable T value) {
		this.data.put(key, value);
	}

	public <T> void removeData(@Nonnull Pair<String, Class<T>> key) {
		if (this.data.containsKey(key))
			this.data.remove(key);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getData(@Nonnull Pair<String, Class<T>> pair) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return null;
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> T getData(@Nonnull Pair<String, Class<T>> pair, @Nonnull T def) {
		if (data.containsKey(pair) && pair.getSecond().isInstance(data.get(pair)))
			return (T) data.get(pair);
		return def;
	}

	public <T> boolean hasData(@Nonnull Pair<String, Class<T>> pair) {
		return data.containsKey(pair) && data.get(pair) != null;
	}

	public void processTrace(RayTraceResult trace, @Nullable Vec3d fallback) {

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			processEntity(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK)
			processBlock(trace.getBlockPos(), trace.sideHit, trace.hitVec);
		else {
			Vec3d vec = trace.hitVec == null ? fallback : trace.hitVec;

			if (vec == null) return;
			processBlock(new BlockPos(vec), null, vec);
		}
	}

	public void processTrace(RayTraceResult trace) {
		processTrace(trace, null);
	}

	@Nullable
	public Vec3d getOriginWithFallback() {
		Vec3d origin = getData(DefaultKeys.ORIGIN);
		if (origin == null) {
			Entity caster = getData(DefaultKeys.CASTER);
			if (caster == null) {
				Vec3d target = getData(DefaultKeys.TARGET_HIT);
				if (target == null) {
					BlockPos pos = getData(DefaultKeys.BLOCK_HIT);
					if (pos == null) {
						Entity victim = getData(DefaultKeys.ENTITY_HIT);
						if (victim == null) {
							return null;
						} else return victim.getPositionVector().addVector(0, victim.height / 2.0, 0);
					} else return new Vec3d(pos).addVector(0.5, 0.5, 0.5);
				} else return target;
			} else return caster.getPositionVector().addVector(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getOrigin() {
		Vec3d origin = getData(DefaultKeys.ORIGIN);
		if (origin == null) {
			Entity caster = getData(DefaultKeys.CASTER);
			if (caster == null) {
				return null;
			} else return caster.getPositionVector().addVector(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getTargetWithFallback() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(DefaultKeys.BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					Vec3d origin = getData(DefaultKeys.ORIGIN);
					if (origin == null) {
						Entity caster = getData(DefaultKeys.CASTER);
						if (caster == null) {
							return null;
						} else return caster.getPositionVector().addVector(0, caster.height / 2.0, 0);
					}
					return origin;
				} else return victim.getPositionVector().addVector(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public Vec3d getTarget() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(DefaultKeys.BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					return null;
				} else return victim.getPositionVector().addVector(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public BlockPos getTargetPos() {
		Vec3d target = getTarget();
		if (target == null) return null;
		return new BlockPos(target);
	}

	@Nullable
	public Entity getCaster() {
		return getData(DefaultKeys.CASTER);
	}

	@Nullable
	public Entity getVictim() {
		return getData(DefaultKeys.ENTITY_HIT);
	}

	@Nullable
	public Entity getVictimWithCasterFallback() {
		Entity victim = getVictim();
		return victim == null ? getCaster() : victim;
	}

	@Nullable
	public IWizardryCapability getCapability() {
		IWizardryCapability capability = getData(DefaultKeys.CAPABILITY);
		if (capability == null) {
			Entity caster = getCaster();
			if (caster == null) {
				return null;
			} else return WizardryCapabilityProvider.getCap(caster);
		} else return capability;
	}

	public RayTraceResult.Type getHitType() {
		if (getVictim() == null) {
			Vec3d vec = getTarget();
			if (vec == null) {
				return RayTraceResult.Type.MISS;
			} else return RayTraceResult.Type.BLOCK;
		} else return RayTraceResult.Type.ENTITY;
	}

	public float getPitch() {
		return getData(DefaultKeys.PITCH, 0f);
	}

	public float getYaw() {
		return getData(DefaultKeys.YAW, 0f);
	}

	@Nullable
	public Vec3d getOriginHand() {
		Vec3d trueOrigin = getOriginWithFallback();
		if (trueOrigin == null) return null;
		float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - getYaw()));
		float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - getYaw()));
		return new Vec3d(offX, 0, offZ).add(trueOrigin);
	}

	public void processEntity(@Nonnull Entity entity, boolean asCaster) {
		if (asCaster) {
			addData(DefaultKeys.ORIGIN, entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0));
			addData(DefaultKeys.CASTER, entity);
			addData(DefaultKeys.YAW, entity.rotationYaw);
			addData(DefaultKeys.PITCH, entity.rotationPitch);
			addData(DefaultKeys.LOOK, entity.getLook(0));
			addData(DefaultKeys.CAPABILITY, WizardryCapabilityProvider.getCap(entity));
		} else {
			addData(DefaultKeys.TARGET_HIT, entity.getPositionVector().addVector(0, entity.height / 2.0, 0));
			addData(DefaultKeys.ENTITY_HIT, entity);
		}
	}

	public void processBlock(@Nullable BlockPos pos, @Nullable EnumFacing facing, @Nullable Vec3d targetHit) {
		if (pos == null && targetHit != null) pos = new BlockPos(targetHit);
		if (targetHit == null && pos != null) targetHit = new Vec3d(pos).addVector(0.5, 0.5, 0.5);

		addData(DefaultKeys.BLOCK_HIT, pos);
		addData(DefaultKeys.TARGET_HIT, targetHit);
		addData(DefaultKeys.FACE_HIT, facing);
	}

	public SpellData copy() {
		SpellData spell = new SpellData(world);
		spell.addAllData(data);
		spell.deserializeNBT(serializeNBT());
		return spell;
	}

	@Override
	@SuppressWarnings("unchecked")
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (Pair pair : data.keySet()) {
			NBTBase nbtClass = dataProcessor.get(pair).serialize(data.get(pair));
			compound.setTag(pair.getFirst() + "", nbtClass);
		}
		return compound;
	}

	@Override
	@SuppressWarnings("unchecked")
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

	public static SpellData deserializeData(World world, NBTTagCompound compound) {
		SpellData data = new SpellData(world);
		data.deserializeNBT(compound);
		return data;
	}

	@Override
	public String toString() {
		return "SpellData{" +
				"world=" + world +
				", data=" + data +
				'}';
	}

	public static class DefaultKeys {

		public static final Pair<String, Class<Integer>> MAX_TIME = constructPair("max_time", Integer.class, new ProcessData.Process<NBTTagInt, Integer>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(@Nullable Integer object) {
				if (object == null) return new NBTTagInt(1);
				return new NBTTagInt(object);
			}

			@Override
			public Integer deserialize(@Nonnull World world, @Nonnull NBTTagInt object) {
				return object.getInt();
			}
		});

		public static final Pair<String, Class<Entity>> CASTER = constructPair("caster", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(Entity object) {
				if (object != null)
					return new NBTTagInt(object.getEntityId());
				return new NBTTagInt(-1);
			}

			@Override
			public Entity deserialize(@Nonnull World world, @Nonnull NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		public static final Pair<String, Class<Float>> YAW = constructPair("yaw", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Nonnull
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(@Nonnull World world, @Nonnull NBTTagFloat object) {
				return object.getFloat();
			}
		});

		public static final Pair<String, Class<Float>> PITCH = constructPair("pitch", Float.class, new ProcessData.Process<NBTTagFloat, Float>() {
			@Nonnull
			@Override
			public NBTTagFloat serialize(Float object) {
				return new NBTTagFloat(object);
			}

			@Override
			public Float deserialize(@Nonnull World world, @Nonnull NBTTagFloat object) {
				return object.getFloat();
			}
		});

		public static final Pair<String, Class<Vec3d>> LOOK = constructPair("look", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(@Nonnull World world, @Nonnull NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		public static final Pair<String, Class<Vec3d>> ORIGIN = constructPair("origin", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(@Nonnull World world, @Nonnull NBTTagCompound object) {
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		public static final Pair<String, Class<Entity>> ENTITY_HIT = constructPair("entity_hit", Entity.class, new ProcessData.Process<NBTTagInt, Entity>() {
			@Nonnull
			@Override
			public NBTTagInt serialize(Entity object) {
				if (object == null) return new NBTTagInt(-1);
				return new NBTTagInt(object.getEntityId());
			}

			@Override
			public Entity deserialize(@Nonnull World world, @Nonnull NBTTagInt object) {
				return world.getEntityByID(object.getInt());
			}
		});

		public static final Pair<String, Class<BlockPos>> BLOCK_HIT = constructPair("block_hit", BlockPos.class, new ProcessData.Process<NBTTagLong, BlockPos>() {
			@Nonnull
			@Override
			public NBTTagLong serialize(BlockPos object) {
				if (object == null) return new NBTTagLong(-1L);
				return new NBTTagLong(object.toLong());
			}

			@Override
			public BlockPos deserialize(@Nonnull World world, @Nonnull NBTTagLong object) {
				return BlockPos.fromLong(object.getLong());
			}
		});

		@Nonnull
		public static final Pair<String, Class<EnumFacing>> FACE_HIT = constructPair("face_hit", EnumFacing.class, new ProcessData.Process<NBTTagString, EnumFacing>() {
			@Nonnull
			@Override
			public NBTTagString serialize(EnumFacing object) {
				if (object == null) return new NBTTagString("UP");
				return new NBTTagString(object.name());
			}

			@Override
			public EnumFacing deserialize(@Nonnull World world, @Nonnull NBTTagString object) {
				return EnumFacing.valueOf(object.getString());
			}
		});

		@Nonnull
		public static final Pair<String, Class<IWizardryCapability>> CAPABILITY = constructPair("capability", IWizardryCapability.class, new ProcessData.Process<NBTTagCompound, IWizardryCapability>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(IWizardryCapability object) {
				if (object == null) return new NBTTagCompound();
				return object.serializeNBT();
			}

			@Override
			public IWizardryCapability deserialize(@Nonnull World world, @Nonnull NBTTagCompound object) {
				DefaultWizardryCapability cap = new DefaultWizardryCapability();
				cap.deserializeNBT(object);
				return cap;
			}
		});

		@Nonnull
		public static final Pair<String, Class<Vec3d>> TARGET_HIT = constructPair("target_hit", Vec3d.class, new ProcessData.Process<NBTTagCompound, Vec3d>() {
			@Nonnull
			@Override
			public NBTTagCompound serialize(Vec3d object) {
				if (object == null) return new NBTTagCompound();
				NBTTagCompound compound = new NBTTagCompound();
				compound.setDouble("x", object.x);
				compound.setDouble("y", object.y);
				compound.setDouble("z", object.z);
				return compound;
			}

			@Override
			public Vec3d deserialize(@Nonnull World world, @Nonnull NBTTagCompound object) {
				if (!object.hasKey("x") || !object.hasKey("y") || !object.hasKey("z")) return Vec3d.ZERO;
				double x = object.getDouble("x");
				double y = object.getDouble("y");
				double z = object.getDouble("z");
				return new Vec3d(x, y, z);
			}
		});

		@Nonnull
		public static final Pair<String, Class<Long>> SEED = constructPair("seed", Long.class, new ProcessData.Process<NBTTagLong, Long>() {

			@Nonnull
			@Override
			public NBTTagLong serialize(@Nullable Long object) {
				if (object == null) return new NBTTagLong(0);
				return new NBTTagLong(object);
			}

			@Nonnull
			@Override
			public Long deserialize(@Nonnull World world, @Nonnull NBTTagLong object) {
				return object.getLong();
			}
		});
	}
}
