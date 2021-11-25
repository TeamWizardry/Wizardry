package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

abstract class Modifier(override val name: String, override val items: List<Item>) : ISpellComponent {
    val translationKey: String
        get() = "wizardry.modifier.$name"
}