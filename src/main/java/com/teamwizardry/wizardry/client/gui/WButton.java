package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import ll.dev.thecodewarrior.bitfont.typesetting.TextLayoutManager;

import java.awt.*;

import static com.teamwizardry.wizardry.client.gui.WorktableGUI.*;

@SuppressWarnings("SuspiciousNameCombination")
public class WButton extends GuiLayer {

    public WButton(Sprite icon, String text, int posX, int posY, int width, int height) {
        super(posX, posY, width, height);

        SpriteLayer buttonBG = new SpriteLayer(SPRITE_BUTTON, 0, 0, width, height);
        add(buttonBG);

        TextLayer textLayer = new TextLayer(getWidthi() / 2, getHeighti() / 2, getWidthi(), getHeighti(), text);
        textLayer.setColor(Color.WHITE);
        textLayer.setAnchor(new Vec2d(0.5, 0.5));
        textLayer.fitToText(TextLayer.FitType.BOTH);
        textLayer.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        add(textLayer);


        if (icon != null) {
            SpriteLayer spriteLayer = new SpriteLayer(icon, 0, 0, height, height);
            buttonBG.add(spriteLayer);
            setWidth(height + textLayer.getWidth() + 16);
        } else {
            setWidth(textLayer.getWidth() + 16);
        }

        textLayer.setWidth(getWidth());
        textLayer.setPos(new Vec2d(icon != null ? height : 0 + getWidth() / 2, getHeight() / 2));
        buttonBG.setWidth(getWidth());


        BUS.hook(GuiLayerEvents.MouseMoveOver.class, (event) -> {
            if (getMouseOver())
                buttonBG.setSprite(SPRITE_BUTTON_HOVER);
        });
        BUS.hook(GuiLayerEvents.MouseMoveOff.class, (event) -> {
            buttonBG.setSprite(SPRITE_BUTTON);
        });
        BUS.hook(GuiLayerEvents.MouseDown.class, (event) -> {
            if (getMouseOver())
                buttonBG.setSprite(SPRITE_BUTTON_PRESSED);
        });
        BUS.hook(GuiLayerEvents.MouseUp.class, (event) -> {
            if (getMouseOver())
                buttonBG.setSprite(SPRITE_BUTTON_HOVER);
        });
    }
}
