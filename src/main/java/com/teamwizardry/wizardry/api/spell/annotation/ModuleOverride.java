package com.teamwizardry.wizardry.api.spell.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares a method being an override implementation. Can be used at spell modules or default implementations.
 * The passed argument is the registry name identifying the override.
 * 
 * @author Avatair
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ModuleOverride {
	String value();
}
