package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.fluid.mana.ManaFluid;
import com.teamwizardry.wizardry.common.block.fluid.nacre.NacreFluid;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ModFluids {

    public static FlowableFluid STILL_MANA;
    public static FlowableFluid FLOWING_MANA;
    public static FlowableFluid STILL_NACRE;
    public static FlowableFluid FLOWING_NACRE;

    public static void init()
    {
        STILL_MANA = Registry.register(Registry.FLUID, Wizardry.getId("mana"), new ManaFluid.Still());
        FLOWING_MANA = Registry.register(Registry.FLUID, Wizardry.getId("flowing_mana"), new ManaFluid.Flowing());
        ModItems.manaBucket = new BucketItem(STILL_MANA, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
        
        STILL_NACRE = Registry.register(Registry.FLUID, Wizardry.getId("nacre"), new NacreFluid.Still());
        FLOWING_NACRE = Registry.register(Registry.FLUID, Wizardry.getId("flowing_nacre"), new NacreFluid.Flowing());
        ModItems.nacreBucket = new BucketItem(STILL_NACRE, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
    }
}
