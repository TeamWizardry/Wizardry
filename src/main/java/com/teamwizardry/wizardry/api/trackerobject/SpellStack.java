package com.teamwizardry.wizardry.api.trackerobject;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad44 This class is created when a spell is created, then
 * tracks it and controls it everywhere
 */
public class SpellStack
{
	// WIP

	public NBTTagCompound spell;
	public Module shape;

	/**
	 * The player that originally casted the spell
	 */
	public EntityPlayer player;
//	private double manaMult;
//	private double burnoutMult;
	
	public Color color;
	
	/**
	 * The entity that is casting the spell. IE: The zone entity
	 */
	public Entity caster;

	public SpellStack(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		this.player = player;
		this.caster = caster;
		this.spell = spell;
		this.shape = getModuleTree(spell);
		this.color = getSpellColor();
	}

	public void castSpell()
	{
		if(shape != null)
			shape.cast(player, player, spell, this);
	}

	public void castEffects(Entity caster)
	{
		SpellCastEvent event = new SpellCastEvent(spell, caster, player);
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isCanceled())
		{
			NBTTagList children = spell.getTagList(Module.MODULES, NBT.TAG_COMPOUND);
			for (int i = 0; i < children.tagCount(); i++)
			{
				NBTTagCompound module = children.getCompoundTagAt(i);
				if (module.getString(Module.TYPE).equals(ModuleType.SHAPE.toString()))
				{
					SpellStack stack = new SpellStack(player, caster, module);
					stack.castSpell();
				}
				else
				{
					Module spell = ModuleRegistry.getInstance().getModuleById(module.getInteger(Module.SHAPE));
					spell.cast(player, caster, module, this);
				}
			}
		}
	}

	private Module getModuleTree(NBTTagCompound fullSpell)
	{
		Module module = ModuleRegistry.getInstance().getModuleById(fullSpell.getInteger(Module.SHAPE));
		if (module == null) return null;
		NBTTagList children = fullSpell.getTagList(Module.MODULES, NBT.TAG_COMPOUND);
		for (int i = 0; i < children.tagCount(); i++)
			module.accept(getModuleTree(children.getCompoundTagAt(i)));
		return module;
	}
	
	private List<Module> getEffectModules(Module spellTree)
	{
		List<Module> effects = new ArrayList<Module>();
		return getEffectModules(spellTree, effects);
	}
	
	private List<Module> getEffectModules(Module root, List<Module> effects)
	{
		if (root != null && root.hasChildren())
		{
			for (Module module : root.getChildren())
			{
				if (module.getType() == ModuleType.EFFECT)
					effects.add(module);
				getEffectModules(module, effects);
			}
		}
		return effects;
	}

	/**
	 * @return The color of the spell with respect to the current active effect
	 *         or effects
	 */
	public Color getSpellColor()
	{
		float r = -1, b = -1, g = -1;

		List<Module> effects = getEffectModules(shape);

		for (Module effect : effects)
		{
			if (r == -1 && g == -1 && b == -1)
			{
				r = effect.getColor().r;
				g = effect.getColor().g;
				b = effect.getColor().b;
			}
			else
			{
				r = (r + effect.getColor().r) / 2;
				g = (g + effect.getColor().g) / 2;
				b = (b + effect.getColor().b) / 2;
			}
		}

		if (r == -1 && g == -1 && b == -1)
			return Color.WHITE;
		else return new Color(r, g, b);
	}
}
