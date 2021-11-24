package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

class Modifier(val attribute: String, override val items: List<Item>) : ISpellComponent {
    val translationKey: String
        get() = "wizardry.modifier.$attribute"
}