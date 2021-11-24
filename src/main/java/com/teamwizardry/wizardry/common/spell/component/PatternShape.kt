package com.teamwizardry.wizardry.common.spell.component

/**
 * Shape effects have affectEntity and affectBlock to run their linked effects and then
 * run any attached shapes after them.
 */
abstract class PatternShape : Pattern() {
    override fun affectEntity(world: World?, entity: Interactor?, instance: Instance?) {
        if (instance is ShapeInstance) {
            val shape: ShapeInstance = instance
            shape.effects.forEach { effect -> effect.run(world, entity) }
            if (shape.nextShape != null) shape.nextShape.run(world, entity)
        }
    }

    override fun affectBlock(world: World?, block: Interactor?, instance: Instance?) {
        if (instance is ShapeInstance) {
            val shape: ShapeInstance = instance
            shape.effects.forEach { effect -> effect.run(world, block) }
            if (shape.nextShape != null) shape.nextShape.run(world, block)
        }
    }
}