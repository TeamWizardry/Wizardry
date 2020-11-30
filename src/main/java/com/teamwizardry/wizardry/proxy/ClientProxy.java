package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.gui.WorktableGUI;
import com.teamwizardry.wizardry.client.particle.*;
import com.teamwizardry.wizardry.common.init.ModBlocks;
import com.teamwizardry.wizardry.common.init.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy implements IProxy {

    private final PhysicsGlitter physicsGlitter = new PhysicsGlitter();
    private final PredeterminedGlitter predeterminedGlitter = new PredeterminedGlitter();
    private final KeyFramedGlitter keyFramedGlitter = new KeyFramedGlitter();

    private static boolean isDataGenRun = false;

    @Override
    public void clientSetup() {
        setRenderLayer(ModBlocks.liquidMana.get(), RenderType.getTranslucent());

        setRenderLayer(ModFluids.MANA_FLUID_FLOWING, RenderType.getTranslucent());
        setRenderLayer(ModFluids.MANA_FLUID, RenderType.getTranslucent());

        if (!isDataGenRun) {
            physicsGlitter.addToGame();
            predeterminedGlitter.addToGame();
            keyFramedGlitter.addToGame();
        }
    }

    private static void setRenderLayer(Block block, RenderType... types) {
        List<RenderType> typeList = Arrays.asList(types);
        RenderTypeLookup.setRenderLayer(block, typeList::contains);
    }

    private static void setRenderLayer(Fluid fluid, RenderType... types) {
        List<RenderType> typeList = Arrays.asList(types);
        RenderTypeLookup.setRenderLayer(fluid, typeList::contains);
    }

    @Override
    public void registerHandlers() {

    }

    @SubscribeEvent
    public static void dataGen(GatherDataEvent event) {
        isDataGenRun = !event.includeClient();
    }

    @Override
    public void setItemStackHandHandler(Hand hand, ItemStack stack) {
		/*if (hand == Hand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);*/
    }

    @Override
    public void spawnParticle(GlitterBox box) {
        if (box.physics) {
            physicsGlitter.spawn(box);
        } else {
            predeterminedGlitter.spawn(box);
        }
    }

    @Override
    public void spawnKeyedParticle(KeyFramedGlitterBox box) {
        keyFramedGlitter.spawn(box);
    }

    @Override
    public void openWorktableGui() {
        Minecraft.getInstance().displayGuiScreen(new WorktableGUI());
    }
}
