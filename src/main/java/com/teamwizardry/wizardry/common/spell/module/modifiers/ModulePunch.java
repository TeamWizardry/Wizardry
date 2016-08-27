package com.teamwizardry.wizardry.common.spell.module.modifiers;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.module.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModulePunch extends Module implements IModifier {
    public ModulePunch(ItemStack stack) {
        super(stack);
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public String getDescription() {
        return "Gives a spell a knockback effect.";
    }

    @Override
    public String getDisplayName() {
        return "Increase Punch";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.KNOCKBACK, new AttributeModifier(Operation.ADD, 0.5));

        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.1));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.1));
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        // TODO Auto-generated method stub
        return false;
    }
}
