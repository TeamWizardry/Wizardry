package com.teamwizardry.wizardry.common.spell.effect;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.init.ModSounds;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/*
* By: Carbon
* Pure magic damage effect.
* TODO: Particles
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

    private static final Color[] colors = new Color[]{Color.CYAN, Color.BLUE, Color.MAGENTA};

    //  Graphical Methods

    @Override
    public Color[] getColors() {
        return colors;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {
        for (int i = 0; i < 100; i++)
            Wizardry.PROXY.spawnParticle(
                    new GlitterBox.GlitterBoxFactory()
                            .setOrigin(target.getPos()
                                    .add(RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15)))
                            .setTarget(RandUtil.nextDouble(-0.5, 0.5),
                                    RandUtil.nextDouble(-0.5, 0.5),
                                    RandUtil.nextDouble(-0.5, 0.5))
                            .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
                            .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
                            .setInitialColor(getRandomColor())
                            .setGoalColor(getRandomColor())
                            .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
                            .setGoalSize(0)
                            .setInitialAlpha(RandUtil.nextFloat(0.5f, 1))
                            .createGlitterBox(RandUtil.nextInt(5, 25)));

    }
}
