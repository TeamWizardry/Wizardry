package com.teamwizardry.wizardry.common.spell.effect

import com.teamwizardry.wizardry.PROXY
import com.teamwizardry.wizardry.client.particle.GlitterBox
import com.teamwizardry.wizardry.common.init.ModSounds
import com.teamwizardry.wizardry.common.init.ModSounds.playSound
import com.teamwizardry.wizardry.common.spell.component.Attributes
import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.Interactor
import com.teamwizardry.wizardry.common.spell.component.PatternEffect
import com.teamwizardry.wizardry.common.utils.RandUtil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.damage.DamageSource
import net.minecraft.world.World
import java.awt.Color

/*
* By: Carbon
* Pure magic damage effect.
* */
class EffectArcane : PatternEffect() {
    override fun affectEntity(world: World, entity: Interactor, instance: Instance) {
        if (entity.type != Interactor.InteractorType.ENTITY) return
        entity.entity?.damage(DamageSource.MAGIC, instance.getAttributeValue(Attributes.INTENSITY).toFloat() * POTENCY_MULTIPLIER)
        playSound(world, instance.caster, entity, ModSounds.FIREWORK, 0.1f)
    }

    // No effect on blocks. This remains a NO-OP.
    override fun affectBlock(world: World, block: Interactor, instance: Instance) {
        // NO-OP
    }

    //  Graphical Methods
    override val colors: Array<Color>
        get() = Companion.colors

    @Environment(EnvType.CLIENT)
    override fun runClient(world: World, instance: Instance, target: Interactor) {
        for (i in 0..99) PROXY.spawnParticle(
                GlitterBox.GlitterBoxFactory()
                        .setOrigin(target.pos.add(RandUtil.nextDouble(-0.15, 0.15), RandUtil.nextDouble(-0.15, 0.15), RandUtil.nextDouble(-0.15, 0.15)))
                        .setTarget(RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(-0.5, 0.5))
                        .setDrag(RandUtil.nextFloat(0.2f, 0.3f))
                        .setGravity(RandUtil.nextFloat(-0.005f, -0.015f))
                        .setInitialColor(randomColor)
                        .setGoalColor(randomColor)
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