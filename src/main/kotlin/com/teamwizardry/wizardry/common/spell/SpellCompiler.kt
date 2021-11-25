package com.teamwizardry.wizardry.common.spell

import com.teamwizardry.wizardry.common.spell.component.*
import com.teamwizardry.wizardry.configs.ServerConfigs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

class SpellCompiler private constructor() {
    private var firstShape: ShapeChain? = null
    private var currentShape: ShapeChain? = null
    private var currentEffect: EffectChain? = null
    private var modifierCount = 0
    private fun compileSpell(vararg items: ItemStack): ShapeChain? {
        return this.compileSpell(items.asList())
    }

    private fun compileSpell(items: List<ItemStack>): ShapeChain? {
        val components: List<ISpellComponent?> = processItems(items)
        return compile(components)
    }

    private fun processItems(items: List<ItemStack>): List<ISpellComponent> {
        val components: MutableList<ISpellComponent> = LinkedList<ISpellComponent>()
        val flattened: LinkedList<Item> = items.stream()
            .flatMap {stack: ItemStack ->
                IntStream.range(0, stack.count)
                        .mapToObj { stack.item }
            }
                .collect(
                Collectors.toCollection {LinkedList()}
                )
        while (!flattened.isEmpty()) {
            val component: ISpellComponent? = ComponentRegistry.getComponentForItems(flattened)
            if (component == null) flattened.remove()
            else {
                for (i in component.items.indices) flattened.remove()
                components.add(component)
            }
        }
        return components
    }

    private fun compile(components: List<ISpellComponent?>): ShapeChain? {
        for (component in components) {
            when (component) {
                is ModuleShape -> handleShape(component)
                is ModuleEffect -> handleEffect(component)
                is Modifier -> handleModifier(component)
                is TargetComponent -> handleTarget(component)
            }
        }
        return firstShape
    }

    private fun handleShape(shape: ModuleShape) {
        val next = ShapeChain(shape)
        if (firstShape == null) {
            currentShape = next
            firstShape = currentShape
        } else {
            currentShape?.setNext(next)
            currentShape = next
        }
        modifierCount = 0
    }

    private fun handleEffect(effect: ModuleEffect) {
        if (firstShape == null) // Spells have to start with shapes!
            return
        currentEffect = EffectChain(effect)
        currentShape?.addEffect(currentEffect!!)
        modifierCount = 0
    }

    private fun handleModifier(modifier: Modifier) {
        if (modifierCount++ > ServerConfigs.maxModifiers) return
        if (firstShape == null) // Spells have to start with shapes!
            return
        if (currentEffect == null) currentShape?.addModifier(modifier) else currentEffect?.addModifier(modifier)
    }

    private fun handleTarget(target: TargetComponent) {
        if (firstShape == null) // Spells have to start with shapes!
            return
        var targetType: TargetType = TargetType.ALL
        if (target === ComponentRegistry.entityTarget) targetType =
            TargetType.ENTITY else if (target === ComponentRegistry.blockTarget) targetType = TargetType.BLOCK
        if (currentEffect == null) currentShape?.setTarget(targetType) else currentEffect?.setTarget(targetType)
    }

    companion object {
        fun get(): SpellCompiler { return SpellCompiler() }
        fun compileSpell(vararg items: ItemStack): ShapeChain? { return get().compileSpell(*items) }
        fun compileSpell(items: List<ItemStack>): ShapeChain? { return get().compileSpell(items) }
    }
}