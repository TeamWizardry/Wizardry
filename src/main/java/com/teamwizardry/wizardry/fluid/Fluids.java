package com.teamwizardry.wizardry.fluid;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Fluids {
    private static ModelResourceLocation manafluidLocation = new ModelResourceLocation(Wizardry.MODID + ":" + "mana", "fluid");
    private static ModelResourceLocation nacrefluidLocation = new ModelResourceLocation(Wizardry.MODID + ":" + "nacre", "fluid");

    public static void preInit() {
        buildRender(FluidMana.instance, FluidBlockMana.instance, "mana", manafluidLocation);
        buildRender(FluidNacre.instance, FluidBlockNacre.instance, "nacre", nacrefluidLocation);
    }
    
    private static void buildRender(Fluid fluid, Block block, String name, ModelResourceLocation resource)
    {
         Item item = Item.getItemFromBlock(block);
         if (item != null) ModelLoader.setCustomMeshDefinition(item, stack -> resource);

         ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
             protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                 return resource;
             }
         });
         FluidRegistry.addBucketForFluid(fluid);
    }
}
