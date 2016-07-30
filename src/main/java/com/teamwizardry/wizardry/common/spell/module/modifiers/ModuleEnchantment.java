package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.IRuntimeModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleEnchantment extends Module implements IModifier, IRuntimeModifier {
    public ModuleEnchantment(ItemStack stack) {
        super(stack);
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription()
    {
    	return "Attempts to apply the given enchantment to the spell effect.";
    }

    @Override
    public String getDisplayName() {
        return "Apply Enchantment";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 2));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 2));
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}