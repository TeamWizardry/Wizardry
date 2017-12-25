package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ZachHourGlass {

	private File zachBlockDir;
	private File zachEntityDir;
	@NotNull
	private JsonObject BLOCK_JSON = new JsonObject();
	@NotNull
	private JsonObject ENTITY_JSON = new JsonObject();
	private HashMultimap<UUID, EntityState> ENTITIES = HashMultimap.create();
	private HashMultimap<BlockPos, IBlockState> BLOCKS = HashMultimap.create();
	private BasicPalette PALETTE;
	private EntityZachriel entityZachriel;

	private boolean tracking = false;

	public ZachHourGlass(@NotNull EntityZachriel entityZachriel) {
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

		//try {
		//	/if (zachBlockDir.exists()) {
		//	/	JsonElement element = new JsonParser().parse(new FileReader(zachBlockDir));
		//	/	if (element != null && element.isJsonObject()) BLOCK_JSON = element.getAsJsonObject();
		//	/}
		//	/if (zachEntityDir.exists()) {
		//	/	JsonElement element = new JsonParser().parse(new FileReader(zachEntityDir));
		//	/	if (element != null && element.isJsonObject()) ENTITY_JSON = element.getAsJsonObject();
		//	/}
		//} catch (FileNotFoundException e) {
		//	e.printStackTrace();
		//}
	}

	public void trackEntityTick(EntityLivingBase entity) {
		if (!tracking) return;
		ENTITIES.put(entity.getUniqueID(), new EntityState(entity));
	}

	public Set<UUID> getAllTrackedEntities() {
		return ENTITIES.keySet();
	}

	public Set<EntityState> getEntityTimeMap(EntityLivingBase entity) {
		return ENTITIES.get(entity.getUniqueID());
	}

	public Set<EntityState> getEntityTimeMap(UUID entity) {
		return ENTITIES.get(entity);
	}

	@Nullable
	public EntityState getEntityStateAtTime(UUID entity, long time) {
		ArrayList<EntityState> list = new ArrayList<>(getEntityTimeMap(entity));

		list.sort((state1, state2) -> {
			long time1 = state1.time;
			long time2 = state2.time;
			return Long.compare(time2, time1);
		});

		float percent = (float) (time / 200.0);

		return list.get(MathHelper.clamp((int) ((list.size() - 1) * percent), 0, list.size() - 1));
	}

	@Nullable
	public EntityState getEntityStateAtTime(EntityLivingBase entity, long time) {
		AtomicReference<EntityState> state = new AtomicReference<>();
		getEntityTimeMap(entity).forEach(entityState -> {
			if ((int) (entityState.getTime() / 1000.0) == time) state.set(entityState);
		});

		return state.get();
	}

	public void resetEntities() {
		ENTITIES = HashMultimap.create();
	}

	public void resetBlocks() {
		BLOCKS = HashMultimap.create();
	}

	public void getPalette() {
		PALETTE = new BasicPalette();
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
						PALETTE.addMapping(fromState, paletteObject.getAsJsonPrimitive("palette_id").getAsInt());
					} catch (NBTException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void serialize() {
		//FileWriter writer = null;
		//try {
		//	writer = new FileWriter(zachBlockDir);
		//	writer.write(new Gson().toJson(BLOCK_JSON));
		//} catch (IOException e) {
		//	e.printStackTrace();
		//} finally {
		//	IOUtils.closeQuietly(writer);
		//}

		//try {
		//	writer = new FileWriter(zachEntityDir);
		//	writer.write(new Gson().toJson(ENTITY_JSON));
		//} catch (IOException e) {
		//	e.printStackTrace();
		//} finally {
		//	IOUtils.closeQuietly(writer);
		//}
	}

	public EntityZachriel getEntityZachriel() {
		return entityZachriel;
	}

	public boolean isTracking() {
		return tracking;
	}

	public void setTracking(boolean tracking, boolean serialize) {
		this.tracking = tracking;

		if (!tracking && serialize) {
			// TODO: serialize
			resetBlocks();
			resetEntities();
		}
	}

	public class EntityState {

		private long time;
		private Vec3d position;
		private float pitch, yaw;
		private float health;
		private double hunger, saturation;

		public EntityState(EntityLivingBase entity) {
			time = System.currentTimeMillis();
			position = entity.getPositionVector();
			pitch = entity.rotationPitch;
			yaw = entity.rotationYaw;
			health = entity.getHealth();

			if (entity instanceof EntityPlayer) {
				saturation = ((EntityPlayer) entity).getFoodStats().getSaturationLevel();
				hunger = ((EntityPlayer) entity).getFoodStats().getFoodLevel();
			}
		}

		public long getTime() {
			return time;
		}

		public Vec3d getPosition() {
			return position;
		}

		public float getPitch() {
			return pitch;
		}

		public float getYaw() {
			return yaw;
		}

		public float getHealth() {
			return health;
		}

		public double getHunger() {
			return hunger;
		}

		public double getSaturation() {
			return saturation;
		}

		public void setToEntity(EntityLivingBase entity) {
			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient() {
					entity.setPositionAndRotationDirect(getPosition().x, getPosition().y, getPosition().z, getYaw(), getPitch(), 10, true);
					Tardis.INSTANCE.interpolatePosition(entity, entity.getPositionVector(), getPosition(), 100);
				}
			});

			//entity.setPositionAndUpdate(getPosition().x, getPosition().y, getPosition().z);
			entity.setHealth(getHealth());

			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).getFoodStats().setFoodLevel((int) getHunger());
				((EntityPlayer) entity).getFoodStats().setFoodSaturationLevel((float) getSaturation());
			}
		}
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
