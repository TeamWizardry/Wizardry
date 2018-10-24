package com.teamwizardry.wizardry.api.spell.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleParameter;

public class ModuleFactory {
	private final Class<? extends IModule> clazz;
	private final HashMap<Map<String, Object>, IModule> instances = new HashMap<>();
	private final HashMap<String, Field> configurableFields = new HashMap<>();
	private final String moduleClassID;
	
	ModuleFactory(String moduleClassID, Class<? extends IModule> clazz) throws ModuleInitException {
		// NOTE: Instanciable only from ModuleRegistry
		this.clazz = clazz;
		this.moduleClassID = moduleClassID;
		
		// Determine configurable fields via reflection
		for(Field field : clazz.getDeclaredFields()) {
			ModuleParameter cfg = field.getDeclaredAnnotation(ModuleParameter.class);
			if( cfg == null )
				continue;
			
			// Add configuration
			configurableFields.put(cfg.value(), field);
		}
	}
	
	public Class<? extends IModule> getModuleClass() {
		return clazz;
	}
	
	public String getClassID() {
		return moduleClassID;
	}
	
	public boolean hasConfigField(String key) {
		return configurableFields.containsKey(key);
	}

	public IModule getInstance() throws ModuleInitException {
		return getInstance(new HashMap<>());
	}
	
	public IModule getInstance(HashMap<String, Object> params) throws ModuleInitException {
		IModule module = instances.get(params);
		if( module != null )
			return module;
		
		try {
			Constructor<?> ctor = clazz.getConstructor();
			module = (IModule)ctor.newInstance();
			
			// Assign parameter values. Exactly every field must be assigned.
			// Default instance is always created to retrieve at least spell class ID name.
			int countFields = 0;
			for( Entry<String, Field> cfgField : configurableFields.entrySet() ) {
				Object value = params.get(cfgField.getKey());
				if( value == null )
					throw new IllegalArgumentException("Field value for '" + cfgField.getKey() + "' is not mapped in configuration.");
				
				Field field = cfgField.getValue();
				if( Enum.class.isAssignableFrom(field.getType()) ) {
					@SuppressWarnings("unchecked")
					Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>)field.getType();
					Enum<?>[] enumConstants = enumType.getEnumConstants();
					
					boolean found = false;
					String strValue = value.toString();
					for( Enum<?> cnst : enumConstants ) {
						if( cnst.toString().equalsIgnoreCase(strValue) ) {
							field.set(module, cnst);
							found = true;
							break;
						}
					}
					
					if( !found )
						throw new ModuleInitException("Unknown enumeration value '" + strValue + "' for field '" + cfgField.getKey() + "' in '" + moduleClassID + "'.");
				}
				else {
					if( value instanceof Number ) {
						Number number = (Number)value;
						if( field.getType().equals(int.class) || field.getType().equals(Integer.class) )
							field.set(module, number.intValue());
						else if( field.getType().equals(long.class) || field.getType().equals(Long.class) )
							field.set(module, number.longValue());
						else if( field.getType().equals(float.class) || field.getType().equals(Float.class) )
							field.set(module, number.floatValue());
						else if( field.getType().equals(double.class) || field.getType().equals(Double.class) )
							field.set(module, number.doubleValue());
						else if( field.getType().equals(byte.class) || field.getType().equals(Byte.class) )
							field.set(module, number.byteValue());
						else if( field.getType().equals(String.class) )
							field.set(module, number.toString());
						else
							throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + moduleClassID + "'. Configuration is a number.");
					}
					else if( value instanceof String )
						field.set(module, value);
					else if( value instanceof Boolean ) {
						Boolean bval = (boolean)value;
						if( field.getType().equals(boolean.class) || field.getType().equals(Boolean.class) )
							field.set(module, bval);
						else if( field.getType().equals(String.class) )
							field.set(module, bval.toString());
						else
							throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + moduleClassID + "'. Configuration is a boolean.");
					}
					else
						throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + moduleClassID + "'. Configuration has an unknown value type.");
				}
				countFields ++;
			}
			
			// Check that all fields are mapped.
			if( countFields != configurableFields.size() ) {
				// NOTE: Throw an exception instead
				throw new ModuleInitException("Unknown field mappings exist for module '" + moduleClassID + "'.");
			}
		} catch (Exception e) {
			throw new ModuleInitException("Couldn't initialize module '" + moduleClassID + "'. See cause.", e);
		}
		
		instances.put(params, module);
		return module;
	}
}
