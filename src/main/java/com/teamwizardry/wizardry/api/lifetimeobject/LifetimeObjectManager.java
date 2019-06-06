package com.teamwizardry.wizardry.api.lifetimeobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.function.Consumer;

public final class LifetimeObjectManager<T extends LifetimeObject> {
	private static final String NBT_KEY_TICK = "tick";
	private static final String NBT_KEY_ENTRIES = "entries";
	private static final String NBT_KEY_ENTRY_OBJECT = "object";
	private static final String NBT_KEY_ENTRY_TICK = "tick";
	private final Adapter<T> adapter;
	private final List<Entry> entries = new ArrayList<>();
	private final Deque<Entry> adds = new ArrayDeque<>();
	private long tick;

	public LifetimeObjectManager(final Adapter<T> adapter) {
		this.adapter = adapter;
	}

	public void add(final T object, final long lifespan) {
		this.adds.addLast(new Entry(object, lifespan));
	}

	public void tick(Consumer<Boolean> onChange) {
		for (Entry entry; ((entry = this.adds.pollFirst()) != null); ) {
			onChange.accept(true);
			this.entries.add(entry);
			entry.object.start();
		}
		for (final Iterator<Entry> it = this.entries.iterator(); it.hasNext(); ) {
			final Entry entry = it.next();
			//	Minecraft.getMinecraft().player.sendChatMessage(entry.tick + " / " + this.tick);
			if (--entry.tick > 0) {
				entry.object.tick();
			} else {
				entry.object.stop();
				it.remove();
				onChange.accept(true);
			}
		}
		this.tick++;
	}

	public NBTTagCompound toNbt() {
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setLong(NBT_KEY_TICK, this.tick);
		final NBTTagList entries = new NBTTagList();
		for (final Entry e : this.entries) {
			this.adapter.toNbt(e.object, nbt -> {
				final NBTTagCompound entry = new NBTTagCompound();
				entry.setTag(NBT_KEY_ENTRY_OBJECT, nbt);
				entry.setLong(NBT_KEY_ENTRY_TICK, e.tick);
				entries.appendTag(entry);
			});
		}
		compound.setTag(NBT_KEY_ENTRIES, entries);
		return compound;
	}

	public void fromNbt(final NBTTagCompound compound) {
		this.tick = compound.getLong(NBT_KEY_TICK);
		this.entries.clear();
		final NBTTagList entries = compound.getTagList(NBT_KEY_ENTRIES, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < entries.tagCount(); i++) {
			final NBTTagCompound entry = entries.getCompoundTagAt(i);
			this.adapter.fromNbt(entry.getCompoundTag(NBT_KEY_ENTRY_OBJECT), obj ->
					this.entries.add(new Entry(obj, entry.getLong(NBT_KEY_ENTRY_TICK)))
			);
		}
	}

	public interface Adapter<T extends LifetimeObject> {
		void toNbt(final T object, final Consumer<NBTTagCompound> consumer);

		void fromNbt(final NBTTagCompound nbt, final Consumer<T> consumer);
	}

	private final class Entry {
		private final T object;

		private long tick;

		Entry(final T object, final long tick) {
			this.object = object;
			this.tick = tick;
		}
	}
}