package me.lordsaad.wizardry.api.modules;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import me.lordsaad.wizardry.spells.modules.modifiers.ModuleModifier;

/**
 * Created by Saad on 6/21/2016.
 * <pre></pre>
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
	
	protected int manaCost = 0;
	protected float manaMult = 1;
	protected int burnoutCost = 0;
	protected float burnoutMult = 1;
	
	protected boolean canHaveChildren = true;
	
	public Module()
	{
		attributes.addAttribute(Attribute.COST);
		attributes.addAttribute(Attribute.BURNOUT);
	}
	
	/**
	 * Determine what type of module this is: An EFFECT, EVENT, MODIFIER, SHAPE, or BOOLEAN
	 * @return The module's {@link ModuleType}
	 */
	public abstract ModuleType getType();

	/**
	 * Generates an {@link NBTTagCompound} containing information about the module and its effects, as well as any connected modules.
	 * @return An {@code NBTTagCompound} containing information on the module and all connected modules
	 */
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString(CLASS, this.getClass().getName());
		return compound;
	}

	/**
	 * Returns the default {@link RecourseLocation} for this module's icon.
	 * @return A {@code ResourceLocation} with the location {@code Wizardry:this.class.simpleName}
	 */
	public ResourceLocation getIcon()
	{
		return new ResourceLocation(Wizardry.MODID, this.getClass().getSimpleName());
	}
	
	public Module setManaCost(int cost)
	{
		manaCost = cost;
		return this;
	}
	
	public Module setManaMultiplier(float multiplier)
	{
		manaMult = multiplier;
		return this;
	}
	
	public Module setBurnout(int amount)
	{
		burnoutCost = amount;
		return this;
	}
	
	public Module setBurnoutMultiplier(float multiplier)
	{
		burnoutMult = multiplier;
		return this;
	}
	
	{ /* attributes/parsing */ }
	
	AttributeMap attributes = new AttributeMap();
	
	/**
	 * Handle a child module {@code other}
	 * @param other the child module
	 * @return if the module was handled
	 */
	public boolean accept(Module other) {
		if(other instanceof ModuleModifier) {
			((ModuleModifier)other).apply(attributes);
			return true;
		}
		return false;
	}
	
	public boolean canHaveChildren() { return canHaveChildren; }
}