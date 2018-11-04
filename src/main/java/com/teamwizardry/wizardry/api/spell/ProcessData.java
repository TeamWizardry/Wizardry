package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterDataType;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;

/**
 * Created by Demoniaque.
 */
public class ProcessData {
	public static final ProcessData INSTANCE = new ProcessData();
	
	private HashMap<String, DatatypeEntry<?,?>> datatypeRegistry = new HashMap<>();
	
	private ProcessData() {}

	public <T extends NBTBase, E> void registerDataType(@Nonnull Class<E> type, @Nonnull Class<T> storageType, @Nonnull Process<T, E> process) throws DataInitException {
		String dataTypeName = type.getName();
		if( datatypeRegistry.containsKey(dataTypeName) )
			throw new DataInitException("Datatype '" + dataTypeName + "' is already registered.");
		
		DatatypeEntry<T,E> entry = new DatatypeEntry<>(dataTypeName, type, storageType, process);
		datatypeRegistry.put(dataTypeName, entry);
	}
	
	@SuppressWarnings("unchecked")
	public <E> DataType<E> getDataType(@Nonnull Class<E> type) {
		String dataTypeName = type.getName();
		DatatypeEntry<?, ?> entry = datatypeRegistry.get(dataTypeName);
		if( entry == null )
			throw new DataSerializationException("Datatype '" + dataTypeName + "' is not existing.");
		if( !entry.getClass().equals(type) )  // NOTE: Keep it as IllegalStateException
			throw new IllegalStateException("Datatype '" + dataTypeName + "' is not compatible with class '" + type + "'.");

		return (DatatypeEntry<?, E>)entry;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registerAnnotatedDataTypes() {
		AnnotationHelper.INSTANCE.findAnnotatedClasses(LibrarianLib.PROXY.getAsmDataTable(), Process.class, RegisterDataType.class, (clazz, info) -> {
			String storageTypeClassName = info.getString("storageType");
			String dataTypeClassName = info.getString("dataType");
			
			// Lookup class names
			Class<? extends NBTBase> storageTypeClass;
			try {
				Class<?> storageTypeClass2 = Class.forName(storageTypeClassName);
				if( !NBTBase.class.isAssignableFrom(storageTypeClass2) ) {
					Wizardry.logger.error("Storage Class '" + storageTypeClassName + "' is not derived from NBTBase.");
					return null;
				}
				storageTypeClass = (Class<? extends NBTBase>)storageTypeClass2;
			}
			catch (ClassNotFoundException e) {
				Wizardry.logger.error("Storage Class '" + storageTypeClassName + "' not existing.", e);
				return null;
			}
			
			Class<?> dataTypeClass;
			try {
				dataTypeClass = Class.forName(dataTypeClassName);
			}
			catch(ClassNotFoundException e) {
				Wizardry.logger.error("Storage Class '" + dataTypeClassName + "' not existing.", e);
				return null;
			}
			
			// Instantiate and register
			try {
				Constructor<?>  ctor = clazz.getConstructor();
				Object object = ctor.newInstance();
				if( !(object instanceof Process) ) {
					Wizardry.logger.error("Data type class is not derived from ProcessData.Process");
					return null;
				}					
					
				registerDataType(dataTypeClass, storageTypeClass, (Process)object);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | DataInitException e) {
				Wizardry.logger.error("Something went wrong when creating instance of '" + clazz + "'.", e);
				return null;
			}

			return null;
		});
	}
	
	////////////////
	
	public static class DatatypeEntry<T extends NBTBase, E> implements DataType<E> {
		
		private final String dataTypeName;
		private final Class<E> dataTypeClazz;
		private final Class<T> storageTypeClazz;
		private final Process<T, E> ioProcess;

		public DatatypeEntry(String dataTypeName, Class<E> dataTypeClazz, Class<T> storageTypeClazz, Process<T, E> ioProcess) {
			this.dataTypeName = dataTypeName;
			this.dataTypeClazz = dataTypeClazz;
			this.storageTypeClazz = storageTypeClazz;
			this.ioProcess = ioProcess;
		}

		public String getDataTypeName() {
			return dataTypeName;
		}

		public Class<E> getDataTypeClazz() {
			return dataTypeClazz;
		}
		
		public Class<T> getStorageTypeClazz() {
			return storageTypeClazz;
		}

		@Override
		@Nonnull
		public NBTBase serialize(@Nullable E object) {
//			if( !dataTypeClazz.equals(object.getClass()) ) // TODO: Make a new exception class
//				throw new IllegalStateException("Object to serialize must be of class '" + dataTypeClazz + "'");
			return ioProcess.serialize(object);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		@Nonnull
		public E deserialize(@Nullable World world, @Nonnull NBTBase object) {
			if( !storageTypeClazz.equals(object.getClass()) )
				throw new DataSerializationException("Storage object to deserialize must be of class '" + storageTypeClazz + "'");
			Object obj = ioProcess.deserialize(world, (T)object);
			if( obj == null )
				throw new DataSerializationException("Deserialized object is null.");
			if( !dataTypeClazz.isInstance(obj) )
				throw new DataSerializationException("Deserialized object must be of class '" + dataTypeClazz + "', but is actually of '" + obj.getClass() + "'");
			return (E)obj;
		}
	}
	
	public interface DataType<E> {
		@Nonnull
		NBTBase serialize(@Nullable E object);
		
		@Nonnull
		E deserialize(@Nullable World world, @Nonnull NBTBase object);
	}
	
	////////////////
	
	public interface Process<T extends NBTBase, E> {
		@Nonnull
		T serialize(@Nullable E object);

		@Nullable
		E deserialize(@Nullable World world, @Nonnull T object);
	}
}

