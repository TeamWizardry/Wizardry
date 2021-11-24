package com.teamwizardry.wizardry.common.item

import net.minecraft.item.*
import net.minecraft.util.Hand

class ItemStaff(settings: Settings?) : Item(settings), INacreDecayProduct {
    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemstack: ItemStack = player.getStackInHand(hand)

        // TODO: Test spell, delete when spell crafting is finished
        if (!world.isClient) {
            val caster = Interactor(player)
            SpellCompiler.Companion.get()
                .compileSpell(
                    ItemStack(Items.BEEF),
                    ItemStack(Items.LEATHER),
                    ItemStack(Items.LAPIS_LAZULI),
                    ItemStack(Items.LAPIS_LAZULI),
                    ItemStack(Items.MAGMA_CREAM)
                )
                .toInstance(caster)
                .run(world, caster)
        }
        return TypedActionResult.success<ItemStack>(itemstack)
    }
}