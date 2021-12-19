package com.teamwizardry.wizardry.capability.spell

import com.teamwizardry.librarianlib.scribe.Save
import com.teamwizardry.wizardry.common.init.ModCapabilities
import com.teamwizardry.wizardry.common.spell.component.ShapeChain
import dev.onyxstudios.cca.api.v3.item.ItemComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

class SpellCapability(val stack: ItemStack, @Save override var spell: ShapeChain? = null): ISpellCapability, ItemComponent(stack, ModCapabilities.SPELL) {
    companion object {
        var ItemStack.spell: ShapeChain?
            get() = ModCapabilities.SPELL.maybeGet(this).orElse(null)?.spell
            set(value) {
                ModCapabilities.SPELL.maybeGet(this).orElse(null)?.let {it.spell = value}
            }
    }
}