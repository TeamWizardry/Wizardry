package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

class TargetComponent(override val name: String, private val item: Item) : ISpellComponent {
    override val items: List<Item>
        get() = listOf(item)
}