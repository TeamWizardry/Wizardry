package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleParameter;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler.OverrideMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A factory object to create new module instances based on passed parameter sets.
 * Annotations like {@link ModuleParameter} are used to identify linked parameters.
 * 
 * @author Avatair
 */
public class ModuleFactory {
	private final Class<? extends IModule> clazz;
	private final HashMap<Map<String, Object>, IModule> instances = new HashMap<>();
	private final HashMap<String, Field> configurableFields = new HashMap<>();
	private final HashMap<String, OverrideMethod> overridableMethods = new HashMap<>();
	private final String referenceModuleID;
	
	ModuleFactory(String referenceModuleID, Class<? extends IModule> clazz) throws ModuleInitException {
		// NOTE: Instanciable only from ModuleRegistry
		this.clazz = clazz;
		this.referenceModuleID = referenceModuleID;
		
		// Determine configurable fields via reflection
		for(Field field : clazz.getFields()) {
			ModuleParameter cfg = field.getDeclaredAnnotation(ModuleParameter.class);
			if( cfg == null )
				continue;
			try {
				field.setAccessible(true);
			}
			catch(SecurityException e) {
				throw new ModuleInitException("Failed to acquire reflection access to field '" + field.toString() + "', annotated by @ModuleParameter.", e);
			}
			configurableFields.put(cfg.value(), field);
		}

		// Determine overrides
		overridableMethods.putAll(ModuleOverrideHandler.getOverrideMethodsFromClass(clazz, true));
	}
	
	/**
	 * Returns the associated class object of the regarded module.
	 * 
	 * @return the associated class object.
	 */
	public Class<? extends IModule> getModuleClass() {
		return clazz;
	}
	
	/**
	 * Returns the ID of the module class, as provided by {@link RegisterModule}.
	 * 
	 * @return the reference module ID
	 */
	public String getReferenceModuleID() {
		return referenceModuleID;
	}
	
	/**
	 * Returns a map containing all overridable methods within the module.
	 * 
	 * @return a map, which associates an override name with a method.
	 */
	public Map<String, OverrideMethod> getOverrides() {
		return Collections.unmodifiableMap(overridableMethods);
	}
	
	/**
	 * Checks whether a given configuration field exists.
	 * 
	 * @param key the name of the configuration field.
	 * @return <code>true</code> iff yes.
	 */
	public boolean hasConfigField(String key) {
		return configurableFields.containsKey(key);
	}
	
	/**
	 * Checks whether the module has methods, which can be overridden.
	 * 
	 * @return <code>true</code> iff yes.
	 */
	public boolean hasOverrides() {
		return !overridableMethods.isEmpty();
	}

	/**
	 * Returns an instance of the module implementation having no parameters. Creates a new one if not existing.
	 * 
	 * @return the instance.
	 * @throws ModuleInitException if something went wrong when creating the instance or some parameters weren't linked successfully.
	 */
	public IModule getInstance() throws ModuleInitException {
		return getInstance(new HashMap<>());
	}
	
	/**
	 * Returns an instance of the module implementation using given parameters. Creates a new one if not existing. <br/>
	 * <b>NOTE</b>: An instance is created only once for a given parameter set. <br />
	 * <b>NOTE</b>: Every parameter must be linked exactly once.
	 * 
	 * @param params a map providing values for every parameter.  
	 * @return the instance.
	 * @throws ModuleInitException if something went wrong when creating the instance or some parameters weren't linked successfully.
	 */
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
						throw new ModuleInitException("Unknown enumeration value '" + strValue + "' for field '" + cfgField.getKey() + "' in '" + referenceModuleID + "'.");
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
							throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + referenceModuleID + "'. Configuration is a number.");
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
							throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + referenceModuleID + "'. Configuration is a boolean.");
					}
					else
						throw new ModuleInitException("Incompatible datatype for field '" + cfgField.getKey() + "' in '" + referenceModuleID + "'. Configuration has an unknown value type.");
				}
				countFields ++;
			}
			
			// Check that all fields are mapped.
			if( countFields != configurableFields.size() ) {
				// NOTE: Throw an exception instead
				throw new ModuleInitException("Unknown field mappings exist for module '" + referenceModuleID + "'.");
			}
		} catch (Exception e) {
			throw new ModuleInitException("Couldn't initialize module '" + referenceModuleID + "'. See cause.", e);
		}
		
		instances.put(params, module);
		return module;
	}
}
