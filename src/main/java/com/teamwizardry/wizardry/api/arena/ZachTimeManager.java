package com.teamwizardry.wizardry.api.arena;

import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.teamwizardry.wizardry.common.entity.angel.EntityZachriel;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZachTimeManager {

	private File zachBlockDir;
	private File zachEntityDir;
	@NotNull
	private JsonObject BLOCK_JSON = new JsonObject();
	@NotNull
	private JsonObject ENTITY_JSON = new JsonObject();
	private EntityZachriel entityZachriel;
	private long lastRecordedBlockTime = System.currentTimeMillis();

	public ZachTimeManager(@NotNull EntityZachriel entityZachriel) {
		this.entityZachriel = entityZachriel;
		Arena arena = ArenaManager.INSTANCE.getArena(entityZachriel);
		if (arena == null) return;

		zachBlockDir = new File(CommonProxy.directory, "/zach_saver/blocks/" + entityZachriel.getUniqueID() + ".json");
		zachEntityDir = new File(CommonProxy.directory, "/zach_saver/entities/" + entityZachriel.getUniqueID() + ".json");

		try {
			if (zachBlockDir.exists()) {
				JsonElement element = new JsonParser().parse(new FileReader(zachBlockDir));
				if (element != null && element.isJsonObject()) BLOCK_JSON = element.getAsJsonObject();
			}
			if (zachEntityDir.exists()) {
				JsonElement element = new JsonParser().parse(new FileReader(zachEntityDir));
				if (element != null && element.isJsonObject()) ENTITY_JSON = element.getAsJsonObject();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		BLOCK_JSON = new JsonObject();
		ENTITY_JSON = new JsonObject();
		serialize();
	}


	public List<BlockPos> getTrackedBlocks() {
		List<BlockPos> poses = new ArrayList<>();
		for (Map.Entry<String, JsonElement> element : BLOCK_JSON.entrySet()) {
			if (element.getKey().equalsIgnoreCase("palette")) continue;
			String key = element.getKey();
			if (key == null) continue;
			if (NumberUtils.isNumber(key)) {
				long posLong = Long.valueOf(key);
				poses.add(BlockPos.fromLong(posLong));
			}
		}
		return poses;
	}

	public HashMap<Long, IBlockState> getBlocksAtPos(BlockPos pos, BasicPalette palette) {
		HashMap<Long, IBlockState> states = new HashMap<>();
		if (BLOCK_JSON.has(pos.toLong() + "") && BLOCK_JSON.get(pos.toLong() + "").isJsonArray()) {
			for (JsonElement element : BLOCK_JSON.getAsJsonArray(pos.toLong() + "")) {
				String data = element.getAsJsonPrimitive().getAsString();
				String[] split = data.split("%");
				long time = Long.valueOf(split[0]);
				IBlockState state = palette.stateFor(Integer.valueOf(split[1]));
				if (state == null) continue;
				states.put(time, state);
			}
		}
		return states;
	}

	public List<IBlockState> getBlocksAtPos(BlockPos pos) {
		List<IBlockState> states = new ArrayList<>();
		if (BLOCK_JSON.has(pos.toLong() + "") && BLOCK_JSON.get(pos.toLong() + "").isJsonArray()) {
			BasicPalette palette = getPalette();
			for (JsonElement element : BLOCK_JSON.getAsJsonArray(pos.toLong() + "")) {
				IBlockState state = palette.stateFor(element.getAsJsonPrimitive().getAsInt());
				if (state == null) continue;
				states.add(state);
			}
		}
		return states;
	}

	public List<Vec3d> getPosesForEntity(Entity entity) {
		List<Vec3d> poses = new ArrayList<>();
		if (ENTITY_JSON.has(entity.getEntityId() + "-pos") && ENTITY_JSON.get(entity.getEntityId() + "-pos").isJsonArray()) {
			Gson gson = new Gson();
			for (JsonElement element : ENTITY_JSON.getAsJsonArray(entity.getEntityId() + "-pos")) {
				Vec3d vec = gson.fromJson(element, Vec3d.class);
				if (vec == null) continue;
				poses.add(vec);
			}
		}
		return poses;
	}

	public List<Integer> getHealthsForEntity(Entity entity) {
		List<Integer> healths = new ArrayList<>();
		if (ENTITY_JSON.has(entity.getEntityId() + "-health") && ENTITY_JSON.get(entity.getEntityId() + "-health").isJsonArray()) {
			for (JsonElement element : ENTITY_JSON.getAsJsonArray(entity.getEntityId() + "-health")) {
				healths.add(element.getAsJsonPrimitive().getAsInt());
			}
		}
		return healths;
	}

	public void trackEntity(Entity entity) {
		if (!ENTITY_JSON.has(entity.getEntityId() + "-pos")) {
			ENTITY_JSON.add(entity.getEntityId() + "-pos", new JsonArray());
		}
		JsonElement element = new JsonParser().parse(new Gson().toJson(entity.getPositionVector()));

		ENTITY_JSON.getAsJsonArray(entity.getEntityId() + "-pos").add(element);

		if (entity instanceof EntityLivingBase) {
			if (!ENTITY_JSON.has(entity.getEntityId() + "-health")) {
				ENTITY_JSON.add(entity.getEntityId() + "-health", new JsonArray());
			}

			ENTITY_JSON.getAsJsonArray(entity.getEntityId() + "-health").add(((EntityLivingBase) entity).getHealth());
		}

		serialize();
	}

	public BasicPalette getPalette() {
		BasicPalette palette = new BasicPalette();
		JsonArray serializedPalette;
		if (!BLOCK_JSON.has("palette")) {
			serializedPalette = new JsonArray();
			BLOCK_JSON.add("palette", serializedPalette);
		} else serializedPalette = BLOCK_JSON.getAsJsonArray("palette");

		for (JsonElement element : serializedPalette) {
			if (element.isJsonObject()) {
				JsonObject paletteObject = element.getAsJsonObject();
				if (paletteObject.has("palette_id") && paletteObject.has("blockstate")) {
					try {
						NBTTagCompound compound = JsonToNBT.getTagFromJson(paletteObject.getAsJsonObject("blockstate").toString());
						IBlockState fromState = NBTUtil.readBlockState(compound);
						palette.addMapping(fromState, paletteObject.getAsJsonPrimitive("palette_id").getAsInt());
					} catch (NBTException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return palette;
	}

	public void trackBlock(IBlockState state, BlockPos pos) {
		BasicPalette palette = getPalette();

		boolean shouldAddID = !palette.hasMappingFor(state);

		int stateID = palette.idFor(state);

		if (!BLOCK_JSON.has(pos.toLong() + "")) {
			BLOCK_JSON.add(pos.toLong() + "", new JsonArray());
		}
		BLOCK_JSON.getAsJsonArray(pos.toLong() + "").add((System.currentTimeMillis() - lastRecordedBlockTime) + "%" + stateID);

		if (shouldAddID) {
			JsonObject paletteAddition = new JsonObject();
			paletteAddition.addProperty("palette_id", stateID);
			JsonElement object = new JsonParser().parse(NBTUtil.writeBlockState(new NBTTagCompound(), state).toString());
			paletteAddition.add("blockstate", object);

			BLOCK_JSON.getAsJsonArray("palette").add(paletteAddition);
		}

		serialize();
	}

	public void deserialize() {
		try {
			if (zachBlockDir.exists()) {
				JsonElement jsonElement = new JsonParser().parse(new FileReader(zachBlockDir));

				if (jsonElement.isJsonObject()) BLOCK_JSON = jsonElement.getAsJsonObject();
				else BLOCK_JSON = new JsonObject();
			}

			if (zachEntityDir.exists()) {
				JsonElement jsonElement = new JsonParser().parse(new FileReader(zachEntityDir));

				if (jsonElement.isJsonObject()) ENTITY_JSON = jsonElement.getAsJsonObject();
				else ENTITY_JSON = new JsonObject();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void serialize() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(zachBlockDir);
			writer.write(new Gson().toJson(BLOCK_JSON));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}

		try {
			writer = new FileWriter(zachEntityDir);
			writer.write(new Gson().toJson(ENTITY_JSON));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public EntityZachriel getEntityZachriel() {
		return entityZachriel;
	}

	public class BasicPalette {
		private final HashBiMap<IBlockState, Integer> ids = HashBiMap.create();
		private int lastId;

		private BasicPalette() {
		}

		public int idFor(@NotNull IBlockState state) {
			if (!ids.containsKey(state)) ids.put(state, lastId++);
			if (ids.containsKey(state)) return ids.get(state);
			return -1;
		}

		@Nullable
		public IBlockState stateFor(int id) {
			if (ids.containsValue(id)) return ids.inverse().get(id);
			return null;
		}

		public void addMapping(IBlockState state, int paletteID) {
			this.ids.put(state, paletteID);

			int biggestInt = -1;
			for (Integer id : ids.values()) {
				if (id > biggestInt) biggestInt = id;
			}
			lastId = ++biggestInt;
		}

		public boolean hasMappingFor(IBlockState state) {
			return this.ids.containsKey(state);
		}
	}
}
