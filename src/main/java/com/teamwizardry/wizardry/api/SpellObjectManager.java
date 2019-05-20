package com.teamwizardry.wizardry.api;

import com.google.common.collect.ImmutableMap;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SpellObjectManager {

	private static final ResourceLocation LINGERING = new ResourceLocation("wizardry", "lingering");
	private static final ResourceLocation DELAYED = new ResourceLocation("wizardry", "delayed");
	private static final String NBT_KEY_TYPE = "type";
	private static final String NBT_KEY_DATA = "data";
	private final ImmutableMap<ResourceLocation, Supplier<SpellObject>> factories = ImmutableMap.<ResourceLocation, Supplier<SpellObject>>builder()
			.put(LINGERING, LingeringObject::new)
			.put(DELAYED, DelayedObject::new)
			.build();
	public final LifetimeObjectManager<SpellObject> manager = new LifetimeObjectManager<>(new LifetimeObjectManager.Adapter<SpellObject>() {
		@Override
		public void toNbt(final SpellObject object, final Consumer<NBTTagCompound> consumer) {
			final NBTTagCompound compound = new NBTTagCompound();
			compound.setString(NBT_KEY_TYPE, object.getType().toString());
			compound.setTag(NBT_KEY_DATA, object.serialize());
			consumer.accept(compound);
		}

		@Override
		public void fromNbt(final NBTTagCompound nbt, final Consumer<SpellObject> consumer) {
			final Supplier<SpellObject> factory = factories.get(new ResourceLocation(nbt.getString(NBT_KEY_TYPE)));
			if (factory != null) {
				final SpellObject obj = factory.get();
				obj.deserialize(nbt.getCompoundTag(NBT_KEY_DATA));
				consumer.accept(obj);
			}
		}
	});

	public void addLingering(final LingeringObject object, final int duration) {
		this.manager.add(object, duration);
	}

	public void addDelayed(final DelayedObject object, final int delay) {
		this.manager.add(object, delay);
	}

	public void tick(Consumer<Boolean> onChange) {
		this.manager.tick(onChange);
	}

	private interface SpellObject extends LifetimeObject {
		ResourceLocation getType();

		NBTTagCompound serialize();

		void deserialize(NBTTagCompound nbt);
	}

	public static final class LingeringObject implements SpellObject {

		private World world;
		private SpellData data;
		private SpellRing ring;

		LingeringObject() {
		}

		public LingeringObject(World world, SpellData data, SpellRing ring) {
			this.world = world;
			this.data = data;
			this.ring = ring;
		}

		@Override
		public ResourceLocation getType() {
			return LINGERING;
		}

		@Override
		public void start() {
		}

		@Override
		public void tick() {
			ring.runSpellRing(world, data, false);
		}

		@Override
		public void stop() {
		}

		@Override
		public NBTTagCompound serialize() {
			NBTTagCompound compound = new NBTTagCompound();
			if (ring != null)
				compound.setTag("spell_ring", ring.serializeNBT());
			if (data != null)
				compound.setTag("spell_data", data.serializeNBT());
			if (world != null) {
				compound.setInteger("world", world.provider.getDimension());
			}
			return compound;
		}

		@Override
		public void deserialize(NBTTagCompound nbt) {
			if (nbt.hasKey("spell_ring"))
				ring = SpellRing.deserializeRing(nbt.getCompoundTag("spell_ring"));
			if (nbt.hasKey("spell_data"))
				data = SpellData.deserializeData(nbt.getCompoundTag("spell_data"));
			if (nbt.hasKey("world"))
				world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(nbt.getInteger("world"));
		}
	}

	public static final class DelayedObject implements SpellObject {

		private World world;
		private SpellData data;
		private SpellRing ring;

		DelayedObject() {
		}

		public DelayedObject(World world, SpellData data, SpellRing ring) {
			this.world = world;
			this.data = data;
			this.ring = ring;
		}

		@Override
		public ResourceLocation getType() {
			return DELAYED;
		}

		@Override
		public void start() {
		}

		@Override
		public void tick() {
		}

		@Override
		public void stop() {
			if (ring.getModule() != null && ring.getModule().getModuleClass() instanceof IDelayedModule)
				((IDelayedModule) ring.getModule().getModuleClass()).runDelayedEffect(world, data, ring);
		}

		@Override
		public NBTTagCompound serialize() {
			NBTTagCompound compound = new NBTTagCompound();
			if (ring != null)
				compound.setTag("spell_ring", ring.serializeNBT());
			if (data != null)
				compound.setTag("spell_data", data.serializeNBT());
			if (world != null) {
				compound.setInteger("world", world.provider.getDimension());
			}
			return compound;
		}

		@Override
		public void deserialize(NBTTagCompound nbt) {
			if (nbt.hasKey("spell_ring"))
				ring = SpellRing.deserializeRing(nbt.getCompoundTag("spell_ring"));
			if (nbt.hasKey("spell_data"))
				data = SpellData.deserializeData(nbt.getCompoundTag("spell_data"));
			if (nbt.hasKey("world"))
				world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(nbt.getInteger("world"));
		}
	}
}