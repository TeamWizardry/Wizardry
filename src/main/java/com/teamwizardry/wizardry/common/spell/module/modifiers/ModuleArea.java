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

public class ModuleArea extends Module implements IModifier {
    public ModuleArea(ItemStack stack) {
        super(stack);
        canHaveChildren = false;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }

    @Override
    public String getDescription() {
        return "Increases a shape or effect's area of effect.";
    }

    @Override
    public String getDisplayName() {
        return "Increase Area";
    }

    @Override
    public void apply(AttributeMap map) {
        map.putModifier(Attribute.RADIUS, new AttributeModifier(Operation.ADD, 1));

        map.putModifier(Attribute.MANA, new AttributeModifier(Operation.MULTIPLY, 1.5));
        map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5));
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
        // TODO Auto-generated method stub
        return false;
    }
}
