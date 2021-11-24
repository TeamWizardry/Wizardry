package com.teamwizardry.wizardry.common.item

import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ItemNacrePearl(settings: Settings?) : Item(settings), INacreProduct {
    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        colorableOnUpdate(stack, worldIn)
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected)
    }

    // TODO - find item entity version of this
    //	@Override
    //	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
    //		colorableOnEntityItemUpdate(entity);
    //
    //		return super.onEntityItemUpdate(stack, entity);
    //	}
    private fun getNameType(stack: ItemStack): String {
        val quality = getQuality(stack)
        if (quality > 1) return "ancient" else if (quality == 1f) return "apex" else if (quality > 0.8) return "potent" else if (quality > 0.6) return "decent" else if (quality > 0.4) return "flawed" else if (quality > 0.2) return "drained"
        return "wasted"
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return if (!stack.hasNbt()) super.getTranslationKey(stack) else super.getTranslationKey(stack) + "." + getNameType(
            stack
        )
    }
}