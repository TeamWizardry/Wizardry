package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleDuration extends Module implements IModifier {
    public ModuleDuration() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }
    
    @Override
    public String getDescription()
    {
    	return "Increases the duration of a shape, effect, or modifier.";
    }

    @Override
    public String getDisplayName() {
        return "Increase Duration";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.DURATION, new AttributeModifier(AttributeModifier.Operation.ADD, 1));

        map.putModifier(Attribute.MANA, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.1));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(AttributeModifier.Operation.MULTIPLY, 1.1));
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}