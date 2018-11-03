package com.teamwizardry.wizardry.api.spell.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotated types are declared to contain default override methods. <br/>
 * <b>NOTE</b>: These types are instanced during registration and require a parameterless constructor to exist. 
 * 
 * @author Avatair
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RegisterOverrideDefaults {
}
