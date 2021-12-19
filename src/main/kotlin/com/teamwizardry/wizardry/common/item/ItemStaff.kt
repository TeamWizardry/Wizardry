package com.teamwizardry.wizardry.common.item

import com.teamwizardry.wizardry.capability.spell.SpellCapability.Companion.spell
import com.teamwizardry.wizardry.common.spell.SpellCompiler
import com.teamwizardry.wizardry.common.spell.component.Interactor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ItemStaff(settings: Settings?) : Item(settings), INacreProduct.INacreDecayProduct {
    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack: ItemStack = player.getStackInHand(hand)

        // TODO: Test spell, delete when spell crafting is finished
        if (!world.isClient) {
            val caster = Interactor(player)
            SpellCompiler.compileSpell(
                    ItemStack(Items.BEEF),
                    ItemStack(Items.LEATHER),
                    ItemStack(Items.LAPIS_LAZULI),
                    ItemStack(Items.LAPIS_LAZULI),
                    ItemStack(Items.PORKCHOP)
                )
                ?.toInstance(caster)
                ?.run(world, caster)

            itemStack.spell?.toInstance(caster)?.run(world)
        }
        return TypedActionResult.success(itemStack)
    }
}