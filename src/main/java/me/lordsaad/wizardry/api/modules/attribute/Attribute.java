package me.lordsaad.wizardry.api.modules.attribute;

public class Attribute {

	public static final Attribute
	
	POWER = new Attribute("power"),
	
	COST = new Attribute("cost"),
	BURNOUT = new Attribute("burnout")
	;
	
	public final String name;
	
	public Attribute(String name) {
		this.name = name;
	}
	
}
