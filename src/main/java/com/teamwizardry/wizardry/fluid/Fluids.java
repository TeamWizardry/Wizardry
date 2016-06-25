package com.teamwizardry.wizardry.fluid;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Fluids {
    private static ModelResourceLocation manafluidLocation = new ModelResourceLocation(Wizardry.MODID + ":" + "mana", "fluid");

    public static void preInit() {
        FluidRegistry.registerFluid(FluidMana.instance);
        GameRegistry.registerBlock(FluidBlockMana.instance, "mana");
        Item mana = Item.getItemFromBlock(FluidBlockMana.instance);
        if (mana != null) ModelLoader.setCustomMeshDefinition(mana, stack -> manafluidLocation);

        ModelLoader.setCustomStateMapper(FluidBlockMana.instance, new StateMapperBase() {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return manafluidLocation;
            }
        });
        FluidRegistry.addBucketForFluid(FluidMana.instance);
    }
}
