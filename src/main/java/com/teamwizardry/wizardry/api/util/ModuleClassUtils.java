package com.teamwizardry.wizardry.api.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.module.ModuleInitException;

public class ModuleClassUtils {
	private ModuleClassUtils() {}
	
	public static Map<String, Method> getOverridableModuleMethods(Class<?> clazz) throws ModuleInitException {
		HashMap<String, Method> overridableMethods = new HashMap<>();
		
		for(Method method : clazz.getMethods()) {
			ModuleOverride ovrd = method.getDeclaredAnnotation(ModuleOverride.class);
			if( ovrd == null )
				continue;
			if( !method.isAccessible() )
				throw new ModuleInitException("Method '" + method.toString() + "' is annotated by @ModuleOverride but is unaccessible.");
			
			// TODO: Check if first argument is a valid invoke helper
			
			overridableMethods.put(ovrd.value(), method);
		}
		
		return overridableMethods;
	}
}
