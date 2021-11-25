package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.Wizardry
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object ModItems {
    val wizardry: ItemGroup = FabricItemGroupBuilder.build(Wizardry.getId("general")) { ItemStack(staff) }
    private val wisdomStick = Item(FabricItemSettings().group(wizardry))
    val staff = Item(FabricItemSettings().group(wizardry).maxCount(1).rarity(Rarity.UNCOMMON))
    private val pearl = Item(FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON))
    val devilDust = Item(FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON))
    private val skyDust = Item(FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON))
    private val fairyDust = Item(FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON))
    private val fairyWings = Item(FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON))
    private val fairyApple = Item(
        FabricItemSettings().group(wizardry).rarity(Rarity.RARE).food(
            FoodComponent.Builder()
                .hunger(5)
                .saturationModifier(2f)
                .alwaysEdible()
                .statusEffect(StatusEffectInstance(StatusEffects.REGENERATION, 20 * 15), 1f)
                .statusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, 20 * 60 * 2, 1), 1f)
                .statusEffect(StatusEffectInstance(StatusEffects.GLOWING, 20 * 30), 1f)
                .build()
        )
    )
    var manaBucket: Item? = null
    var nacreBucket: Item? = null
    fun init() {
        initItem(wisdomStick, "wisdom_stick")
        initItem(staff, "staff")
        initItem(pearl, "pearl")
        initItem(devilDust, "devil_dust")
        initItem(skyDust, "sky_dust")
        initItem(fairyDust, "fairy_dust")
        initItem(fairyWings, "fairy_wings")
        initItem(fairyApple, "fairy_apple")
        initItem(manaBucket, "mana_bucket")
    }

    private fun initItem(item: Item?, path: String) {
        Registry.register<Item, Item?>(Registry.ITEM, Wizardry.getId(path), item)
    }
}