package com.teamwizardry.wizardry.api.spell;

import kotlin.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by LordSaad.
 */
public class SpellData {

	@NotNull
	public World world;
	@NotNull
	private HashMap<Pair, Object> data = new HashMap<>();

	public SpellData(@NotNull World world) {
		this.world = world;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> Pair<String, Class<T>> constructPair(@NotNull String key, @NotNull Class<?> type) {
		return (Pair<String, Class<T>>) new Pair(key, type);
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
			addData(DefaultKeys.TARGET_HIT, entity.getPositionVector());
			addData(DefaultKeys.ENTITY_HIT, entity);
		}
	}

	public SpellData copy() {
		SpellData spell = new SpellData(world);
		spell.addAllData(data);
		return spell;
	}

	public static class DefaultKeys {
		@NotNull
		public static final Pair<String, Class<Entity>> CASTER = constructPair("caster", Entity.class);
		@NotNull
		public static final Pair<String, Class<Float>> YAW = constructPair("yaw", Float.class);
		@NotNull
		public static final Pair<String, Class<Float>> PITCH = constructPair("pitch", Float.class);
		@NotNull
		public static final Pair<String, Class<Vec3d>> ORIGIN = constructPair("origin", Vec3d.class);
		@NotNull
		public static final Pair<String, Class<Entity>> ENTITY_HIT = constructPair("entity_hit", Entity.class);
		@NotNull
		public static final Pair<String, Class<BlockPos>> BLOCK_HIT = constructPair("block_hit", BlockPos.class);
		@NotNull
		public static final Pair<String, Class<Vec3d>> TARGET_HIT = constructPair("target_hit", Vec3d.class);
	}
}
