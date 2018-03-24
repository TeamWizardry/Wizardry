package com.teamwizardry.wizardry.api.spell.attribute;

public class AttributeRange
{
	public static final AttributeRange BACKUP = new AttributeRange(0, 0, 0);
	
	public double base;
	public double min;
	public double max;
	
	public AttributeRange(double base, double min, double max)
	{
		this.base = base;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String toString()
	{
		return "[ " + min + " <-- " + base + " --> " + max + " ]";
	}
}
