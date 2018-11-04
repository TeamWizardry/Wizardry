package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public class ProcessData {
	public static final ProcessData INSTANCE = new ProcessData();
	
	private HashMap<String, DatatypeEntry<?,?>> datatypeRegistry = new HashMap<>();
	
	private ProcessData() {}

	public <T extends NBTBase, E> void registerDataType(@Nonnull Class<E> type, @Nonnull Class<T> storageType, @Nonnull Process<T, E> process) {
		String dataTypeName = type.getName();
		if( datatypeRegistry.containsKey(dataTypeName) )
			throw new IllegalStateException("Datatype '" + dataTypeName + "' is already registered.");	// TODO: Make a new exception class
		
		DatatypeEntry<T,E> entry = new DatatypeEntry<>(dataTypeName, type, storageType, process);
		datatypeRegistry.put(dataTypeName, entry);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NBTBase, E> DatatypeEntry<T, E> getDataType(@Nonnull Class<E> type) {
		String dataTypeName = type.getName();
		DatatypeEntry<?, ?> entry = datatypeRegistry.get(dataTypeName);
		if( entry == null )		// TODO: Make a new exception class
			throw new IllegalStateException("Datatype '" + dataTypeName + "' is not existing.");
		if( !entry.getClass().equals(type) )  // NOTE: Keep it as IllegalStateException
			throw new IllegalStateException("Datatype '" + dataTypeName + "' is not compatible with class '" + type + "'.");

		return (DatatypeEntry<T, E>)entry;
	}

	////////////////
	
	public static class DatatypeEntry<T extends NBTBase, E> {
		
		private final String dataTypeName;
		private final Class<E> dataTypeClazz;
		private final Class<T> storageTypeClazz;
		private final Process<T, E> ioProcess;

		public  DatatypeEntry(String dataTypeName, Class<E> dataTypeClazz, Class<T> storageTypeClazz, Process<T, E> ioProcess) {
			this.dataTypeName = dataTypeName;
			this.dataTypeClazz = dataTypeClazz;
			this.storageTypeClazz = storageTypeClazz;
			this.ioProcess = ioProcess;
		}

		public String getDataTypeName() {
			return dataTypeName;
		}

		public Class<?> getDataTypeClazz() {
			return dataTypeClazz;
		}
		
		public Class<?> getStorageTypeClazz() {
			return storageTypeClazz;
		}

		@SuppressWarnings("unchecked")
		public NBTBase serialize(@Nullable Object object) {
			if( !dataTypeClazz.equals(object.getClass()) ) // TODO: Make a new exception class
				throw new IllegalStateException("Object to serialize must be of class '" + dataTypeClazz + "'");
			return ioProcess.serialize((E)object);
		}
		
		@SuppressWarnings("unchecked")
		public <F> F deserialize(@Nullable World world, @Nonnull NBTBase object, Class<F> expectedTypeClazz) {
			if( !expectedTypeClazz.isAssignableFrom(this.dataTypeClazz) ) // TODO: Make a new exception class
				throw new IllegalStateException("Object to deserialize must be at least of class '" + this.dataTypeClazz + "'");
			return (F)ioProcess.deserialize(world, (T)object);
		}
	}
	
	////////////////
	
	public interface Process<T extends NBTBase, E> {
		@Nonnull
		T serialize(@Nullable E object);

		@Nullable
		E deserialize(@Nullable World world, @Nonnull T object);
	}
}
