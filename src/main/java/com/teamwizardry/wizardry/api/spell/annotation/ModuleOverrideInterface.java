package com.teamwizardry.wizardry.api.spell.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares a method being an override interface.
 * The passed argument is the registry name identifying the override
 * which is used to link the interface node to an implementation.
 * 
 * @author Avatair
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ModuleOverrideInterface {
	String value();
}
