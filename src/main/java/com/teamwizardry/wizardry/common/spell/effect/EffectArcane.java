package com.teamwizardry.wizardry.common.spell.effect;

import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.common.init.ModSounds;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/*
* By: Carbon
* Pure magic damage effect.
* */
public class EffectArcane extends PatternEffect {
    // FIXME: Needs balancing.
    public static final float POTENCY_MULTIPLIER = 1f;

    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if(entity.getType() != Interactor.InteractorType.ENTITY) return;

        entity.getEntity().attackEntityFrom(DamageSource.MAGIC, (float) instance.getAttributeValue("potency") * POTENCY_MULTIPLIER);
        ModSounds.playSound(world, instance.getCaster(), entity, ModSounds.FIREWORK, 0.1f);
    }

    // No affect on blocks. This remains a NO-OP.
    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        // NO-OP
    }
}
