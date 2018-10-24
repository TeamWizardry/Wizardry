package com.teamwizardry.wizardry.api.spell.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
			ConfigField cfg = field.getDeclaredAnnotation(ConfigField.class);
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
			// TODO: Handle datatypes correctly
			int countFields = 0;
			for( Entry<String, Object> entry : params.entrySet() ) {
				Field field = configurableFields.get(entry.getKey());
				if( field == null )
					throw new IllegalArgumentException("Field for configuration '" + entry.getKey() + "' is not existing.");
				
				field.set(module, entry.getValue());
				countFields ++;
			}
			
			//
			if( countFields != configurableFields.size() ) {
				// NOTE: Throw an exception instead
				throw new ModuleInitException("Not all configuration fields are mapped for module '" + moduleClassID + "'.");
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new ModuleInitException("Couldn't initialize module '" + moduleClassID + "'. See cause.", e);
		}
		
		instances.put(params, module);
		return module;
	}
}
