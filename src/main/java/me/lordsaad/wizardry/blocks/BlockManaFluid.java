package me.lordsaad.wizardry.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Saad on 6/9/2016.
 */
public class BlockManaFluid extends BlockWizardryFluid {

    public BlockManaFluid(Fluid fluid, Material material) {
        super(fluid, material);
        setUnlocalizedName("mana");
    }
}
