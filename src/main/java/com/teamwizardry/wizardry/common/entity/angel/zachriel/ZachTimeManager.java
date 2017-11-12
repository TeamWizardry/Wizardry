package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

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

		zachBlockDir.getParentFile().mkdirs();
		zachEntityDir.getParentFile().mkdirs();

		try {
			if (!zachBlockDir.exists()) zachBlockDir.createNewFile();
			if (!zachEntityDir.exists()) zachEntityDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	public void resetEntities() {
		ENTITY_JSON = new JsonObject();
		serialize();
	}

	public void resetBlocks() {
		BLOCK_JSON = new JsonObject();
		serialize();
	}

	public List<BlockPos> getTrackedBlocks() {
		List<BlockPos> poses = new ArrayList<>();
		for (Map.Entry<String, JsonElement> element : BLOCK_JSON.entrySet()) {
			if (element.getKey().equalsIgnoreCase("palette")) continue;
			String key = element.getKey();
			if (key == null) continue;
			if (NumberUtils.isCreatable(key)) {
				long posLong = Long.valueOf(key);
				poses.add(BlockPos.fromLong(posLong));
			}
		}
		return poses;
	}

	public List<Entity> getTrackedEntities(World world) {
		List<Entity> entities = new ArrayList<>();
		for (Map.Entry<String, JsonElement> element : ENTITY_JSON.entrySet()) {
			String key = element.getKey();
			if (key == null) continue;
			UUID uuid = UUID.fromString(key);
			for (Entity entity : world.getEntities(Entity.class, input -> true)) {
				if (entity.getUniqueID().equals(uuid)) entities.add(entity);
			}
		}
		return entities;
	}

	public HashMap<Long, JsonObject> getEntitySnapshots(Entity entity) {
		HashMap<Long, JsonObject> objects = new HashMap<>();
		if (ENTITY_JSON.has(entity.getUniqueID() + "") && ENTITY_JSON.get(entity.getUniqueID() + "").isJsonArray()) {
			for (JsonElement element : ENTITY_JSON.getAsJsonArray(entity.getUniqueID() + "")) {
				if (element.isJsonObject()) {
					JsonObject object = element.getAsJsonObject();
					if (object.has("time")) {
						long time = object.getAsJsonPrimitive("time").getAsLong();
						objects.put(time, object);
					}
				}
			}
		}
		return objects;
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

	public void trackEntity(Entity entity) {
		UUID uuid = entity.getUniqueID();

		if (!ENTITY_JSON.has(uuid + "")) {
			ENTITY_JSON.add(uuid + "", new JsonArray());
		}

		JsonObject snapshot = snapshotEntity(entity);
		ENTITY_JSON.getAsJsonArray(uuid + "").add(snapshot);

		serialize();
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

	// TODO capability saving
	public void setEntityToSnapshot(JsonObject snapshot, Entity entity) {
		if (snapshot.has("pos") && snapshot.get("pos").isJsonObject()) {
			JsonObject pos = snapshot.getAsJsonObject("pos");

			if (pos.has("pos_x") && pos.has("pos_y") && pos.has("pos_z") && pos.has("rot_yaw") && pos.has("rot_pitch")) {
				double x = pos.getAsJsonPrimitive("pos_x").getAsDouble();
				double y = pos.getAsJsonPrimitive("pos_y").getAsDouble();
				double z = pos.getAsJsonPrimitive("pos_z").getAsDouble();
				float yaw = pos.getAsJsonPrimitive("rot_yaw").getAsFloat();
				float pitch = pos.getAsJsonPrimitive("rot_pitch").getAsFloat();

				Wizardry.logger.info("time: " + snapshot.getAsJsonPrimitive("time").getAsLong() + " pos: " + x + ", " + y + ", " + z);

				Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);

				entity.dismountRidingEntity();
				if (entity instanceof EntityPlayerMP)
					((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, yaw, pitch, set);
				entity.setRotationYawHead(yaw);
			}
		}

		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).setHealth(snapshot.getAsJsonPrimitive("health").getAsFloat());
		}
		if (entity instanceof EntityPlayer) {
			((EntityPlayer) entity).getFoodStats().setFoodLevel(snapshot.getAsJsonPrimitive("hunger").getAsInt());
			((EntityPlayer) entity).getFoodStats().setFoodSaturationLevel(snapshot.getAsJsonPrimitive("saturation").getAsInt());
		}

		if (entity instanceof EntityPlayer && snapshot.has("inventory") && snapshot.get("inventory").isJsonArray()) {
			for (JsonElement element : snapshot.getAsJsonArray("inventory")) {
				if (!element.isJsonObject()) continue;
				JsonObject itemObject = element.getAsJsonObject();
				if (itemObject.has("count")
						&& itemObject.has("item")
						&& itemObject.has("armor")
						&& itemObject.has("damage")
						&& itemObject.has("meta")
						&& itemObject.has("slot")) {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemObject.getAsJsonPrimitive("item").getAsString()));
					if (item == null) continue;
					int count = itemObject.getAsJsonPrimitive("count").getAsInt();
					int damage = itemObject.getAsJsonPrimitive("damage").getAsInt();
					int slot = itemObject.getAsJsonPrimitive("slot").getAsInt();
					int meta = itemObject.getAsJsonPrimitive("meta").getAsInt();
					boolean armor = itemObject.getAsJsonPrimitive("armor").getAsBoolean();

					ItemStack stack = new ItemStack(item, count, meta);
					stack.setItemDamage(damage);

					if (!armor) ((EntityPlayer) entity).inventory.mainInventory.add(slot, stack);
					else ((EntityPlayer) entity).inventory.armorInventory.add(slot, stack);
				}
			}
		}
	}

	public JsonObject snapshotEntity(Entity entity) {
		JsonObject object = new JsonObject();

		object.addProperty("time", System.currentTimeMillis() - lastRecordedBlockTime);

		JsonObject pos = new JsonObject();
		pos.addProperty("pos_x", entity.posX);
		pos.addProperty("pos_y", entity.posY);
		pos.addProperty("pos_z", entity.posZ);
		pos.addProperty("rot_pitch", entity.rotationPitch);
		pos.addProperty("rot_yaw", entity.rotationYaw);
		object.add("pos", pos);

		if (entity instanceof EntityLivingBase)
			object.addProperty("health", ((EntityLivingBase) entity).getHealth());

		if (entity instanceof EntityPlayer) {
			object.addProperty("hunger", ((EntityPlayer) entity).getFoodStats().getFoodLevel());
			object.addProperty("saturation", ((EntityPlayer) entity).getFoodStats().getSaturationLevel());
		}

		JsonArray inv = new JsonArray();
		if (entity instanceof EntityPlayer)
			for (ItemStack stack : ((EntityPlayer) entity).inventory.mainInventory) {
				if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) continue;

				JsonObject itemObject = new JsonObject();
				itemObject.addProperty("armor", false);
				itemObject.addProperty("count", stack.getCount());
				itemObject.addProperty("item", stack.getItem().getRegistryName().toString());
				itemObject.addProperty("damage", stack.getItemDamage());
				itemObject.addProperty("meta", stack.getMetadata());
				itemObject.addProperty("slot", ((EntityPlayer) entity).inventory.getSlotFor(stack));

				inv.add(itemObject);
			}

		if (entity instanceof EntityPlayer)
			for (ItemStack stack : ((EntityPlayer) entity).inventory.armorInventory) {
				if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) continue;

				JsonObject itemObject = new JsonObject();
				itemObject.addProperty("armor", true);
				itemObject.addProperty("count", stack.getCount());
				itemObject.addProperty("item", stack.getItem().getRegistryName().toString());
				itemObject.addProperty("damage", stack.getItemDamage());
				itemObject.addProperty("meta", stack.getMetadata());
				itemObject.addProperty("slot", ((EntityPlayer) entity).inventory.getSlotFor(stack));

				inv.add(itemObject);
			}
		object.add("inventory", inv);
		return object;
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
