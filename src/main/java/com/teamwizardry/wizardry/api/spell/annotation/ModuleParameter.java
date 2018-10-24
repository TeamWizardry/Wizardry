package com.teamwizardry.wizardry.api.spell.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated fields are linked to a json module configuration field. <br />
 * 
 * <b>Usage</b>: Parameters are provided via json as the following example: <br /><br />
 * <code>
 * { <br/>
 * &nbsp;&nbsp;"sub_module_id": "my_spell", <br/>
 * &nbsp;&nbsp;"reference_module_id": "spell_receiving_parameters", <br/>
 * &nbsp;&nbsp;<b>"parameters": { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;"group1" : { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"group2" : { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"param1" : "This is a String" <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}, <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"param2" : 100 <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}, <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;"param3" : "ENUM_VALUE_1" <br/>
 * &nbsp;&nbsp;}, </b><br/>
 * &nbsp;&nbsp;"item": "minecraft:diamond", <br/>
 * &nbsp;&nbsp;"mana": { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;"base": 30 <br/>
 * &nbsp;&nbsp;}, <br/>
 * &nbsp;&nbsp;"burnout": { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;"base": 30 <br/>
 * &nbsp;&nbsp;}, <br/>
 * &nbsp;&nbsp;"cooldown": { "base": 30 } <br/>
 * } <br/>
 * </code>
 * <br/>
 * If annotating a field by adressing a path in the JSON configuration, then its value is taken when the object instance is created: <br /><br />
 * <code>
 * {@literal @}RegisterModule(ID="spell_receiving_parameters") <br />
 * public class ModuleParameterReceiving implements IModuleEffect { <br />
 * <br />
 * &nbsp;&nbsp;{@literal @}ModuleParameter("group1.group2.param1") <br />
 * &nbsp;&nbsp;public String myString; <br />
 * <br />
 * &nbsp;&nbsp;{@literal @}ModuleParameter("group1.param2") <br />
 * &nbsp;&nbsp;public int myInteger; <br />
 * <br />
 * &nbsp;&nbsp;{@literal @}ModuleParameter("param3") <br />
 * &nbsp;&nbsp;public TestEnum myEnum; <br />
 * <br />
 * &nbsp;&nbsp;public ModuleParameterReceiving() { <br />
 * &nbsp;&nbsp;&nbsp;&nbsp;// WARNING: Values are NOT initialized! <br />
 * &nbsp;&nbsp;} <br />
 * <br />
 * &nbsp;&nbsp;public void someMethod() { <br />
 * &nbsp;&nbsp;&nbsp;&nbsp;// Values are initialized and can be used outside the constructor. <br />
 * &nbsp;&nbsp;}<br />
 * <br />
 * &nbsp;&nbsp;//// <br />
 * <br />
 * &nbsp;&nbsp;public static enum TestEnum {<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;ENUM_VALUE_1, ENUM_VALUE_2<br />
 * &nbsp;&nbsp;}<br />
 * }<br />
 * </code> 
 * @author Avatair
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ModuleParameter {
	String value();
}
