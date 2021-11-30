package com.teamwizardry.wizardry.common.item

import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World


class ItemPearl(settings: Settings?) : Item(settings), INacreProduct {

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        colorableOnUpdate(stack, world)
    }


}