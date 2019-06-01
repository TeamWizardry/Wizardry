package com.teamwizardry.wizardry.api.spell.attribute;

public class AttributeRange
{
	public static final AttributeRange BACKUP = new AttributeRange(0, 0);

	public float min;
	public float max;

	public AttributeRange(float min, float max)
	{
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String toString()
	{
		return "[ " + min + " <----> " + max + " ]";
	}
}
