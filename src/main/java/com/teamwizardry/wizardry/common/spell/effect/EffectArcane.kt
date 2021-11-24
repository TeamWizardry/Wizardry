package com.teamwizardry.wizardry.common.spell.effect

import com.teamwizardry.wizardry.common.spell.component.Attributes
import com.teamwizardry.wizardry.common.spell.component.Instance
import net.fabricmc.api.Environment
import java.awt.Color

/*
* By: Carbon
* Pure magic damage effect.
* */
class EffectArcane : PatternEffect() {
    fun affectEntity(world: World?, entity: Interactor, instance: Instance) {
        if (entity.getType() != InteractorType.ENTITY) return
        entity.getEntity()
            .damage(DamageSource.MAGIC, instance.getAttributeValue(Attributes.INTENSITY).toFloat() * POTENCY_MULTIPLIER)
        playSound(world, instance.caster, entity, ModSounds.FIREWORK, 0.1f)
    }

    // No affect on blocks. This remains a NO-OP.
    fun affectBlock(world: World?, block: Interactor?, instance: Instance?) {
        // NO-OP
    }

    //  Graphical Methods
    val colors: Array<Color>
        get() = Companion.colors

    @Environment(EnvType.CLIENT)
    fun runClient(world: World?, instance: Instance?, target: Interactor) {
        for (i in 0..99) Wizardry.Companion.PROXY.spawnParticle(
            GlitterBoxFactory()
                .setOrigin(
                    target.getPos()
                        .add(
                            RandUtil.nextDouble(-0.15, 0.15),
                            RandUtil.nextDouble(-0.15, 0.15),
                            RandUtil.nextDouble(-0.15, 0.15)
                        )
                )
                .setTarget(
                    RandUtil.nextDouble(-0.5, 0.5),
                    RandUtil.nextDouble(-0.5, 0.5),
                    RandUtil.nextDouble(-0.5, 0.5)
                )
                .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
                .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
                .setInitialColor(getRandomColor())
                .setGoalColor(getRandomColor())
                .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
                .setGoalSize(0f)
                .setInitialAlpha(RandUtil.nextFloat(0.5f, 1f))
                .createGlitterBox(RandUtil.nextInt(5, 25))
        )
    }

    companion object {
        // FIXME: Needs balancing.
        const val POTENCY_MULTIPLIER = 1f
        private val colors = arrayOf(Color.CYAN, Color.BLUE, Color.MAGENTA)
    }
}