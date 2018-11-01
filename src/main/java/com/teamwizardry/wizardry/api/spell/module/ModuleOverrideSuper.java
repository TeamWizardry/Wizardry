package com.teamwizardry.wizardry.api.spell.module;

import java.util.function.Consumer;

import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler.OverridePointer;

public class ModuleOverrideSuper {
	private final OverridePointer superPointer;
	private Exception occurredException;
	
	ModuleOverrideSuper(OverridePointer superPointer) {
		// NOTE: Is only created within ModuleOverrideHandler
		this.superPointer = superPointer;
	}
	
	public boolean hasSuper() {
		return superPointer != null;
	}
	
	public Object invoke(boolean throwRuntimeExceptionsImmediately, Object ... args) throws RuntimeException {
		try {
			return internalInvokeWithExceptions(args);
		}
		catch(Exception e) {
			if( throwRuntimeExceptionsImmediately ) {
				if( e instanceof RuntimeException )
					throw (RuntimeException)e;
			}
			occurredException = e;
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <ET extends Exception> void catchException(Class<ET> clazz, Consumer<ET> lambda) {
		if( occurredException == null )
			return;
		
		try {
			if( clazz.isInstance(occurredException) )
				lambda.accept((ET)occurredException);
		}
		finally {
			occurredException = null;
		}
	}
	
	public void rethrowUncatched() throws RuntimeException {
		if( occurredException == null )
			return;
		
		try {
			if( occurredException instanceof RuntimeException )
				throw (RuntimeException)occurredException;
			else
				throw new ModuleOverrideException("A checked exception has been thrown. See cause.", occurredException);
		}
		finally {
			occurredException = null;
		}
	}
	
	private Object internalInvokeWithExceptions(Object ... args) throws Exception {
		if( superPointer == null )
			throw new ModuleOverrideException("No super is existing.");
		
		try {
			return superPointer.invoke(args);
		}
		catch(Throwable e) {
			if( e instanceof Error )
				throw (Error)e;		// simply rethrow errors
			else if( e instanceof Exception )
				throw (Exception)e;
			else {
				// Should not happen unless a new throwable type has been introduced to Java.
				throw new IllegalStateException("An unknown throwable occurred. Either Exception or Error is expected.", e);
			}
		}
	}

}
