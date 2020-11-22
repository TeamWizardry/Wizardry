package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.gui.WorktableGUI;
import com.teamwizardry.wizardry.client.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements IProxy {

    private final PhysicsGlitter physicsGlitter = new PhysicsGlitter();
    private final PredeterminedGlitter predeterminedGlitter = new PredeterminedGlitter();
    private final KeyFramedGlitter keyFramedGlitter = new KeyFramedGlitter();

    @Override
    public void registerHandlers() {
        physicsGlitter.addToGame();
        predeterminedGlitter.addToGame();
        keyFramedGlitter.addToGame();
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
