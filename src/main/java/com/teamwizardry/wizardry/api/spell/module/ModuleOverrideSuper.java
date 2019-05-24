package com.teamwizardry.wizardry.api.spell.module;

import com.teamwizardry.wizardry.api.spell.module.ModuleOverrideHandler.OverridePointer;

import java.util.function.Consumer;

/**
 * An object to perform invokes on an overwritten super method and to help handling exceptions. 
 * 
 * @author Avatair
 */
public class ModuleOverrideSuper {
	private final OverridePointer superPointer;
	private Exception occurredException;
	
	ModuleOverrideSuper(OverridePointer superPointer) {
		// NOTE: Is only created within ModuleOverrideHandler
		this.superPointer = superPointer;
	}
	
	/**
	 * Returns whether there exists an overwritten method.
	 * 
	 * @return <code>true</code> iff yes.
	 */
	public boolean hasSuper() {
		return superPointer != null;
	}
	
	/**
	 * Invokes the overwritten method. Check {@link #hasSuper()} first before calling it.
	 * 
	 * @param throwRuntimeExceptionsImmediately iff <code>true</code> then an occurred runtime exception is thrown
	 *        If it is not intended to catch exceptions after the invoke, then this argument must be <code>true</code>.
	 * @param args arguments passed to the invoke call. 
	 * @return return value of the invoked method. Is <code>null</code> if an exception has been occurred.
	 * @throws RuntimeException any kind of runtime exceptions thrown within the invoke call, if 
	 *         <code>throwRuntimeExceptionsImmediately</code> is <code>true</code>
	 */
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
	
	/**
	 * Executes a passed catch handler if an exception of given time has occurred.
	 * The caught exception is cleared afterwards.
	 * 
	 * @param exceptionType the exception type to be caught.
	 * @param handler a passed exception handler, e.g. as a lambda expression.
	 */
	@SuppressWarnings("unchecked")
	public <ET extends Exception> void catchException(Class<ET> exceptionType, Consumer<ET> handler) {
		if( occurredException == null )
			return;
		
		try {
			if( exceptionType.isInstance(occurredException) )
				handler.accept((ET)occurredException);
		}
		finally {
			occurredException = null;
		}
	}
	
	/**
	 * Rethrows any uncaught exception. The caught exception is cleared afterwards.
	 * 
	 * @throws RuntimeException if a runtime exception has been thrown.
	 * @throws ModuleOverrideException if a uncaught checked exception has been thrown.
	 */
	public void rethrowUncatched() throws RuntimeException, ModuleOverrideException {
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
	
	/**
	 * Invokes the overwritten method. Special throwables, like {@link Error} are rethrown immediately. 
	 * 
	 * @param args arguments passed to the invoke call. 
	 * @return return value of the invoked method.
	 * @throws Exception a caught exception.
	 */
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
