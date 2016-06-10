package me.lordsaad.wizardry;

import me.lordsaad.wizardry.blocks.ManaFluid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Saad on 6/9/2016.
 */
public class ModFluids {

    public static void init() {
        Fluid mana = new ManaFluid();
        FluidRegistry.registerFluid(mana);
        // GameRegistry.registerBlock()
        FluidRegistry.addBucketForFluid(mana);
    }
}
