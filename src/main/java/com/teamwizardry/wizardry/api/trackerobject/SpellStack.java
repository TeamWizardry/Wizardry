package com.teamwizardry.wizardry.api.trackerobject;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IHasAffinity;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellEvent.SpellRunEvent;

/**
 * Created by LordSaad44 This class is created when a spell is created, then
 * tracks it and controls it everywhere
 */
public class SpellStack {
    // WIP

    public NBTTagCompound spell;
    public Module shape;
    public List<Module> effects;

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
		this.effects = getEffectModules(shape);
		this.color = getSpellColor();
	}

	public void castSpell()
	{
		if(shape != null)
			shape.cast(player, player, spell, this);
	}

	public void castEffects(Entity caster)
	{
		SpellRunEvent event = new SpellRunEvent(spell, caster, player);
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
					Module spell = ModuleRegistry.getInstance().getModuleByLocation(module.getString(Module.SHAPE));
					spell.cast(player, caster, module, this);
				}
			}
		}
	}

	private Module getModuleTree(NBTTagCompound fullSpell)
	{
		Module module = ModuleRegistry.getInstance().getModuleByLocation(fullSpell.getString(Module.SHAPE));
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
			for (int i = 0; i < root.getChildren().size(); i++)
			{
				Module module = root.getChildren().get(i);
				if (module == null) continue;
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
		int r = -1, b = -1, g = -1;
		
		if (effects == null) effects = getEffectModules(shape);

		for (Module effect : effects)
		{
			if (r == -1) r = effect.getColor().getRed();
			else r = (r + effect.getColor().getRed()) / 2;
			
			if (g == -1) g = effect.getColor().getGreen();
			else g = (g + effect.getColor().getGreen()) / 2;
			
			if (b == -1) b = effect.getColor().getBlue();
			else b = (b + effect.getColor().getBlue()) / 2;
		}

		if (r == -1) r = Color.WHITE.getRed();
		if (g == -1) g = Color.WHITE.getGreen();
		if (b == -1) b = Color.WHITE.getBlue();
		return new Color(r, g, b);
	}
	
	public IBloodType getAffinity()
	{
		Map<IBloodType, Integer> bloodLevels = new HashMap<IBloodType, Integer>();
		for (Module effect : effects)
		{
			if (effect instanceof IHasAffinity)
			{
				Map<IBloodType, Integer> effectLevels = ((IHasAffinity) effect).getAffinityLevels();
				for (IBloodType type : effectLevels.keySet())
				{
					bloodLevels.put(type, bloodLevels.get(type) + effectLevels.get(type));
				}
			}
		}
		
		IBloodType bloodType = null;
		int affinityLevel = 0;
		for (IBloodType type : bloodLevels.keySet())
		{
			if (bloodLevels.get(type) > affinityLevel)
			{
				bloodType = type;
				affinityLevel = bloodLevels.get(type);
			}
			else if (bloodLevels.get(type) == affinityLevel)
			{
				bloodType = null;
			}
		}
		
		return bloodType;
	}
}
