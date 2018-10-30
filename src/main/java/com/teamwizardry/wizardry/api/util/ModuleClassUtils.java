package com.teamwizardry.wizardry.api.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;
import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideException;

public class ModuleClassUtils {
	private ModuleClassUtils() {}
	
	public static Map<String, Method> getOverridableModuleMethods(Class<?> clazz, boolean isInterface) throws ModuleOverrideException {
		HashMap<String, Method> overridableMethods = new HashMap<>();

		Class<? extends Annotation> targetAnnotation = isInterface ? ModuleOverrideInterface.class : ModuleOverride.class;
		
		for(Method method : clazz.getMethods()) {
			Annotation ovrd = method.getDeclaredAnnotation(targetAnnotation);
			if( ovrd == null )
				continue;
			if( !method.isAccessible() )
				throw new ModuleOverrideException("Method '" + method.toString() + "' is annotated by @ModuleOverride but is unaccessible.");
			
			// TODO: Check if first argument is a valid invoke helper
			
			String overrideName = isInterface ? ((ModuleOverrideInterface)ovrd).value() : ((ModuleOverride)ovrd).value();
			overridableMethods.put(overrideName, method);
		}
		
		return overridableMethods;
	}
}
