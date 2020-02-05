package com.teamwizardry.wizardry.api.spell;

import java.util.function.Function;

import net.minecraft.entity.Entity;

public class EntityTarget
{
    private EntityTarget() {}
    
    public static final Function<Entity, Boolean> ALWAYS = entity -> true;
    public static final Function<Entity, Boolean> NEVER = entity -> false;
}
