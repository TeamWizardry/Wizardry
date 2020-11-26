package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {

    public static ForgeFlowingFluid.Properties MANA_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> ModFluids.MANA_FLUID,
            () -> ModFluids.MANA_FLUID_FLOWING,
            FluidAttributes.builder(
            new ResourceLocation(Wizardry.MODID, "fluid/mana_still"),
            new ResourceLocation(Wizardry.MODID, "fluid/mana_flowing"))
            .density(200)
            .viscosity(500)
            .luminosity(0)
            .temperature(20)
            .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY))
            .block(() -> (FlowingFluidBlock) ModBlocks.liquidMana.get())
            .bucket(ModItems.manaBucket::get);

    public static ForgeFlowingFluid.Flowing MANA_FLUID_FLOWING = new ForgeFlowingFluid.Flowing(MANA_FLUID_PROPERTIES);
    public static ForgeFlowingFluid.Source MANA_FLUID = new ForgeFlowingFluid.Source(MANA_FLUID_PROPERTIES);

    @SubscribeEvent
    public static void registerFluids(RegistryEvent.Register<Fluid> event) {

        event.getRegistry().register(MANA_FLUID_FLOWING.setRegistryName("mana_fluid_flowing"));
        event.getRegistry().register(MANA_FLUID.setRegistryName("mana_fluid"));
    }
}
