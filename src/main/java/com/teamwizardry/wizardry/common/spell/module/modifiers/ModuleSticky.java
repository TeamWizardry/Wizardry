package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.IRuntimeModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSticky extends Module implements IModifier, IRuntimeModifier {
    private int baseCost = 5;
    private int baseBurnout = 5;

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public String getDescription()
    {
    	return "Causes the spell effect to last beyond the instant it activates.";
    }

    @Override
    public String getDisplayName() {
        return "Stick";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.MANA, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.2));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.2));
        map.putModifier(Attribute.MANA, new AttributeModifier(AttributeModifier.Operation.ADD, attributes.apply(Attribute.MANA, baseCost), AttributeModifier.Priority.HIGH));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.ADD, attributes.apply(Attribute.BURNOUT, baseBurnout), AttributeModifier.Priority.HIGH));
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}