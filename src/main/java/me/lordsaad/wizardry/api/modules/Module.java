package me.lordsaad.wizardry.api.modules;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

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
	
	protected Module[] modules;
	
	public Module(Module... modules)
	{
		this.modules = modules;
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
		NBTTagList list = new NBTTagList();
		for (Module module : modules)
			list.appendTag(module.getModuleData());
		compound.setTag(MODULES, list);
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
}