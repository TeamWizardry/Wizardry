package com.teamwizardry.wizardry.common.prism

import java.util.function.Function
import java.util.function.Predicate

open class ModuleSerializer private constructor() : NbtSerializer<Module?>() {
    protected override fun deserialize(nbt: NbtElement): Module {
        return ComponentRegistry.getModules().get(nbt.asString())
    }

    protected override fun serialize(module: Module): NbtElement {
        return NbtString.of(
            ComponentRegistry.getModules().entries.stream()
                .filter(Predicate { (_, value): Map.Entry<String?, com.teamwizardry.wizardry.common.spell.component.Module> -> value == module })
                .map<String>(
                    Function { (key): Map.Entry<String?, com.teamwizardry.wizardry.common.spell.component.Module?> -> key })
                .findFirst().get()
        )
    }

    companion object {
        private val INSTANCE = ModuleSerializer()
        fun get(): ModuleSerializer {
            return INSTANCE
        }
    }
}