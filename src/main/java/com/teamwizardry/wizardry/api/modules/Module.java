package com.teamwizardry.wizardry.api.modules;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.IRuntimeModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/21/2016.
 * 
 * <pre></pre>
 * 
 * Edited by Escapee from 6/22/2016
 */
public abstract class Module
{
	public static final String CLASS = "Class";
	public static final String MODULES = "Modules";
	public static final String POWER = "Power";
	public static final String DURATION = "Duration";
	public static final String SILENT = "Silent";
	public static final String MANA = "Mana";
	public static final String BURNOUT = "Burnout";
	public static final String RADIUS = "Radius";
	public AttributeMap attributes = new AttributeMap();

	public List<Module> children = new ArrayList<Module>();

	protected boolean canHaveChildren = true;

	private ResourceLocation iconLocation = new ResourceLocation(Wizardry.MODID, this.getClass().getSimpleName());
	private String description = "<-NULL->";

	{ /* attributes/parsing */}

	public Module()
	{
		attributes.addAttribute(Attribute.COST);
		attributes.addAttribute(Attribute.BURNOUT);
	}

	/**
	 * Determine what type of module this is: An EFFECT, EVENT, MODIFIER, SHAPE,
	 * or BOOLEAN
	 *
	 * @return The module's {@link ModuleType}
	 */
	public abstract ModuleType getType();

	/**
	 * Generates an {@link NBTTagCompound} containing information about the
	 * module and its effects, as well as any connected module.
	 *
	 * @return An {@code NBTTagCompound} containing information on the module
	 *         and all connected module
	 */
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString(CLASS, this.getClass().getName());
		NBTTagList list = new NBTTagList();
		for (Module module : children)
		{
			list.appendTag(module.getModuleData());
		}
		return compound;
	}

	/**
	 * Gets the current {@link ResourceLocation}. Set to
	 * {@code Wizardry:this.class.getSimpleName()} by default.
	 *
	 * @return The current {@code ResourceLocation}
	 */
	public ResourceLocation getIcon()
	{
		return iconLocation;
	}

	/**
	 * Sets the {@link ResourceLocation} for this module
	 *
	 * @param location
	 *            The new {@code ResourceLocation}
	 */
	public void setIcon(ResourceLocation location)
	{
		iconLocation = location;
	}

	/**
	 * Get the description of this module which describes what it does.
	 * <p>
	 * Override it to return a custom description
	 *
	 * @return The current {@link String} set for this module which describes
	 *         what it does
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Handle a child module {@code other}
	 *
	 * @param other
	 *            the child module
	 * @return if the module was handled
	 */
	public boolean accept(Module other)
	{
		if (other instanceof IModifier)
		{
			IModifier modifier = ((IModifier) other);
			attributes.beginCaputure();
			modifier.apply(attributes);

			if (modifier.doesFallback() && attributes.didHaveInvalid())
				attributes.endCapture(false); // discard changes and don't
												// return true so it passes on
												// to subclass
			else
			{
				attributes.endCapture(true); // save changes
				return true;// we don't want to handle the module normally, so
							// return that we handled it
			}
		}
		if (other instanceof IRuntimeModifier)
			children.add(other);
		if (this.getType() == ModuleType.EVENT)
			if (other.getType() == ModuleType.SHAPE)
				children.add(other);
		if (this.getType() == ModuleType.BOOLEAN)
			if (other.getType() == ModuleType.BOOLEAN || other.getType() == ModuleType.EVENT)
				children.add(other);
		if (this.getType() == ModuleType.SHAPE)
			if (other.getType() == ModuleType.BOOLEAN || other.getType() == ModuleType.EFFECT || other.getType() == ModuleType.EVENT)
				children.add(other);
		return false;
	}

	public boolean canHaveChildren()
	{
		return canHaveChildren;
	}
}