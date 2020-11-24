package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import ll.dev.thecodewarrior.bitfont.typesetting.TextLayoutManager;

import java.awt.*;

import static com.teamwizardry.wizardry.client.gui.WorktableGUI.*;

public class LayerPickPattern extends GuiLayer {

    protected final SpriteLayer shapesLayer;
    protected final SpriteLayer actionsLayer;
    protected final RectLayer bg;
    private final WorktableGUI table;

    public LayerPickPattern(WorktableGUI table, int posX, int posY,
                            int width, int height) {
        super(posX, posY, width, height);
        this.table = table;
        setClipToBounds(true);

        bg = new RectLayer(new Color(0x00000000, true), 0, 0, getWidthi(), getHeighti());
        bg.getColor_im().animate(new Color(0xA8000000, true), 12);
        bg.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            table.back();
        });


        shapesLayer = new SpriteLayer(SPRITE_HUGE_PAPER_1, 0, getHeighti(), getHeighti(), getWidthi() / 2);
        shapesLayer.getPos_rm().animate(Vec2d.ZERO, 8, Easing.easeOutQuart);

        SpriteLayer shapeIconLayer = new SpriteLayer(ICON_HUMAN_TARGET,
                shapesLayer.getWidthi() / 2,
                shapesLayer.getHeighti() / 2 - 50);
        shapeIconLayer.setAnchor(new Vec2d(0.5, 0.5));
        shapesLayer.add(shapeIconLayer);

        TextLayer shapeText = new TextLayer(shapesLayer.getWidthi() / 2,
                shapesLayer.getHeighti() / 2 - 10,
                "Pick a Shape");
        shapeText.setScale(1.5);
        shapeText.setAnchor(new Vec2d(0.5, 0.5));
        shapeText.fitToText(TextLayer.FitType.BOTH);
        shapeText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        shapesLayer.add(shapeText);

        TextLayer shapeSubtitleText = new TextLayer(shapesLayer.getWidthi() / 2,
                shapesLayer.getHeighti() / 2 + 10,
                "Shapes control how\nthe spell is cast");
        shapeSubtitleText.setAnchor(new Vec2d(0.5, 0.5));
        shapeSubtitleText.fitToText(TextLayer.FitType.BOTH);
        shapeSubtitleText.setColor(Color.DARK_GRAY);
        shapeSubtitleText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        shapesLayer.add(shapeSubtitleText);


        actionsLayer = new SpriteLayer(SPRITE_HUGE_PAPER_2,
                getWidthi() / 2,
                getHeighti(),
                getHeighti(),
                getWidthi() / 2);
        actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 12, Easing.easeOutQuart);

        SpriteLayer actionsIconLayer = new SpriteLayer(ICON_ELECTRIC,
                actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 - 50);
        actionsIconLayer.setAnchor(new Vec2d(0.5, 0.5));
        actionsLayer.add(actionsIconLayer);

        TextLayer actionText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 - 10,
                "Pick an Action");
        actionText.setScale(1.5);
        actionText.setAnchor(new Vec2d(0.5, 0.5));
        actionText.fitToText(TextLayer.FitType.BOTH);
        actionText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionText);

        TextLayer actionsSubtitleText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 + 10,
                "Actions control what\nthe spell does");
        actionsSubtitleText.setAnchor(new Vec2d(0.5, 0.5));
        actionsSubtitleText.fitToText(TextLayer.FitType.BOTH);
        actionsSubtitleText.setColor(Color.DARK_GRAY);
        actionsSubtitleText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionsSubtitleText);


        shapesLayer.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
            if (getParent() != null) {
                shapesLayer.getPos_rm().animate(new Vec2d(getWidth() / 4.0, 0), 10, Easing.easeInOutQuart);
                actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, getHeighti()), 8, Easing.easeInOutQuart);

                table.revealShapes();
            }
        });

        actionsLayer.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
            if (getParent() != null) {
                shapesLayer.getPos_rm().animate(new Vec2d(0, getHeighti()), 10, Easing.easeInOutQuart);
                actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 4.0, 0), 8, Easing.easeInOutQuart);

                table.revealActions();
            }
        });


        add(bg, shapesLayer, actionsLayer);
    }

    protected void resetState() {
        table.hideComponentGrids();
        bg.getColor_im().animate(new Color(0xA8000000, true), 12);
        shapesLayer.getPos_rm().animate(new Vec2d(0, 0), 10, Easing.easeInOutQuart);
        actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 8, Easing.easeInQuart);
    }

    protected void hideAway() {
        bg.getColor_im().animate(new Color(0x00000000, true), 5);
        shapesLayer.getPos_rm().animate(new Vec2d(shapesLayer.getX(), getHeighti()), 5, Easing.easeInOutQuart);
        actionsLayer.getPos_rm().animate(new Vec2d(actionsLayer.getX(), getHeighti()), 4, Easing.easeInQuart);
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();
    }
}
