package com.teamwizardry.wizardry.common.spell.module.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleArea extends Module implements IModifier {
    public ModuleArea() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public String getDescription()
    {
    	return "Increases a shape or effect's area of effect.";
    }
    
    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.RADIUS, new AttributeModifier(Operation.ADD, 1));

        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.5));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5));
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}