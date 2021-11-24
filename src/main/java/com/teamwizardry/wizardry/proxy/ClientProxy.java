package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.init.ModFluids;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements IProxy {

//    private final PhysicsGlitter physicsGlitter = new PhysicsGlitter();
//    private final PredeterminedGlitter predeterminedGlitter = new PredeterminedGlitter();
//    private final KeyFramedGlitter keyFramedGlitter = new KeyFramedGlitter();

    private static boolean isDataGenRun = false;

    @Override
    public void clientSetup() {
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_MANA, ModFluids.FLOWING_MANA);

        if (!isDataGenRun) {
//            physicsGlitter.addToGame();
//            predeterminedGlitter.addToGame();
//            keyFramedGlitter.addToGame();
        }
    }

    @Override
    public void registerHandlers() {

    }

//    public static void dataGen(GatherDataEvent event) {
//        isDataGenRun = !event.includeClient();
//    }

    @Override
    public void setItemStackHandHandler(Hand hand, ItemStack stack) {
		/*if (hand == Hand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);*/
    }

    @Override
    public void spawnParticle(GlitterBox box) {
        if (box.physics) {
//            physicsGlitter.spawn(box);
        } else {
//            predeterminedGlitter.spawn(box);
        }
    }

//    @Override
//    public void spawnKeyedParticle(KeyFramedGlitterBox box) {
//        keyFramedGlitter.spawn(box);
//    }

    @Override
    public void openWorktableGui() {
//        MinecraftClient.getInstance().setScreen(new WorktableGUI());
    }
}
