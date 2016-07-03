package com.teamwizardry.wizardry.common.spell.module.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Priority;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleCritChance extends Module implements IModifier {
    public ModuleCritChance() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }
    
    @Override
    public String getDescription()
    {
    	return "Allows a spell effect to critically strike, dealing bonus damage.";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.CRIT_CHANCE, new AttributeModifier(Operation.ADD, 0.1));

        // 100% Crit Chance ~
        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}