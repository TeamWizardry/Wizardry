package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.spell.ProcessData.DataType;
import com.teamwizardry.wizardry.api.spell.SpellDataTypes.BlockSet;
import com.teamwizardry.wizardry.api.spell.SpellDataTypes.BlockStateCache;

import net.minecraft.block.state.IBlockState;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.BLOCK_HIT;

/**
 * Created by Demoniaque.
 */
@Savable
@SuppressWarnings("rawtypes")
public class SpellData implements INBTSerializable<NBTTagCompound> {

	private static HashMap<String, DataField<?>> availableFields = new HashMap<>(); 

	@Nonnull
	public final World world;

	@Nonnull
	private final HashMap<DataField<?>, Object> data = new HashMap<>();
	
	public SpellData(@Nonnull World world) {
		this.world = world;
	}
	
	@Nonnull
	public static <T> DataField<T> constructField(@Nonnull String key, @Nonnull Class<T> type) {
		DataField<T> field = new DataField<T>(key, type);
		availableFields.put(key, field);
		return field;
	}
	
	@Nonnull static Collection<DataField<?>> getAllAvailableFields() {
		return Collections.unmodifiableCollection(availableFields.values());
	}
	
	public void addAllData(HashMap<DataField<?>, Object> data) {
		this.data.putAll(data);
	}
	
	public <T> void addData(@Nonnull DataField<T> key, @Nullable T value) {
		this.data.put(key, value);
	}
	
	public <T> void removeData(@Nonnull DataField<T> key) {
		this.data.remove(key);
	}
	
	@Nullable
	public <T> T getData(@Nonnull DataField<T> key) {
		return getData(key, null);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> T getData(@Nonnull DataField<T> key, @Nonnull T def) {
		Object value = data.get(key);
		if ( value != null && key.getDataType().isInstance(value) )
			return (T)value;
		return def;
	}
	
	public <T> boolean hasData(@Nonnull DataField<T> key) {
		return data.get(key) != null;
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
					BlockPos pos = getData(BLOCK_HIT);
					if (pos == null) {
						Entity victim = getData(DefaultKeys.ENTITY_HIT);
						if (victim == null) {
							return null;
						} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
					} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
				} else return target;
			} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getOrigin() {
		Vec3d origin = getData(DefaultKeys.ORIGIN);
		if (origin == null) {
			Entity caster = getData(DefaultKeys.CASTER);
			if (caster == null) {
				return null;
			} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
		} else return origin;
	}

	@Nullable
	public Vec3d getTargetWithFallback() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					Vec3d origin = getData(DefaultKeys.ORIGIN);
					if (origin == null) {
						Entity caster = getData(DefaultKeys.CASTER);
						if (caster == null) {
							return null;
						} else return caster.getPositionVector().add(0, caster.height / 2.0, 0);
					}
					return origin;
				} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public Vec3d getTarget() {
		Vec3d target = getData(DefaultKeys.TARGET_HIT);
		if (target == null) {
			BlockPos pos = getData(BLOCK_HIT);
			if (pos == null) {
				Entity victim = getData(DefaultKeys.ENTITY_HIT);
				if (victim == null) {
					return null;
				} else return victim.getPositionVector().add(0, victim.height / 2.0, 0);
			} else return new Vec3d(pos).add(0.5, 0.5, 0.5);
		}
		return target;
	}

	@Nullable
	public BlockPos getTargetPos() {
		return getData(BLOCK_HIT);
	}

	@Nullable
	public EnumFacing getFaceHit() {
		return getData(DefaultKeys.FACE_HIT);
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
			addData(DefaultKeys.ORIGIN, entity.getPositionVector().add(0, entity.getEyeHeight(), 0));
			addData(DefaultKeys.CASTER, entity);
			addData(DefaultKeys.YAW, entity.rotationYaw);
			addData(DefaultKeys.PITCH, entity.rotationPitch);
			addData(DefaultKeys.LOOK, entity.getLook(0));
			addData(DefaultKeys.CAPABILITY, WizardryCapabilityProvider.getCap(entity));
		} else {
			addData(DefaultKeys.TARGET_HIT, entity.getPositionVector().add(0, entity.height / 2.0, 0));
			addData(DefaultKeys.ENTITY_HIT, entity);
		}
	}

	public void processBlock(@Nullable BlockPos pos, @Nullable EnumFacing facing, @Nullable Vec3d targetHit) {
		if (pos == null && targetHit != null) pos = new BlockPos(targetHit);
		if (targetHit == null && pos != null) targetHit = new Vec3d(pos).add(0.5, 0.5, 0.5);

		addData(BLOCK_HIT, pos);
		addData(DefaultKeys.TARGET_HIT, targetHit);
		addData(DefaultKeys.FACE_HIT, facing);
	}

	public SpellData copy() {
		SpellData spell = new SpellData(world);
		spell.addAllData(data);
		spell.deserializeNBT(serializeNBT());
		return spell;
	}

	public static SpellData deserializeData(World world, NBTTagCompound compound) {
		SpellData data = new SpellData(world);
		data.deserializeNBT(compound);
		return data;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		primary:
		for (String key : nbt.getKeySet()) {
			DataField<?> field = availableFields.get(key);
			if( field != null ) {
				NBTBase nbtType = nbt.getTag(key);
				data.put(field, field.getDataTypeProcess().deserialize(world, nbtType));
				continue primary;
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (Entry<DataField<? extends Object>, Object> entry : data.entrySet()) {
			NBTBase nbtClass = entry.getKey().getDataTypeProcess().serialize(entry.getValue());
			compound.setTag(entry.getKey().getFieldName(), nbtClass);
		}

		return compound;
	}

	@Override
	public String toString() {
		return "SpellData{" +
				"world=" + world +
				", data=" + data +
				'}';
	}
	
	/////////////////
	
	public static class DataField<E> {
		private final String fieldName;
		private final Class<E> dataType;
		private DataType lazy_dataTypeProcess = null;	// Lazy, because datatypes might not been initialized, if calling before ProcessData.registerAnnotatedDataTypes()
		
		public DataField(String fieldName, Class<E> dataType) {
			this.fieldName = fieldName;
			this.dataType = dataType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<E> getDataType() {
			return dataType;
		}

		public DataType getDataTypeProcess() {
			if( lazy_dataTypeProcess == null )
				lazy_dataTypeProcess = ProcessData.INSTANCE.getDataType(dataType);
			return lazy_dataTypeProcess;
		}

		//////////////////////////
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dataType == null) ? 0 : dataType.toString().hashCode());
			result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataField other = (DataField) obj;
			if (dataType == null) {
				if (other.dataType != null)
					return false;
			} else if (!dataType.toString().equals(other.dataType.toString()))
				return false;
			if (fieldName == null) {
				if (other.fieldName != null)
					return false;
			} else if (!fieldName.equals(other.fieldName))
				return false;
			return true;
		}
	}
	
	/////////////////

	public static class DefaultKeys {
		public static final DataField<NBTTagList> TAG_LIST = constructField("list", NBTTagList.class);
		public static final DataField<NBTTagCompound> COMPOUND = constructField("compound", NBTTagCompound.class);
		public static final DataField<Integer> MAX_TIME = constructField("max_time", Integer.class);
		public static final DataField<Entity> CASTER = constructField("caster", Entity.class);
		public static final DataField<Float> YAW = constructField("yaw", Float.class);
		public static final DataField<Float> PITCH = constructField("pitch", Float.class);
		public static final DataField<Vec3d> LOOK = constructField("look", Vec3d.class);
		public static final DataField<Vec3d> ORIGIN = constructField("origin", Vec3d.class);
		public static final DataField<Entity> ENTITY_HIT = constructField("entity_hit", Entity.class);
		public static final DataField<BlockPos> BLOCK_HIT = constructField("block_hit", BlockPos.class);
		public static final DataField<EnumFacing> FACE_HIT = constructField("face_hit", EnumFacing.class);
		public static final DataField<IWizardryCapability> CAPABILITY = constructField("capability", IWizardryCapability.class);
		public static final DataField<Vec3d> TARGET_HIT = constructField("target_hit", Vec3d.class);
		public static final DataField<IBlockState> BLOCK_STATE = constructField("block_state", IBlockState.class);
		public static final DataField<Long> SEED = constructField("seed", Long.class);
		public static final DataField<BlockSet> BLOCK_SET = constructField("block_set", BlockSet.class);
		public static final DataField<BlockStateCache> BLOCKSTATE_CACHE = constructField("blockstate_cache", BlockStateCache.class);
	}
}
