package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.player.mana.DefaultManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.spell.ProcessData.Process;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterDataType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SpellDataTypes {

	private SpellDataTypes() {
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagInt", dataType = "java.lang.Integer")
	public static class IntegerType implements Process<NBTTagInt, Integer> {
		@NotNull
		@Override
		public NBTTagInt serialize(Integer object) {
			if (object == null) return new NBTTagInt(1);
			return new NBTTagInt(object);
		}

		@Override
		public Integer deserialize(@NotNull NBTTagInt object) {
			return object.getInt();
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagList", dataType = "net.minecraft.nbt.NBTTagList")
	public static class NBTTagListType implements Process<NBTTagList, NBTTagList> {
		@NotNull
		@Override
		public NBTTagList serialize(NBTTagList object) {
			return object == null ? new NBTTagList() : object;
		}

		@Override
		public NBTTagList deserialize(@NotNull NBTTagList object) {
			return object;
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagCompound", dataType = "net.minecraft.nbt.NBTTagCompound")
	public static class NBTTagCompoundType implements Process<NBTTagCompound, NBTTagCompound> {
		@NotNull
		@Override
		public NBTTagCompound serialize(NBTTagCompound object) {
			return object == null ? new NBTTagCompound() : object;
		}

		@Override
		public NBTTagCompound deserialize(@NotNull NBTTagCompound object) {
			return object;
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagFloat", dataType = "java.lang.Float")
	public static class FloatType implements Process<NBTTagFloat, Float> {
		@NotNull
		@Override
		public NBTTagFloat serialize(Float object) {
			return new NBTTagFloat(object);
		}

		@Override
		public Float deserialize(@NotNull NBTTagFloat object) {
			return object.getFloat();
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagCompound", dataType = "net.minecraft.util.math.Vec3d")
	public static class Vec3dType implements Process<NBTTagCompound, Vec3d> {
		@NotNull
		@Override
		public NBTTagCompound serialize(Vec3d object) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setDouble("x", object.x);
			compound.setDouble("y", object.y);
			compound.setDouble("z", object.z);
			return compound;
		}

		@Override
		public Vec3d deserialize(@NotNull NBTTagCompound object) {
			if (!object.hasKey("x", 6) || !object.hasKey("y", 6) || !object.hasKey("z", 6))
				return Vec3d.ZERO;
			double x = object.getDouble("x");
			double y = object.getDouble("y");
			double z = object.getDouble("z");
			return new Vec3d(x, y, z);
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagString", dataType = "java.lang.String")
	public static class CustomTagType implements Process<NBTTagString, String> {

		@Nonnull
		@Override
		public NBTTagString serialize(String object) {
			return new NBTTagString(object);
		}

		@Nullable
		@Override
		public String deserialize(@NotNull NBTTagString object) {
			return object.getString();
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagString", dataType = "java.util.UUID")
	public static class UUIDType implements Process<NBTTagString, UUID> {
		@Nonnull
		@Override
		public NBTTagString serialize(UUID object) {
			return new NBTTagString(object.toString());
		}

		@Nullable
		@Override
		public UUID deserialize(@NotNull NBTTagString object) {
			return UUID.fromString(object.getString());
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagLong", dataType = "net.minecraft.util.math.BlockPos")
	public static class BlockPosType implements Process<NBTTagLong, BlockPos> {
		@NotNull
		@Override
		public NBTTagLong serialize(BlockPos object) {
			if (object == null) return new NBTTagLong(-1L);
			return new NBTTagLong(object.toLong());
		}

		@Override
		public BlockPos deserialize(@NotNull NBTTagLong object) {
			return BlockPos.fromLong(object.getLong());
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagString", dataType = "net.minecraft.util.EnumFacing")
	public static class EnumFacingType implements Process<NBTTagString, EnumFacing> {
		@NotNull
		@Override
		public NBTTagString serialize(EnumFacing object) {
			if (object == null) return new NBTTagString("UP");
			return new NBTTagString(object.name());
		}

		@Override
		public EnumFacing deserialize(@NotNull NBTTagString object) {
			return EnumFacing.valueOf(object.getString());
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagCompound", dataType = "com.teamwizardry.wizardry.api.capability.player.mana.IWizardryCapability")
	public static class WizardryCapabilityType implements Process<NBTTagCompound, IManaCapability> {
		@NotNull
		@Override
		public NBTTagCompound serialize(IManaCapability object) {
			if (object == null) return new NBTTagCompound();
			return object.serializeNBT();
		}

		@Override
		public IManaCapability deserialize(@NotNull NBTTagCompound object) {
			DefaultManaCapability cap = new DefaultManaCapability();
			cap.deserializeNBT(object);
			return cap;
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagCompound", dataType = "net.minecraft.block.state.IBlockState")
	public static class BlockStateType implements Process<NBTTagCompound, IBlockState> {
		@NotNull
		@Override
		public NBTTagCompound serialize(IBlockState object) {
			NBTTagCompound nbtState = new NBTTagCompound();
			if (object == null) return nbtState;
			NBTUtil.writeBlockState(nbtState, object);
			return nbtState;
		}

		@Override
		public IBlockState deserialize(@NotNull NBTTagCompound object) {
			return NBTUtil.readBlockState(object);
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagLong", dataType = "java.lang.Long")
	public static class LongType implements Process<NBTTagLong, Long> {
		@NotNull
		@Override
		public NBTTagLong serialize(Long object) {
			if (object == null) return new NBTTagLong(0);
			return new NBTTagLong(object);
		}

		@Override
		public Long deserialize(@NotNull NBTTagLong object) {
			return object.getLong();
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagList", dataType = "com.teamwizardry.wizardry.api.spell.SpellDataTypes$BlockSet")
	public static class BlockSetType implements Process<NBTTagList, BlockSet> {
		@NotNull
		@Override
		public NBTTagList serialize(BlockSet object) {
			NBTTagList list = new NBTTagList();

			if (object == null) return list;

			for (BlockPos pos : object.getBlockSet()) {
				list.appendTag(new NBTTagLong(pos.toLong()));
			}

			return list;
		}

		@Override
		public BlockSet deserialize(@NotNull NBTTagList object) {
			Set<BlockPos> poses = new HashSet<>();

			for (NBTBase base : object) {
				if (base instanceof NBTTagLong) {
					poses.add(BlockPos.fromLong(((NBTTagLong) base).getLong()));
				}
			}

			return new BlockSet(poses);
		}
	}

	@RegisterDataType(storageType = "net.minecraft.nbt.NBTTagList", dataType = "com.teamwizardry.wizardry.api.spell.SpellDataTypes$BlockStateCache")
	public static class BlockStateCacheType implements Process<NBTTagList, BlockStateCache> {
		@NotNull
		@Override
		public NBTTagList serialize(BlockStateCache object) {
			NBTTagList list = new NBTTagList();

			if (object == null) return list;

			for (Map.Entry<BlockPos, IBlockState> entry : object.getBlockStateCache().entrySet()) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setLong("pos", entry.getKey().toLong());

				NBTTagCompound nbtState = new NBTTagCompound();
				NBTUtil.writeBlockState(nbtState, entry.getValue());
				compound.setTag("blockstate", nbtState);

				list.appendTag(compound);
			}

			return list;
		}

		@Override
		public BlockStateCache deserialize(@NotNull NBTTagList object) {
			HashMap<BlockPos, IBlockState> stateCache = new HashMap<>();

			for (NBTBase base : object) {
				if (base instanceof NBTTagCompound) {

					NBTTagCompound compound = (NBTTagCompound) base;
					if (compound.hasKey("pos") && compound.hasKey("blockstate")) {
						BlockPos pos = BlockPos.fromLong(compound.getLong("pos"));
						IBlockState state = NBTUtil.readBlockState(compound.getCompoundTag("blockstate"));

						stateCache.put(pos, state);

					}
				}
			}

			return new BlockStateCache(stateCache);
		}
	}

	////////////////

	public static class BlockSet {
		private final Set<BlockPos> blockSet;

		public BlockSet(Set<BlockPos> blockSet) {
			this.blockSet = blockSet;
		}

		public Set<BlockPos> getBlockSet() {
			return blockSet;
		}
	}

	public static class BlockStateCache {
		private final Map<BlockPos, IBlockState> blockStateCache;

		public BlockStateCache(Map<BlockPos, IBlockState> blockStateCache) {
			this.blockStateCache = blockStateCache;
		}

		public Map<BlockPos, IBlockState> getBlockStateCache() {
			return blockStateCache;
		}
	}
}
