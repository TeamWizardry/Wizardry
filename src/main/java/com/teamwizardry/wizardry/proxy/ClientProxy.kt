package com.teamwizardry.wizardry.proxy

import com.teamwizardry.wizardry.client.particle.GlitterBox
import com.teamwizardry.wizardry.common.init.ModFluids
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

class ClientProxy : IProxy {
    override fun clientSetup() {
        BlockRenderLayerMap.INSTANCE.putFluids(
            RenderLayer.getTranslucent(),
            ModFluids.STILL_MANA,
            ModFluids.FLOWING_MANA
        )
        if (!isDataGenRun) {
//            physicsGlitter.addToGame();
//            predeterminedGlitter.addToGame();
//            keyFramedGlitter.addToGame();
        }
    }

    override fun registerHandlers() {}

    //    public static void dataGen(GatherDataEvent event) {
    //        isDataGenRun = !event.includeClient();
    //    }
    override fun setItemStackHandHandler(hand: Hand?, stack: ItemStack?) {
        /*if (hand == Hand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);*/
    }

    override fun spawnParticle(box: GlitterBox) {
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
    override fun openWorktableGui() {
//        MinecraftClient.getInstance().setScreen(new WorktableGUI());
    }

    companion object {
        //    private final PhysicsGlitter physicsGlitter = new PhysicsGlitter();
        //    private final PredeterminedGlitter predeterminedGlitter = new PredeterminedGlitter();
        //    private final KeyFramedGlitter keyFramedGlitter = new KeyFramedGlitter();
        private const val isDataGenRun = false
    }
}