package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.block.fluid.mana.ManaFluid
import com.teamwizardry.wizardry.common.block.fluid.nacre.NacreFluid
import com.teamwizardry.wizardry.getId
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

object ModFluids {
    var STILL_MANA: FlowableFluid? = null
    var FLOWING_MANA: FlowableFluid? = null
    var STILL_NACRE: FlowableFluid? = null
    var FLOWING_NACRE: FlowableFluid? = null

    fun init() {
        STILL_MANA = Registry.register<Fluid, ManaFluid.Still>(Registry.FLUID, getId("mana"), ManaFluid.Still())
        FLOWING_MANA = Registry.register<Fluid, ManaFluid.Flowing>(Registry.FLUID, getId("flowing_mana"), ManaFluid.Flowing())
        ModItems.manaBucket = BucketItem(STILL_MANA, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1))

        STILL_NACRE = Registry.register<Fluid, NacreFluid.Still>(Registry.FLUID, getId("nacre"), NacreFluid.Still())
        FLOWING_NACRE = Registry.register<Fluid, NacreFluid.Flowing>(Registry.FLUID, getId("flowing_nacre"), NacreFluid.Flowing())
        ModItems.nacreBucket = BucketItem(STILL_NACRE, Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1))
    }
}