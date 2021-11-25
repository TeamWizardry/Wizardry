package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.common.block.fluid.mana.ManaFluid
import com.teamwizardry.wizardry.common.block.fluid.nacre.NacreFluid
import com.teamwizardry.wizardry.getId
import net.minecraft.fluid.FlowableFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry

object ModFluids {
    var STILL_MANA: FlowableFluid? = null
    var FLOWING_MANA: FlowableFluid? = null
    var STILL_NACRE: FlowableFluid? = null
    var FLOWING_NACRE: FlowableFluid? = null

    fun init() {
        STILL_MANA = Registry.register(Registry.FLUID, getId("mana"), ManaFluid.Still())
        FLOWING_MANA = Registry.register(Registry.FLUID, getId("flowing_mana"), ManaFluid.Flowing())
        ModItems.manaBucket = BucketItem(STILL_MANA, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1))

        STILL_NACRE = Registry.register(Registry.FLUID, getId("nacre"), NacreFluid.Still())
        FLOWING_NACRE = Registry.register(Registry.FLUID, getId("flowing_nacre"), NacreFluid.Flowing())
        ModItems.nacreBucket = BucketItem(STILL_NACRE, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1))
    }
}