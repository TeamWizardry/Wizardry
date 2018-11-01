package com.teamwizardry.wizardry.api.spell.module;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextSuper;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry.OverrideDefaultMethod;

public class ModuleOverrideHandler {
	
	private HashMap<String, OverridePointer> overridePointers = new HashMap<>();
	private HashMap<String, Object> cachedProxies = new HashMap<>();
	private final SpellRing spellChain;

	public ModuleOverrideHandler(SpellRing spellChain) throws ModuleOverrideException {
		this.spellChain = spellChain;
		if( spellChain.getParentRing() != null )
			throw new IllegalArgumentException("passed spellRing is not a root.");
		
		// Apply default overrides
		for( OverrideDefaultMethod methodEntry : ModuleRegistry.INSTANCE.getDefaultOverrides().values() ) {
			applyDefaultOverride(methodEntry);
		}
		
		// Apply overrides from spell chain
		SpellRing[] spellSequence = getSequenceFromSpellChain(spellChain);
		for( SpellRing curRing : spellSequence )
			applyModuleOverrides(curRing);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T getConsumerInterface(Class<T> interfaceClass) throws ModuleOverrideException {
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
		Map<String, Method> overridableMethods = getInterfaceMethods(interfaceClass);
		
		// Create invocation handler. All interface methods are mapped to their base method pendants
		OverrideInvoker invocationHandler = new OverrideInvoker(overridableMethods, interfaceClass.getName());
		
		//
		ClassLoader myClassLoader = getClass().getClassLoader();	// Inherit class loader from this class
		Class<?>[] proxyInterfaces = new Class<?>[] { interfaceClass };
		
		@SuppressWarnings("unchecked")
		T proxy = (T)Proxy.newProxyInstance(myClassLoader, proxyInterfaces, invocationHandler);
		
		return proxy;
	}
	
	private void applyModuleOverrides( SpellRing spellRing ) throws ModuleOverrideException {
		ModuleInstance module = spellRing.getModule();
		Map<String, OverrideMethod> overrides = module.getFactory().getOverrides();
		
		for( Entry<String, OverrideMethod> entry : overrides.entrySet() ) {
			OverridePointer ptr = overridePointers.get(entry.getKey());
			if( ptr == null ) {
				ptr = new OverridePointer(spellRing, null, entry.getKey(), entry.getValue());
			}
			else {
				if( !areMethodsCompatible(ptr.getBaseMethod().getMethod(), entry.getValue().getMethod()) )
					throw new ModuleOverrideException("Method '" + ptr.getBaseMethod() + "' can't be overridden by '" + entry.getValue() + "' due to incompatible signature.");
				ptr = new OverridePointer(spellRing, ptr, entry.getKey(), entry.getValue());
			}
			
			overridePointers.put(entry.getKey(), ptr);			
		}
	}
	
	private void applyDefaultOverride( OverrideDefaultMethod methodEntry ) {
		if( overridePointers.containsKey(methodEntry.getOverrideName()) )
			throw new IllegalStateException("Duplicate override found.");	// Should not happen, as duplication cases are catched in ModuleRegistry.registerOverrideDefaults()
		OverridePointer ptr = new OverridePointer(methodEntry);
	}
	
	/////////////////
	
	private static SpellRing[] getSequenceFromSpellChain(SpellRing spellRing) {
		SpellRing cur = spellRing;
		LinkedList<SpellRing> instances = new LinkedList<>();
		while( cur != null ) {
			ModuleInstance module = cur.getModule();
			if( module.getFactory().hasOverrides() ) {
				instances.add(cur);
			}			
			cur = cur.getChildRing();
		}
		
		return instances.toArray(new SpellRing[instances.size()]);
	}
	
	private static boolean areMethodsCompatible(Method baseMtd, Method overrideMtd) {
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
		Parameter[] baseParams = baseMtd.getParameters();
		Parameter[] overrideParams = overrideMtd.getParameters();

		int i = 0, j = 0;
		while( i < baseParams.length || j < baseParams.length ) {
			if( i >= baseParams.length ) {
				while( j < overrideParams.length ) {
					Parameter overrideParam = overrideParams[j];
					if( !isExtraParameter( overrideParam ) )
						return false;	// Unmappable extra parameter.
					j ++;
				}
				break;
			}
			
			if( j >= overrideParams.length ) {
				while( i < overrideParams.length ) {
					Parameter baseParam = baseParams[i];
					if( !isExtraParameter( baseParam ) )
						return false;	// Unmappable extra parameter.
					i ++;
				}
				break;
			}
			
			// 
			Parameter baseParam = baseParams[i];
			if( isExtraParameter( baseParam ) ) {
				i ++;
				continue;	// Ignore parameters taking values from context
			}
			
			Parameter overrideParam = overrideParams[j];
			if( isExtraParameter( overrideParam ) ) {
				j ++;
				continue;	// Ignore parameters taking values from context
			}
			
			if( !baseParam.getType().isAssignableFrom(overrideParam.getType()) )
				return false;
			
			i ++;
			j ++;
		}
		
		// Check compatibility of exceptions
		Class<?>[] baseExcps = baseMtd.getExceptionTypes();
		Class<?>[] overrideExcps = overrideMtd.getExceptionTypes();
		
		// For every checked exception at the interface method
		// there should exist an exception type at base
		// which is assignable from the interface method exception
		for( Class<?> overrideExcp : overrideExcps ) {
			if( RuntimeException.class.isAssignableFrom(overrideExcp) )
				continue;
			
			boolean found = false;
			for( Class<?> baseExcp : baseExcps ) {
				if( RuntimeException.class.isAssignableFrom(baseExcp) )
					continue;
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
	
	private static boolean isExtraParameter(Parameter param) {
		return param.isAnnotationPresent(ContextRing.class) ||
			   param.isAnnotationPresent(ContextSuper.class);
	}
	
	static HashMap<String, OverrideMethod> getOverrideMethodsFromClass(Class<?> clazz, boolean hasContext) throws ModuleInitException {
		HashMap<String, OverrideMethod> overridableMethods = new HashMap<>();
		// Determine overriden methods via reflection
		// FIXME: Separation of concerns: Overridable methods are not part of factory. Move them or rename factory class appropriately.
		// TODO: Check for ambiguity of method names. Handle overrides by superclass properly!
		for(Method method : clazz.getMethods()) {
			ModuleOverride ovrd = method.getDeclaredAnnotation(ModuleOverride.class);
			if( ovrd == null )
				continue;
			
			try {
				method.setAccessible(true);
			}
			catch(SecurityException e) {
				throw new ModuleInitException("Failed to aquire reflection access to method '" + method.toString() + "', annotated by @ModuleOverride.", e);
			}
			
			// Search for context parameters
			int idxContextParamRing = -1;
			int idxContextParamSuper = -2;
			Parameter[] params = method.getParameters();
			for( int i = 0; i < params.length; i ++ ) {
				Parameter param = params[i];
				if( param.isAnnotationPresent(ContextRing.class) ) {
					if( idxContextParamRing >= 0 )
						throw new ModuleInitException("Method '" + method.toString() + "' has invalid @ContextRing annotated parameter. It is not allowed on multiple parameters.");
					idxContextParamRing = i;
				}
				if( param.isAnnotationPresent(ContextSuper.class) ) {
					if( idxContextParamSuper >= 0 )
						throw new ModuleInitException("Method '" + method.toString() + "' has invalid @ContextSuper annotated parameter. It is not allowed on multiple parameters.");
					idxContextParamSuper = i;
				}
			}
			
			if( !hasContext ) {
				if( idxContextParamRing >= 0 || idxContextParamSuper >= 0 )
					throw new ModuleInitException("Context parameters are not allowed.");
			}
			
			if( idxContextParamRing == idxContextParamSuper )
				throw new ModuleInitException("Method '" + method.toString() + "' has a parameter which is annotated with multiple roles.");
			
			OverrideMethod ovrdMethod = new OverrideMethod(method, idxContextParamRing, idxContextParamSuper);
			overridableMethods.put(ovrd.value(), ovrdMethod);
		}
		
		return overridableMethods;
	}
	
	/////////////////
	
	private class OverrideInvoker implements InvocationHandler {
		private final HashMap<String, OverrideInterfaceMethod> callMap = new HashMap<>();
		private final String displayedInterfaceName;
		
		public OverrideInvoker(Map<String, Method> interfaceMethods, String displayedInterfaceName) throws ModuleOverrideException {
			this.displayedInterfaceName = displayedInterfaceName;
			
			for( Entry<String, Method> interfaceMethod : interfaceMethods.entrySet() ) {
				OverridePointer ptr = overridePointers.get(interfaceMethod.getKey());
				if( ptr == null )
					continue;	// Ignore unmapped methods. invoke() will throw a proper exception on attempt to call them.
				if( !areMethodsCompatible(interfaceMethod.getValue(), ptr.getBaseMethod().getMethod()) )
					throw new ModuleOverrideException("Interface method signature of '" + interfaceMethod.getValue() + "' is incompatible with '" + ptr.getBaseMethod().getMethod() + "'.");
				
				OverrideInterfaceMethod intfMethodEntry = new OverrideInterfaceMethod(ptr, interfaceMethod.getValue());
				callMap.put(intfMethodEntry.getKey(), intfMethodEntry);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String name = method.getName();
			OverrideInterfaceMethod intfMethod = callMap.get(name);
			if( intfMethod == null ) {
				// Check for default methods
				// NOTE: Hopefully monitor method calls are not redirected to this invoke handler.
				if( isMethodToString(method) ) {
					return "Proxy for '" + displayedInterfaceName + "' on " + spellChain;
				}
				else if( isMethodHashCode(method) ) {
					return (int)0;
				}
				else if( isMethodEquals(method) ) {
					return proxy == args[0];
				}
				else if( isMethodClone(method) ) {
					throw new CloneNotSupportedException("Override handler has no clone ability.");
				}
				else {
					// Nothing found. Throw an exception.
					ModuleOverrideInterface annot = method.getDeclaredAnnotation(ModuleOverrideInterface.class);
					if( annot != null )
						throw new UnsupportedOperationException("Override method for '" + annot.value() + "' invoke via '" + method + "' is not implemented or not public.");
					else
						throw new UnsupportedOperationException("Method '" + method + "' is not an override. Annotation @ModuleOverrideInterface must be supplied.");
				}
			}
			
			OverridePointer ptr = intfMethod.getOverridePointer();
			return ptr.invoke(args);
		}
		
		private boolean isMethodHashCode(Method method) {
			// TODO: Move to utils.
			if( method == null )
				return false;
			
			if( method.getParameterCount() != 0 )
				return false;
			if( !int.class.equals(method.getReturnType()) )
				return false;
			if( !"hashCode".equals(method.getName()) )
				return false;
			return true;
		}
		
		private boolean isMethodEquals(Method method) {
			// TODO: Move to utils.
			if( method == null )
				return false;

			if( method.getParameterCount() != 1 )
				return false;
			if( !method.getParameterTypes()[0].equals(Object.class) )
				return false;
			if( !boolean.class.equals(method.getReturnType()) )
				return false;
			if( !"equals".equals(method.getName()) )
				return false;
			return true;
		}
		
		private boolean isMethodToString(Method method) {
			// TODO: Move to utils.
			if( method == null )
				return false;
			
			if( method.getParameterCount() != 0 )
				return false;
			if( !String.class.equals(method.getReturnType()) )
				return false;
			if( !"toString".equals(method.getName()) )
				return false;
			return true;
		}
		
		private boolean isMethodClone(Method method) {
			// TODO: Move to utils.
			if( method == null )
				return false;
			
			if( method.getParameterCount() != 0 )
				return false;
			if( !Object.class.equals(method.getReturnType()) )
				return false;
			if( !"clone".equals(method.getName()) )
				return false;
			return true;
		}
	}
	
	////////////////////////
	
	public static Map<String, Method> getInterfaceMethods(Class<?> clazz) throws ModuleOverrideException {
		HashMap<String, Method> overridableMethods = new HashMap<>();

		// TODO: Check for ambiguity of method names. Handle overrides by superclass properly!
		
		for(Method method : clazz.getMethods()) {
			ModuleOverrideInterface ovrd = method.getDeclaredAnnotation(ModuleOverrideInterface.class);
			if( ovrd == null )
				continue;
			
			try {
				method.setAccessible(true);
			}
			catch(SecurityException e) {
				throw new ModuleOverrideException("Failed to aquire reflection access to method '" + method.toString() + "', annotated by @ModuleOverrideInterface.", e);
			}
			
			overridableMethods.put(ovrd.value(), method);
		}
		
		return overridableMethods;
	}
	
	////////////////////////
	
	static class OverridePointer {
		private final Object object;
		private final SpellRing spellRingWithOverride;
		private final String overrideName;
		private final OverrideMethod baseMethod;
		private final OverridePointer prev;
		
		OverridePointer(SpellRing spellRingWithOverride, OverridePointer prev, String overrideName, OverrideMethod baseMethod) {
			this.spellRingWithOverride = spellRingWithOverride;
			this.baseMethod = baseMethod;
			this.overrideName = overrideName;
			this.prev = prev;
			this.object = getModule().getModuleClass();
		}
		
		public OverridePointer(OverrideDefaultMethod methodEntry) {
			this.spellRingWithOverride = null;
			this.baseMethod = methodEntry.getMethod();
			this.overrideName = methodEntry.getOverrideName();
			this.prev = null;
			this.object = methodEntry.getObj();
		}

		SpellRing getSpellRingWithOverride() {
			return spellRingWithOverride;
		}
		
		OverrideMethod getBaseMethod() {
			return this.baseMethod;
		}
		
		ModuleInstance getModule() {
			if( spellRingWithOverride == null )
				return null;
			return spellRingWithOverride.getModule();
		}
		
		OverridePointer getPrev() {
			return prev;
		}
		
		String getOverrideName() {
			return overrideName;
		}
		
		Object invoke(Object[] args) throws Throwable {
			int idxContextParamRing = baseMethod.getIdxContextParamRing();
			int idxContextParamSuper = baseMethod.getIdxContextParamSuper();
			
			Object passedArgs[] = args;
			int countExtra = 1;
			if( idxContextParamRing >= 0 )
				countExtra ++;
			if( idxContextParamSuper >= 0 )
				countExtra ++;
			
			// Add extra arguments like this pointer a.s.o.
			passedArgs = new Object[args.length + countExtra];
			int i = 0;
			int j = 0;
			while( i < passedArgs.length ) {
				if( i == 0 ) {
					passedArgs[i] = object;
				}
				else if( i == idxContextParamRing + 1 ) {
					passedArgs[i] = spellRingWithOverride;
				}
				else if( i == idxContextParamSuper + 1 ) {
					passedArgs[i] = new ModuleOverrideSuper(prev);
				}
				else {
					passedArgs[i] = args[j];
					j ++;
				}
				i ++;
			}
			
			try {
				return baseMethod.getMethodHandle().invokeWithArguments(passedArgs);
			}
			catch(WrongMethodTypeException | ClassCastException e) {
				// NOTE: If this happens, then correctness of checks like "areMethodsCompatible()" a.s.o. need to be debugged.
				throw new IllegalStateException("Couldn't invoke call. See cause.", e);
			}
		}
	}
	
	private static class OverrideInterfaceMethod {
		private final OverridePointer overridePointer;
		private final Method interfaceMethod;
		
		OverrideInterfaceMethod(OverridePointer overridePointer, Method interfaceMethod) {
			super();
			this.overridePointer = overridePointer;
			this.interfaceMethod = interfaceMethod;
		}

		OverridePointer getOverridePointer() {
			return overridePointer;
		}

		Method getInterfaceMethod() {
			return interfaceMethod;
		}
		
		String getKey() {
			return interfaceMethod.getName();
		}
	}
	
	static class OverrideMethod {
		private final Method method;
		private final MethodHandle methodHandle;
		private final int idxContextParamRing;
		private final int idxContextParamSuper;
		
		OverrideMethod(Method method, int idxContextParamRing, int idxContextParamSuper) throws ModuleInitException {
			super();

			try {
				this.method = method;
				this.methodHandle = MethodHandles.lookup().unreflect(method);

				// NOTE: Parameter indices should be less or equal to -2 if missing to make ModuleOverrideHandler.OverridePointer.invoke work correctly
				this.idxContextParamRing = idxContextParamRing >= 0 ? idxContextParamRing : -2;
				this.idxContextParamSuper = idxContextParamSuper >= 0 ? idxContextParamSuper : -2;
			} catch (Exception e) {
				throw new ModuleInitException("Couldn't initialize override method binding. See cause.", e);
			}
		}

		Method getMethod() {
			return method;
		}
		
		MethodHandle getMethodHandle() {
			return methodHandle;
		}

		int getIdxContextParamRing() {
			return idxContextParamRing;
		}
		
		int getIdxContextParamSuper() {
			return idxContextParamSuper;
		}
	}
}
