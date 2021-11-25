package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.world.World

/**
 * Shape effects have affectEntity and affectBlock to run their linked effects and then
 * run any attached shapes after them.
 */
abstract class PatternShape : Pattern() {
    override fun affectEntity(world: World, entity: Interactor, instance: Instance) {
        if (instance is ShapeInstance) {
            instance.effects.forEach { effect -> effect.run(world, entity) }
            instance.nextShape?.run(world, entity)
        }
    }

    override fun affectBlock(world: World, block: Interactor, instance: Instance) {
        if (instance is ShapeInstance) {
            instance.effects.forEach { effect -> effect.run(world, block) }
            instance.nextShape?.run(world, block)
        }
    }
}