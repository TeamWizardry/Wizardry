package com.teamwizardry.wizardry.common.spell

import com.teamwizardry.wizardry.common.spell.component.Modifier
import com.teamwizardry.wizardry.configs.ServerConfigs
import net.minecraft.item.Item
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Stream

class SpellCompiler private constructor() {
    private var firstShape: ShapeChain? = null
    private var currentShape: ShapeChain? = null
    private var currentEffect: EffectChain? = null
    private var modifierCount = 0
    private fun compileSpell(vararg items: ItemStack?): ShapeChain? {
        return this.compileSpell(listOf(*items))
    }

    fun compileSpell(items: List<ItemStack>): ShapeChain? {
        val components: List<ISpellComponent?> = processItems(items)
        return compile(components)
    }

    private fun processItems(items: List<ItemStack>): List<ISpellComponent?> {
        val components: MutableList<ISpellComponent?> = LinkedList<ISpellComponent>()
        val flattened: LinkedList<Item> = items.stream()
            .flatMap(Function<ItemStack, Stream<out Item>> { stack: ItemStack ->
                IntStream.range(0, stack.getCount())
                    .mapToObj<Item>(IntFunction<Item> { n: Int -> stack.getItem() })
            })
            .collect(
                Collectors.toCollection<Item, LinkedList<Item>>(
                    Supplier { LinkedList() })
            )
        while (!flattened.isEmpty()) {
            val component: ISpellComponent = ComponentRegistry.getComponentForItems(flattened)
            if (component == null) flattened.remove() else for (i in component.getItems().indices) flattened.remove()
            components.add(component)
        }
        return components
    }

    private fun compile(components: List<ISpellComponent?>): ShapeChain? {
        for (component in components) {
            if (component is ModuleShape) handleShape(component as ModuleShape) else if (component is ModuleEffect) handleEffect(
                component as ModuleEffect
            ) else if (component is Modifier) handleModifier(component as Modifier) else if (component is TargetComponent) handleTarget(
                component as TargetComponent
            )
        }
        return firstShape
    }

    private fun handleShape(shape: ModuleShape) {
        val next = ShapeChain(shape)
        if (firstShape == null) {
            currentShape = next
            firstShape = currentShape
        } else {
            currentShape.setNext(next)
            currentShape = next
        }
        modifierCount = 0
    }

    private fun handleEffect(effect: ModuleEffect) {
        if (firstShape == null) // Spells have to start with shapes!
            return
        currentEffect = EffectChain(effect)
        currentShape.addEffect(currentEffect)
        modifierCount = 0
    }

    private fun handleModifier(modifier: Modifier) {
        if (modifierCount++ > ServerConfigs.maxModifiers) return
        if (firstShape == null) // Spells have to start with shapes!
            return
        if (currentEffect == null) currentShape.addModifier(modifier) else currentEffect.addModifier(modifier)
    }

    private fun handleTarget(target: TargetComponent) {
        if (firstShape == null) // Spells have to start with shapes!
            return
        var targetType: TargetType = TargetType.ALL
        if (target === ComponentRegistry.getEntityTarget()) targetType =
            TargetType.ENTITY else if (target === ComponentRegistry.getBlockTarget()) targetType = TargetType.BLOCK
        if (currentEffect == null) currentShape.setTarget(targetType) else currentEffect.setTarget(targetType)
    }

    companion object {
        fun get(): SpellCompiler {
            return SpellCompiler()
        }
    }
}