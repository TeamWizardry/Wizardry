package com.teamwizardry.wizardry.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

/**
 * Created by Saad on 4/15/2016.
 */
public class Button extends GuiButton {

    private int ID;

    public Button(int buttonId, float x, float y, float width, float height) {
        super(buttonId, (int) x, (int) y, (int) width, (int) height, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    public int getID() {
        return ID;
    }

    public int setID(int ID) {
        this.ID = ID;
        return ID;
    }
}
