package com.teamwizardry.wizardry.common.prism

import com.teamwizardry.librarianlib.scribe.nbt.NbtSerializer
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry
import com.teamwizardry.wizardry.common.spell.component.Module
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

open class ModuleSerializer private constructor() : NbtSerializer<Module>() {
    override fun deserialize(tag: NbtElement): Module {
        return ComponentRegistry.modules[tag.asString()] ?: throw RuntimeException("Failed to deserialize module!")
    }

    override fun serialize(module: Module): NbtElement {
        return NbtString.of(ComponentRegistry.modules.entries.stream()
                                    .filter {(_, value): Map.Entry<String?, Module> -> value == module}
                                    .map<String> {(key): Map.Entry<String?, Module?> -> key}
                                    .findFirst().get())
    }

    companion object {
        private val INSTANCE = ModuleSerializer()
        fun get(): ModuleSerializer {
            return INSTANCE
        }
    }
}