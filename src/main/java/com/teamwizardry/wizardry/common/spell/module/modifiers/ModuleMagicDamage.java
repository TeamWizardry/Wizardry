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

public class ModuleMagicDamage extends Module implements IModifier {
    public ModuleMagicDamage() {
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public String getDescription()
    {
    	return "Increases the amount of magic damage a spell does.";
    }
    
    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.DAMAGE, new AttributeModifier(Operation.ADD, 1));

        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.2));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.2));
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}