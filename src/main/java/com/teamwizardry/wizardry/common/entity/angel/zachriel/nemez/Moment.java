package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WireSegal
 * Created at 3:12 PM on 1/15/18.
 */
public final class Moment {
	// The lists produced are to be treated like stacks.
	private transient final Map<String, EntityMoment> totalDifference = new HashMap<>();
	private final ListMultimap<String, EntityMoment> entities = ArrayListMultimap.create();
	private final ListMultimap<BlockPos, IBlockState> blocks = ArrayListMultimap.create();

	@SuppressWarnings("deprecation")
	public static Moment fromNBT(NBTTagCompound nbt) {
		Moment newMoment = new Moment();
		NBTTagList blocksSerialized = nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound entitiesSerialized = nbt.getCompoundTag("entities");

		Map<String, EntityMoment> newTotal = new HashMap<>();
		ListMultimap<String, EntityMoment> newEntities = ArrayListMultimap.create();
		ListMultimap<BlockPos, IBlockState> newBlocks = ArrayListMultimap.create();

		for (NBTBase blockUncast : blocksSerialized) {
			NBTTagCompound block = (NBTTagCompound) blockUncast;
			BlockPos pos = BlockPos.fromLong(block.getLong("pos"));
			NBTTagList states = block.getTagList("states", Constants.NBT.TAG_COMPOUND);
			for (NBTBase stateUncast : states) {
				NBTTagCompound state = (NBTTagCompound) stateUncast;
				Block blockAt = Block.getBlockFromName(state.getString("id"));
				if (blockAt != null) {
					int meta = state.getByte("data");
					newBlocks.put(pos, blockAt.getStateFromMeta(meta));
				}
			}
		}

		for (String key : entitiesSerialized.getKeySet()) {
			NBTTagList moments = entitiesSerialized.getTagList(key, Constants.NBT.TAG_COMPOUND);
			EntityMoment total = EntityMoment.EMPTY;
			for (NBTBase momentUncast : moments) {
				NBTTagCompound momentCompound = (NBTTagCompound) momentUncast;
				EntityMoment moment = EntityMoment.fromNBT(momentCompound);
				total = total.withOverride(moment);
				newEntities.put(key, moment);
			}
			newTotal.put(key, total);
		}

		newMoment.totalDifference.putAll(newTotal);
		newMoment.entities.putAll(newEntities);
		newMoment.blocks.putAll(newBlocks);
		for (String id : newMoment.entities.keySet())
			newMoment.capEntity(id);
		for (BlockPos pos : newMoment.blocks.keySet())
			newMoment.capBlock(pos);
		return newMoment;
	}

	public void addBlockSnapshot(BlockPos position, IBlockState newState) {
		List<IBlockState> moments = blocks.get(position);
		if (moments != null && !moments.isEmpty()) {
			if (moments.get(moments.size() - 1) != newState)
				return;
		}
		blocks.put(position, newState);

		capBlock(position);
	}

	public void addEntitySnapshot(Entity entity) {
		String uuid = entity.getCachedUniqueIdString();
		EntityMoment total = totalDifference.get(uuid);
		EntityMoment newMoment = EntityMoment.fromPreviousMoment(entity, total);
		if (total == null)
			totalDifference.put(uuid, newMoment);
		else if (!total.matches(entity))
			totalDifference.put(uuid, total.withOverride(newMoment));
		else
			return;

		entities.put(entity.getCachedUniqueIdString(), new EntityMoment(entity));

		capEntity(entity);
	}

	private void capBlock(BlockPos pos) {
		List<IBlockState> moments = blocks.get(pos);
		if (moments.size() > 5) {
			List<IBlockState> newMoments = Lists.newArrayList();
			int slice = moments.size() / 5;
			int remainder = moments.size() % 5;
			for (int i = 5; i > 0; i--)
				newMoments.add(moments.get(slice * i + (i > remainder ? 1 : 0)));

			blocks.replaceValues(pos, newMoments);
		}
	}

	private void capEntity(Entity entity) {
		capEntity(entity.getCachedUniqueIdString());
	}

	private void capEntity(String id) {
		List<EntityMoment> moments = entities.get(id);
		if (moments.size() > 5) {
			List<EntityMoment> newMoments = Lists.newArrayList();
			int slice = moments.size() / 5;
			int remainder = moments.size() % 5;
			for (int i = 5; i > 0; i--) {
				int startIndex = slice * i + (i > remainder ? 1 : 0);
				int endIndex = (slice - 1) * i;
				EntityMoment compiled = moments.get(startIndex);
				for (int subIndex = startIndex; subIndex > endIndex; subIndex--)
					compiled = compiled.withOverride(moments.get(subIndex));

				newMoments.add(compiled);
			}

			entities.replaceValues(id, newMoments);
		}
	}

	public void collapse(Moment theNext) {
		entities.putAll(theNext.entities);
		blocks.putAll(theNext.blocks);

		for (String id : entities.keySet())
			capEntity(id);
		for (BlockPos pos : blocks.keySet())
			capBlock(pos);
	}

	public void apply(Entity entity) {
		List<EntityMoment> momentsOfEntity = entities.get(entity.getCachedUniqueIdString());
		if (momentsOfEntity == null || momentsOfEntity.isEmpty())
			return;
		momentsOfEntity.get(0).apply(entity);
	}

	public void apply(Entity entity, float partialTicks) {
		if (partialTicks == 0) {
			apply(entity);
			return;
		}

		List<EntityMoment> momentsOfEntity = entities.get(entity.getCachedUniqueIdString());
		if (momentsOfEntity == null || momentsOfEntity.isEmpty())
			return;

		int stackIndexOfMoment = (int) (momentsOfEntity.size() * partialTicks);
		int indexOfMoment = momentsOfEntity.size() - stackIndexOfMoment;
		if (indexOfMoment == 0) {
			apply(entity);
			return;
		}

		EntityMoment momentToApply = momentsOfEntity.get(indexOfMoment);
		EntityMoment nextMoment = momentsOfEntity.get(indexOfMoment - 1);
		float subPartial = partialTicks * momentsOfEntity.size() - ((float) stackIndexOfMoment / momentsOfEntity.size());
		momentToApply.apply(entity, nextMoment, subPartial);
	}

	public void apply(World world, BlockPos pos) {

		List<IBlockState> momentsOfBlock = blocks.get(pos);
		if (momentsOfBlock == null || momentsOfBlock.isEmpty())
			return;

		world.setBlockState(pos, momentsOfBlock.get(0));
	}

	public void apply(World world, BlockPos pos, float partialTicks) {
		if (partialTicks == 0) {
			apply(world, pos);
			return;
		}

		List<IBlockState> momentsOfBlock = blocks.get(pos);
		if (momentsOfBlock == null || momentsOfBlock.isEmpty())
			return;

		int stackIndexOfMoment = (int) (momentsOfBlock.size() * partialTicks);
		int indexOfMoment = momentsOfBlock.size() - stackIndexOfMoment;

		world.setBlockState(pos, momentsOfBlock.get(indexOfMoment));
	}

	public void apply(World world, Collection<Entity> tracked) {
		for (BlockPos position : blocks.keySet())
			apply(world, position);
		for (Entity entity : tracked)
			apply(entity);
	}

	public void apply(World world, Collection<Entity> tracked, float partialTicks) {
		for (BlockPos position : blocks.keySet())
			apply(world, position, partialTicks);
		for (Entity entity : tracked)
			apply(entity, partialTicks);
	}

	public Moment snapshot() {
		for (String id : entities.keySet())
			capEntity(id);
		for (BlockPos pos : blocks.keySet())
			capBlock(pos);

		Moment moment = new Moment();
		moment.totalDifference.putAll(totalDifference);
		moment.entities.putAll(entities);
		moment.blocks.putAll(blocks);
		return moment;
	}

	public NBTTagCompound serializeNBT() {
		for (String id : entities.keySet())
			capEntity(id);
		for (BlockPos pos : blocks.keySet())
			capBlock(pos);

		NBTTagCompound momentSerialized = new NBTTagCompound();
		NBTTagList blocksSerialized = new NBTTagList();
		NBTTagCompound entitiesSerialized = new NBTTagCompound();

		momentSerialized.setTag("blocks", blocksSerialized);
		momentSerialized.setTag("entities", entitiesSerialized);

		for (BlockPos position : blocks.keySet()) {
			NBTTagList states = new NBTTagList();
			NBTTagCompound posCompound = new NBTTagCompound();
			posCompound.setLong("pos", position.toLong());
			posCompound.setTag("states", states);

			for (IBlockState state : blocks.get(position)) {
				ResourceLocation regName = state.getBlock().getRegistryName();
				if (regName != null) {
					NBTTagCompound block = new NBTTagCompound();
					block.setString("id", regName.toString());
					block.setByte("data", (byte) state.getBlock().getMetaFromState(state));
					states.appendTag(block);
				}
			}

			blocksSerialized.appendTag(posCompound);
		}

		for (String identifier : entities.keySet()) {
			NBTTagList moments = new NBTTagList();

			for (EntityMoment moment : entities.get(identifier))
				moments.appendTag(moment.serializeNBT());

			entitiesSerialized.setTag(identifier, moments);
		}

		return momentSerialized;
	}
}
