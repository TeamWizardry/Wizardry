package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Module;
import ll.dev.thecodewarrior.bitfont.typesetting.TextLayoutManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.teamwizardry.wizardry.client.gui.WorktableGUI.*;

public class LayerPickPattern extends GuiLayer {

    private final WorktableGUI table;

    protected final SpriteLayer shapesLayer;
    protected final SpriteLayer shapeIconLayer;
    protected final TextLayer shapeText;
    protected final TextLayer shapeSubtitleText;

    protected final SpriteLayer actionsLayer;
    protected final SpriteLayer actionsIconLayer;
    protected final TextLayer actionText;
    protected final TextLayer actionsSubtitleText;

    protected final RectLayer bg;

    private String selectedPatternType = "none";

    protected final SpriteLayer modifierPane;
    protected final TextLayer modifierPaneRightTitle;

    protected final Map<String, Integer> modifiers = new HashMap<>();
    int tokens = 6;

    private final Map<String, SpriteLayer> modifierPluses = new HashMap<>();
    private final Map<String, SpriteLayer> modifierMinuses = new HashMap<>();

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


        modifierPane = new SpriteLayer(SPRITE_MODIFIER_BOX,
                getWidthi() / 4,
                getHeighti() + SPRITE_MODIFIER_BOX.getHeight(),
                SPRITE_MODIFIER_BOX.getWidth(),
                SPRITE_MODIFIER_BOX.getHeight());
        modifierPane.setAnchor(new Vec2d(0.5, 0.5));

        TextLayer modifierPaneLefTitle = new TextLayer(2 + 4,
                2 + 22 / 2,
                modifierPane.getWidthi() - 4,
                24 - 4,
                "MODIFIERS");
        modifierPaneLefTitle.setAnchor(new Vec2d(0, 0.5));
        modifierPaneLefTitle.fitToText(TextLayer.FitType.BOTH);
        modifierPaneLefTitle.setColor(Color.WHITE);
        modifierPane.add(modifierPaneLefTitle);

        modifierPaneRightTitle = new TextLayer(modifierPane.getWidthi() - 4 - 2,
                2 + 22 / 2,
                modifierPane.getWidthi() - 4 - 2 - 2 - 4,
                24 - 4,
                tokens + " tokens left");
        modifierPaneRightTitle.setTextAlignment(TextLayoutManager.Alignment.RIGHT);
        modifierPaneRightTitle.fitToText(TextLayer.FitType.VERTICAL);
        modifierPaneRightTitle.setAnchor(new Vec2d(1, 0.5));
        modifierPaneRightTitle.setScale(0.75);
        modifierPaneRightTitle.setColor(Color.WHITE);
        modifierPane.add(modifierPaneRightTitle);

        shapesLayer = new SpriteLayer(SPRITE_HUGE_PAPER_1, 0, getHeighti(), getHeighti(), getWidthi() / 2);
        shapesLayer.getPos_rm().animate(Vec2d.ZERO, 8, Easing.easeOutQuart);

        shapeIconLayer = new SpriteLayer(ICON_HUMAN_TARGET,
                shapesLayer.getWidthi() / 2,
                shapesLayer.getHeighti() / 2 - 50);
        shapeIconLayer.setTint(Color.BLACK);
        shapeIconLayer.setAnchor(new Vec2d(0.5, 0.5));
        shapesLayer.add(shapeIconLayer);

        shapeText = new TextLayer(shapesLayer.getWidthi() / 2,
                shapesLayer.getHeighti() / 2 - 10,
                "Pick a Shape");
        shapeText.setScale(1.5);
        shapeText.setAnchor(new Vec2d(0.5, 0.5));
        shapeText.fitToText(TextLayer.FitType.BOTH);
        shapeText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        shapesLayer.add(shapeText);

        shapeSubtitleText = new TextLayer(shapesLayer.getWidthi() / 2,
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

        actionsIconLayer = new SpriteLayer(ICON_ELECTRIC,
                actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 - 50);
        actionsIconLayer.setTint(Color.BLACK);
        actionsIconLayer.setAnchor(new Vec2d(0.5, 0.5));
        actionsLayer.add(actionsIconLayer);

        actionText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 - 10,
                "Pick an Action");
        actionText.setScale(1.5);
        actionText.setAnchor(new Vec2d(0.5, 0.5));
        actionText.fitToText(TextLayer.FitType.BOTH);
        actionText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionText);

        actionsSubtitleText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionsLayer.getHeighti() / 2 + 10,
                "Actions control what\nthe spell does");
        actionsSubtitleText.setAnchor(new Vec2d(0.5, 0.5));
        actionsSubtitleText.fitToText(TextLayer.FitType.BOTH);
        actionsSubtitleText.setColor(Color.DARK_GRAY);
        actionsSubtitleText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionsSubtitleText);


        shapesLayer.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
            if (getParent() != null && getMouseOver() && selectedPatternType.equals("none")) {
                shapesLayer.getPos_rm().animate(new Vec2d(getWidth() / 4.0, 0), 10, Easing.easeInOutQuart);
                actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, getHeighti()), 8, Easing.easeInOutQuart);

                table.revealShapes();
                selectedPatternType = "shape";
            }
        });

        actionsLayer.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
            if (getParent() != null && getMouseOver() && selectedPatternType.equals("none")) {
                shapesLayer.getPos_rm().animate(new Vec2d(0, getHeighti()), 10, Easing.easeInOutQuart);
                actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 4.0, 0), 8, Easing.easeInOutQuart);

                table.revealActions();
                selectedPatternType = "action";
            }
        });


        add(bg, shapesLayer, actionsLayer, modifierPane);
    }

    protected void pickPattern(Sprite patternSprite, Pattern pattern) {

        GuiLayer pane = null;
        SpriteLayer paneSpriteLayer = null;
        TextLayer paneTitle = null;
        TextLayer paneSubtitle = null;
        switch (selectedPatternType) {
            case "shape": {
                pane = shapesLayer;
                paneSpriteLayer = shapeIconLayer;
                paneTitle = shapeText;
                paneSubtitle = shapeSubtitleText;
                break;
            }
            case "action": {
                pane = actionsLayer;
                paneSpriteLayer = actionsIconLayer;
                paneTitle = actionText;
                paneSubtitle = actionsSubtitleText;
                break;
            }
        }
        if (pane == null || paneSpriteLayer == null || paneTitle == null || paneSubtitle == null) return;

        paneSpriteLayer.setSprite(patternSprite);
        paneSpriteLayer.getScale_rm().animate(new Vec2d(2, 2), 5, Easing.easeOutExpo);
        paneSpriteLayer.getScale_rm().animate(new Vec2d(1, 1), 5, Easing.easeInCirc, 10);

        paneTitle.setText(pattern.getRegistryName().getPath());
        paneTitle.markTextDirty();
        paneTitle.getScale_rm().animate(new Vec2d(1.75, 1.75), 5, Easing.easeOutExpo, 1);
        paneTitle.getScale_rm().animate(new Vec2d(1.5, 1.5), 6, Easing.easeInCirc, 10);

        paneSubtitle.setText("A dope component");
        paneSubtitle.markTextDirty();
        paneSubtitle.getScale_rm().animate(new Vec2d(1.25, 1.25), 5, Easing.easeOutExpo, 2);
        paneSubtitle.getScale_rm().animate(new Vec2d(1, 1), 7, Easing.easeInCirc, 10);


        List<String> attributes = null;
        Map<String, List<Double>> attributeValues = null;
        for (Module module : ComponentRegistry.getModules().values()) {
            if (module.getPattern() != null && module.getPattern().getClass().isInstance(pattern)) {
                attributes = module.getAttributes();
                attributeValues = module.getAttributeValues();
                break;
            }
        }
        if (attributes != null) {
            pane.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 8, Easing.easeInOutQuart, 8);

            modifierPluses.clear();
            modifierMinuses.clear();
            for (int i = 0; i < attributes.size(); i++) {
                createModifierTile(attributes, attributeValues, i);
            }
            modifierPane.getPos_rm()
                    .animate(new Vec2d(getWidth() / 4, getHeight() / 2), 8, Easing.easeInOutQuart, 8);
        }
    }

    private void createModifierTile(List<String> attributes, Map<String, List<Double>> attributeValues, int i) {
        String attribute = attributes.get(i);
        int attributeAmount = attributeValues.get(attribute).size();
        modifiers.putIfAbsent(attribute, 0);

        int barY = 26 + 4 + 16 * i + 8 * i;

        TextLayer attribTitle = new TextLayer(2 + 4,
                barY,
                (modifierPane.getWidthi() - 4) / 2,
                16,
                attribute);
        attribTitle.fitToText(TextLayer.FitType.VERTICAL_SHRINK);
        attribTitle.setPos(attribTitle.getPos().add(0, attribTitle.getSize().getY() / 2));
        attribTitle.setColor(Color.WHITE);

        int halfWidth = modifierPane.getWidthi() / 3;
        int barWidth =
                (modifierPane.getWidthi() - modifierPane.getWidthi() / 3) - SPRITE_MODIFIER_MINUS.getWidth() * 2 - 8 -
                        4;

        SpriteLayer minus = new SpriteLayer(SPRITE_MODIFIER_MINUS_DISABLED,
                halfWidth,
                barY);
        modifierMinuses.put(attribute, minus);

        int barX = minus.getXi() + minus.getWidthi() + 4;
        SpriteLayer knob = new SpriteLayer(SPRITE_MODIFIER_KNOB,
                barX,
                barY + SPRITE_MODIFIER_BAR.getHeight() / 2,
                SPRITE_MODIFIER_KNOB.getWidth(),
                SPRITE_MODIFIER_KNOB.getHeight());
        knob.setAnchor(new Vec2d(0.5, 0.5));

        SpriteLayer bar = new SpriteLayer(SPRITE_MODIFIER_BAR,
                barX,
                barY,
                barWidth,
                SPRITE_MODIFIER_BAR.getHeight());
        modifierPane.add(bar);

        for (int j = 0; j < attributeAmount; j++) {
            SpriteLayer increment = new SpriteLayer(SPRITE_MODIFIER_VERTICAL_BAR,
                    (int) (minus.getXi() + minus.getWidthi() + 4 + (j / (double) attributeAmount) * barWidth),
                    barY,
                    SPRITE_MODIFIER_VERTICAL_BAR.getWidth(),
                    SPRITE_MODIFIER_VERTICAL_BAR.getHeight());
            modifierPane.add(increment);
        }

        SpriteLayer plus = new SpriteLayer(SPRITE_MODIFIER_PLUS,
                bar.getXi() + bar.getWidthi() + 4,
                barY);
        modifierPluses.put(attribute, plus);

        minus.BUS.hook(GuiLayerEvents.MouseMoveOver.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attribute) > 0)
                minus.setSprite(
                        modifiers.get(attribute) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS_HOVER);
        });
        minus.BUS.hook(GuiLayerEvents.MouseMoveOff.class, (event) -> {
            minus.setSprite(modifiers.get(attribute) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS);
        });
        minus.BUS.hook(GuiLayerEvents.MouseDown.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attribute) > 0)
                minus.setSprite(
                        modifiers.get(attribute) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS_PRESSED);
        });
        minus.BUS.hook(GuiLayerEvents.MouseUp.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attribute) > 0)
                minus.setSprite(
                        modifiers.get(attribute) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS_HOVER);
        });
        minus.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attribute) > 0) {
                modifiers.put(attribute, modifiers.get(attribute) - 1);
                tokens++;
                modifierPaneRightTitle.setText(tokens + " tokens left");
                modifierPaneRightTitle.markTextDirty();

                knob.getPos_rm()
                        .animate(new Vec2d(barX + (modifiers.get(attribute) / (double) attributeAmount) * barWidth,
                                knob.getY()), 4, Easing.easeInOutQuart);
                if (modifiers.get(attribute) <= 0) minus.setSprite(SPRITE_MODIFIER_MINUS_DISABLED);

                for (Map.Entry<String, SpriteLayer> entry : modifierMinuses.entrySet()) {
                    entry.getValue().setSprite(modifiers.get(entry.getKey()) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS);
                }
                for (Map.Entry<String, SpriteLayer> entry : modifierPluses.entrySet()) {
                    entry.getValue().setSprite(modifiers.get(entry.getKey()) >= attributeAmount || tokens <= 0 ?
                            SPRITE_MODIFIER_PLUS_DISABLED :
                            SPRITE_MODIFIER_PLUS);
                }
            }
        });
        plus.BUS.hook(GuiLayerEvents.MouseMoveOver.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attribute) < attributeAmount )
                plus.setSprite(modifiers.get(attribute) >= attributeAmount  || tokens <= 0?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_HOVER);
        });
        plus.BUS.hook(GuiLayerEvents.MouseMoveOff.class, (event) -> {
            plus.setSprite(modifiers.get(attribute) >= attributeAmount || tokens <= 0 ?
                    SPRITE_MODIFIER_PLUS_DISABLED :
                    SPRITE_MODIFIER_PLUS);
        });
        plus.BUS.hook(GuiLayerEvents.MouseDown.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attribute) < attributeAmount)
                plus.setSprite(modifiers.get(attribute) >= attributeAmount || tokens <= 0 ?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_PRESSED);
        });
        plus.BUS.hook(GuiLayerEvents.MouseUp.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attribute) < attributeAmount)
                plus.setSprite(modifiers.get(attribute) >= attributeAmount  || tokens <= 0?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_HOVER);
        });
        plus.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            if (plus.getMouseOver())
                if (tokens > 0) {
                    if (modifiers.get(attribute) < attributeAmount) {
                        tokens--;
                        modifierPaneRightTitle.setText(tokens + " tokens left");
                        modifierPaneRightTitle.markTextDirty();

                        modifiers.put(attribute, modifiers.get(attribute) + 1);
                        knob.getPos_rm()
                                .animate(new Vec2d(
                                        barX + (modifiers.get(attribute) / (double) attributeAmount) * barWidth,
                                        knob.getY()), 4, Easing.easeInOutQuart);
                        if (modifiers.get(attribute) >= attributeAmount)
                            plus.setSprite(SPRITE_MODIFIER_PLUS_DISABLED);
                        for (Map.Entry<String, SpriteLayer> entry : modifierMinuses.entrySet()) {
                            entry.getValue().setSprite(modifiers.get(entry.getKey()) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS);
                        }
                        for (Map.Entry<String, SpriteLayer> entry : modifierPluses.entrySet()) {
                            entry.getValue().setSprite(modifiers.get(entry.getKey()) >= attributeAmount || tokens <= 0 ?
                                    SPRITE_MODIFIER_PLUS_DISABLED :
                                    SPRITE_MODIFIER_PLUS);
                        }
                    }
                }
        });

        modifierPane.add(attribTitle, minus, plus);
        modifierPane.add(knob);
    }

    protected void resetState() {
        selectedPatternType = "none";
        table.hideComponentGrids();
        bg.getColor_im().animate(new Color(0xA8000000, true), 12);
        shapesLayer.getPos_rm().animate(new Vec2d(0, 0), 10, Easing.easeInOutQuart);
        actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 8, Easing.easeInQuart);
    }

    protected void hideAway() {
        selectedPatternType = "none";
        bg.getColor_im().animate(new Color(0x00000000, true), 5);
        shapesLayer.getPos_rm().animate(new Vec2d(shapesLayer.getX(), getHeighti()), 5, Easing.easeInQuart);
        actionsLayer.getPos_rm().animate(new Vec2d(actionsLayer.getX(), getHeighti()), 4, Easing.easeInQuart);
        modifierPane.getPos_rm().animate(new Vec2d(getWidth() / 4,
                getHeight() + SPRITE_MODIFIER_BOX.getHeight()), 5, Easing.easeInQuart);
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();
    }
}
