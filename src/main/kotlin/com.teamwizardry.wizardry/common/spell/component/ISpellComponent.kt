package com.teamwizardry.wizardry.common.spell.component

import net.minecraft.item.Item

interface ISpellComponent {
    val name: String
    val items: List<Item>
}