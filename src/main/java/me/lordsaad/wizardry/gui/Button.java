package me.lordsaad.wizardry.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;

/**
 * Created by Saad on 4/15/2016.
 */
public class Button extends GuiButton {

    public Button(int buttonId, float x, float y, float width, float height) {
        super(buttonId, (int) x, (int) y, (int) width, (int) height, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_LEVER_CLICK, 1.0F));
    }
}
