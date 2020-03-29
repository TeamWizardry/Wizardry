package com.teamwizardry.wizardry.api.spell;

import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeModifier
{
    private Operation operation;
    private double amount;
    
    public AttributeModifier(Operation operation, double amount)
    {
        this.operation = operation;
        this.amount = amount;
    }
    
    public Operation getOperation() { return operation; }
    
    public double getAmount() { return amount; }
}
