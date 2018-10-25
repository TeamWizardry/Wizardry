package com.teamwizardry.wizardry.api.spell.module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.util.ModuleClassUtils;

public class ModuleOverrideHandler {
	
	private HashMap<String, OverridePointer> overridePointers = new HashMap<>();
	private HashMap<String, Object> cachedProxies = new HashMap<>();

	public ModuleOverrideHandler(ModuleInstance[] spellSequence) throws ModuleOverrideException {
		for( ModuleInstance module : spellSequence )
			applyOverrides(module);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getConsumerInterface(Class<T> interfaceClass) throws ModuleOverrideException {
		String className = interfaceClass.getName();
		Object obj = cachedProxies.get(className);
		if( obj == null ) {
			T newProxy = createConsumerInterface(interfaceClass);
			cachedProxies.put(className, newProxy);
			return newProxy;
		}
		
		// check for interface compatibility
		if( !interfaceClass.isInstance(obj) )
			throw new IllegalStateException("Incompatible interface class with matching name. Class loader different?");
		return (T)obj;
	}
	
	private <T> T createConsumerInterface(Class<T> interfaceClass) throws ModuleOverrideException {
		// Retrieve all overridable methods and check them for compatibility with base class
		Map<String, Method> overridableMethods = ModuleClassUtils.getOverridableModuleMethods(interfaceClass);
		
		// Create invocation handler. All interface methods are mapped to their base method pendants
		OverrideInvoker invocationHandler = new OverrideInvoker(overridableMethods);
		
		//
		ClassLoader myClassLoader = getClass().getClassLoader();	// Inherit class loader from this class
		Class<?>[] proxyInterfaces = new Class<?>[] { interfaceClass };
		
		@SuppressWarnings("unchecked")
		T proxy = (T)Proxy.newProxyInstance(myClassLoader, proxyInterfaces, invocationHandler);
		
		return proxy;
	}
	
	private void applyOverrides( ModuleInstance module ) throws ModuleOverrideException {
		Map<String, Method> overrides = module.getFactory().getOverrides();
		
		for( Entry<String, Method> entry : overrides.entrySet() ) {
			OverridePointer ptr = overridePointers.get(entry.getKey());
			if( ptr == null ) {
				ptr = new OverridePointer(null, module, entry.getKey(), entry.getValue());
			}
			else {
				if( !areMethodsCompatible(ptr.getBaseMethod(), entry.getValue()) )
					throw new ModuleOverrideException("Method '" + ptr.getBaseMethod() + "' can't be overridden by '" + entry.getValue() + "' due to incompatible signature.");
				ptr = new OverridePointer(ptr, module, entry.getKey(), entry.getValue());
			}
			
			overridePointers.put(entry.getKey(), ptr);			
		}
	}
	
	/////////////////
	
	public static ModuleInstance[] getSequenceFromSpellRing(SpellRing spellRing) {
		SpellRing cur = spellRing;
		LinkedList<ModuleInstance> instances = new LinkedList<>();
		while( cur != null ) {
			ModuleInstance module = cur.getModule();
			if( !module.getFactory().hasOverrides() )
				continue;
			instances.add(module);
			cur = cur.getChildRing();
		}
		
		return instances.toArray(new ModuleInstance[instances.size()]);
	}
	
	private static boolean areMethodsCompatible(Method baseMtd, Method overrideMtd) {
		// TODO: Move to reflection utils.
		
		// WARNING: Update this method, if language conventions in java change. 
		
		// Check compatibility of return types
		Class<?> baseReturnType = baseMtd.getReturnType();
		Class<?> overrideReturnType = overrideMtd.getReturnType();
		if( baseReturnType == null ) {
			if( overrideReturnType != null )
				return false;
		}
		else {
			if( overrideReturnType == null )
				return false;
			if( !baseReturnType.isAssignableFrom(overrideReturnType) )
				return false;
		}
		
		// Check compatibility of parameters
		int countParams = baseMtd.getParameterCount();
		if( countParams != overrideMtd.getParameterCount() )
			return false;		
		
		Parameter[] baseParams = baseMtd.getParameters();
		Parameter[] overrideParams = overrideMtd.getParameters();
		
		for( int i = 0; i < countParams; i ++ ) {
			Parameter baseParam = baseParams[i];
			Parameter overrideParam = overrideParams[i];
			if( !baseParam.getType().isAssignableFrom(overrideParam.getType()) )
				return false;
		}
		
		// Check compatibility of exceptions
		Class<?>[] baseExcps = baseMtd.getExceptionTypes();
		Class<?>[] overrideExcps = overrideMtd.getExceptionTypes();
		
		// For every checked exception at the interface method
		// there should exist an exception type at base
		// which is assignable from the interface method exception
		// TODO: Maybe ignore unchecked exceptions?
		for( Class<?> overrideExcp : overrideExcps ) {
			boolean found = false;
			for( Class<?> baseExcp : baseExcps ) {
				if( baseExcp.isAssignableFrom(overrideExcp) ) {
					found = true;
					break;
				}
			}
			if( !found )
				return false;
		}

		return true;		
	}
	
	/////////////////
	
	private class OverrideInvoker implements InvocationHandler {
		private final HashMap<String, OverridePointer> callMap = new HashMap<>();

		public OverrideInvoker(Map<String, Method> overrides) throws ModuleOverrideException {
			for( Entry<String, Method> override : overrides.entrySet() ) {
				OverridePointer ptr = overridePointers.get(override.getKey());
				if( ptr == null )
					throw new ModuleOverrideException("Override with name '" + override.getKey() + "' referenced by '" + override.getValue()+"' is not existing.");
				callMap.put(override.getValue().getName(), ptr);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String name = method.getName();
			OverridePointer ptr = callMap.get(name);
			if( ptr == null )
				throw new UnsupportedOperationException("Method '" + method + "' is not declared as override.");

			try {
				return ptr.getBaseMethod().invoke(ptr.getModule().getModuleClass(), args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// NOTE: If this happens, then correctness of checks like "areMethodsCompatible()" a.s.o. need to be debugged.
				e.printStackTrace();
				throw new IllegalStateException("Couldn't invoke call. See cause.", e);
			}
		}
	}
	
	private static class OverridePointer {
		private final String overrideName;
		private final Method baseMethod;
		private final ModuleInstance module;
		private final OverridePointer prev;
		
		OverridePointer(OverridePointer prev, ModuleInstance module, String overrideName, Method baseMethod) {
			this.baseMethod = baseMethod;
			this.module = module;
			this.overrideName = overrideName;
			this.prev = prev;
		}
		
		Method getBaseMethod() {
			return this.baseMethod;
		}
		
		ModuleInstance getModule() {
			return module;
		}
		
		OverridePointer getPrev() {
			return prev;
		}
		
		String getOverrideName() {
			return overrideName;
		}
	}
}
