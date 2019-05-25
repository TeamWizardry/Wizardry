package com.teamwizardry.wizardry.api.spell.attribute;

public class AttributeRange
{
	public static final AttributeRange BACKUP = new AttributeRange(0, 0, 0);

	public float base;
	public float min;
	public float max;

	public AttributeRange(float base, float min, float max)
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
