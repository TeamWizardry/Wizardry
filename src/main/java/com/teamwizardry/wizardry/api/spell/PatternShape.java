package com.teamwizardry.wizardry.api.spell;

import net.minecraft.world.World;

public abstract class PatternShape extends Pattern
{
    @Override
    public void affectEntity(World world, Interactor entity, Instance instance)
    {
        if (instance instanceof ShapeInstance)
        {
            ShapeInstance shape = (ShapeInstance) instance;
            shape.effects.forEach(effect -> effect.run(world, entity));
            shape.nextShape.run(world, entity);
        }
    }
    
    @Override
    public void affectBlock(World world, Interactor block, Instance instance)
    {
        if (instance instanceof ShapeInstance)
        {
            ShapeInstance shape = (ShapeInstance) instance;
            shape.effects.forEach(effect -> effect.run(world, block));
            shape.nextShape.run(world, block);
        }
    }
}
