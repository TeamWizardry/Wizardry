package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import com.teamwizardry.wizardry.common.spell.component.Module;
import ll.dev.thecodewarrior.bitfont.typesetting.TextLayoutManager;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    protected TextLayer modifierPaneRightTitle;

    protected final Map<String, Integer> modifiers = new HashMap<>();

    private final Map<String, SpriteLayer> modifierPluses = new HashMap<>();
    private final Map<String, SpriteLayer> modifierMinuses = new HashMap<>();

    protected int tokens = 6;

    private final Map<String, String> friendlyModifierDescriptions = new HashMap<>();
    private final TextLayer friendlyModifierDescTextShape;
    private final TextLayer friendlyModifierDescTextAction;

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


        shapesLayer = new SpriteLayer(SPRITE_HUGE_PAPER_1, 0, getHeighti(), getHeighti(), getWidthi() / 2);
        shapesLayer.setClipToBounds(true);
        shapesLayer.getPos_rm().animate(Vec2d.ZERO, 8, Easing.easeOutQuart);

        shapeIconLayer = new SpriteLayer(ICON_HUMAN_TARGET,
                shapesLayer.getWidthi() / 2,
                15);
        shapeIconLayer.setTint(Color.BLACK);
        shapeIconLayer.setAnchor(new Vec2d(0.5, 0));
        shapesLayer.add(shapeIconLayer);

        shapeText = new TextLayer(shapesLayer.getWidthi() / 2,
                shapeIconLayer.getYi() + shapeIconLayer.getHeighti() + 8,
                shapesLayer.getWidthi() - 8,
                shapesLayer.getHeighti() / 3,
                "Pick a Shape");
        shapeText.setScale(1.5);
        shapeText.setAnchor(new Vec2d(0.5, 0));
        shapeText.fitToText(TextLayer.FitType.VERTICAL);
        shapeText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        shapesLayer.add(shapeText);

        shapeSubtitleText = new TextLayer(shapesLayer.getWidthi() / 2,
                shapeText.getYi() + shapeText.getHeighti() + 8,
                shapesLayer.getWidthi() - 16,
                shapesLayer.getHeighti() / 3,
                "Shapes control how the spell is cast");
        shapeSubtitleText.setAnchor(new Vec2d(0.5, 0));
        shapeSubtitleText.fitToText(TextLayer.FitType.VERTICAL);
        shapeSubtitleText.setColor(Color.DARK_GRAY);
        shapeSubtitleText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        shapesLayer.add(shapeSubtitleText);

        friendlyModifierDescTextShape = new TextLayer(8 * 2,
                getHeighti() * 2,
                shapesLayer.getWidthi() * 2 - 16 * 4,
                shapesLayer.getHeighti(), "");
        friendlyModifierDescTextShape.setScale(0.5);
        friendlyModifierDescTextShape.fitToText(TextLayer.FitType.VERTICAL);
        shapesLayer.add(friendlyModifierDescTextShape);

        actionsLayer = new SpriteLayer(SPRITE_HUGE_PAPER_2,
                getWidthi() / 2,
                getHeighti(),
                getHeighti(),
                getWidthi() / 2);
        actionsLayer.setClipToBounds(true);
        actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 12, Easing.easeOutQuart);

        actionsIconLayer = new SpriteLayer(ICON_ELECTRIC,
                actionsLayer.getWidthi() / 2,
                15);
        actionsIconLayer.setTint(Color.BLACK);
        actionsIconLayer.setAnchor(new Vec2d(0.5, 0));
        actionsLayer.add(actionsIconLayer);

        actionText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionsIconLayer.getYi() + actionsIconLayer.getHeighti() + 8,
                actionsLayer.getWidthi() - 8,
                actionsLayer.getHeighti() / 3,
                "Pick an Action");
        actionText.setScale(1.5);
        actionText.setAnchor(new Vec2d(0.5, 0));
        actionText.fitToText(TextLayer.FitType.VERTICAL);
        actionText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionText);

        actionsSubtitleText = new TextLayer(actionsLayer.getWidthi() / 2,
                actionText.getYi() + actionText.getHeighti() + 8,
                actionsLayer.getWidthi() - 16,
                actionsLayer.getHeighti() / 3,
                "Actions control what\nthe spell does");
        actionsSubtitleText.setAnchor(new Vec2d(0.5, 0));
        actionsSubtitleText.fitToText(TextLayer.FitType.VERTICAL);
        actionsSubtitleText.setColor(Color.DARK_GRAY);
        actionsSubtitleText.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        actionsLayer.add(actionsSubtitleText);

        friendlyModifierDescTextAction = new TextLayer(8 * 2,
                getHeighti() * 2,
                actionsLayer.getWidthi() * 2 - 16 * 4,
                actionsLayer.getHeighti(), "");
        friendlyModifierDescTextAction.setScale(0.5);
        friendlyModifierDescTextAction.fitToText(TextLayer.FitType.VERTICAL);
        actionsLayer.add(friendlyModifierDescTextAction);

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

    protected void pickPattern(Sprite patternSprite, Module module) {
        modifierPane.forEachChild(modifierPane::remove);
        friendlyModifierDescriptions.clear();
        modifiers.clear();
        tokens = 6;

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


        GuiLayer pane = null;
        SpriteLayer paneSpriteLayer = null;
        TextLayer paneTitle = null;
        TextLayer paneSubtitle = null;
        TextLayer friendlyModifier = null;
        switch (selectedPatternType) {
            case "shape": {
                pane = shapesLayer;
                paneSpriteLayer = shapeIconLayer;
                paneTitle = shapeText;
                paneSubtitle = shapeSubtitleText;
                friendlyModifier = friendlyModifierDescTextShape;
                break;
            }
            case "action": {
                pane = actionsLayer;
                paneSpriteLayer = actionsIconLayer;
                paneTitle = actionText;
                paneSubtitle = actionsSubtitleText;
                friendlyModifier = friendlyModifierDescTextAction;
                break;
            }
        }
        if (pane == null || paneSpriteLayer == null || paneTitle == null || paneSubtitle == null ||
                friendlyModifier == null) return;

        paneSpriteLayer.setSprite(patternSprite);
        paneSpriteLayer.getAnchor_rm().animate(new Vec2d(0.5, 0.25), 6, Easing.easeOutExpo);
        paneSpriteLayer.getScale_rm().animate(new Vec2d(1.25, 1.25), 6, Easing.easeOutExpo);
        paneSpriteLayer.getAnchor_rm().animate(new Vec2d(0.5, 0), 3, Easing.easeInCirc, 10);
        paneSpriteLayer.getScale_rm().animate(new Vec2d(1, 1), 3, Easing.easeInCirc, 10);

        String patternName = module.getPattern().getRegistryName().getPath();
        String moduleName = module.getName();
        paneTitle.setText(I18n.format("wizardry.spell.wizardry:" + patternName + ":" + moduleName));
        paneTitle.markTextDirty();
        paneTitle.getAnchor_rm().animate(new Vec2d(0.5, 0.5 / 1.75), 6, Easing.easeOutExpo, 4);
        paneTitle.getScale_rm().animate(new Vec2d(1.75, 1.75), 6, Easing.easeOutExpo, 4);
        paneTitle.getAnchor_rm().animate(new Vec2d(0.5, 0), 3, Easing.easeInCirc, 10);
        paneTitle.getScale_rm().animate(new Vec2d(1.5, 1.5), 3, Easing.easeInCirc, 10);

        paneSubtitle.setText(I18n.format("wizardry.spell.wizardry:" + patternName + ":" + moduleName + ".desc"));
        paneSubtitle.markTextDirty();
        paneSubtitle.fitToText(TextLayer.FitType.VERTICAL);
        paneSubtitle.getScale_rm().animate(new Vec2d(1.1, 1.1), 6, Easing.easeOutExpo, 6);
        paneSubtitle.getScale_rm().animate(new Vec2d(1, 1), 4, Easing.easeInCirc, 10);

        friendlyModifier.getPos_rm().animate(new Vec2d(8 * 2, getHeight() + friendlyModifier.getHeighti()), 4, Easing.easeInCubic);

        List<String> attributes = module.getAttributes();
        Map<String, List<Double>> attributeValues = module.getAttributeValues();

        if (attributes != null) {
            pane.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 8, Easing.easeInOutQuart, 8);

            modifierPluses.clear();
            modifierMinuses.clear();
            for (int i = 0; i < attributes.size(); i++) {
                String attribute = attributes.get(i);

                friendlyModifierDescriptions.putIfAbsent(attribute, I18n.format(
                        "wizardry.spell.wizardry:" + patternName + ":" + moduleName + "." + attribute,
                        attributeValues.get(attribute).get(0)));

                createModifierTile(attribute,
                        patternName,
                        moduleName,
                        attributeValues.get(attribute).size(),
                        attributeValues.get(attribute),
                        friendlyModifier,
                        i);
            }


            TextLayer finalFriendlyModifier = friendlyModifier;
            delay(8, () -> {
                finalFriendlyModifier.setText(friendlyModifierDescriptions.values()
                        .stream()
                        .map(str -> "- " + str)
                        .collect(Collectors.joining("\n\n")));
                finalFriendlyModifier.fitToText(TextLayer.FitType.VERTICAL);
                finalFriendlyModifier.markTextDirty();
                finalFriendlyModifier.getPos_rm()
                        .animate(new Vec2d(8 * 2,
                                        actionsSubtitleText.getYi() + actionsSubtitleText.getHeighti() + 8 * 2),
                                8,
                                Easing.easeOutCubic);
            });

            modifierPane.getPos_rm()
                    .animate(new Vec2d(getWidth() / 4, getHeight() / 2), 8, Easing.easeInOutQuart, 8);

        }
    }

    private void createModifierTile(String attributeName, String pattern, String moduleName, int attributeAmount,
                                    List<Double> attribValues,
                                    TextLayer friendlyModifier, int i) {
        modifiers.putIfAbsent(attributeName, 0);


        int barY = 26 + 4 + 16 * i + 8 * i;

        TextLayer attribTitle = new TextLayer(2 + 4,
                barY,
                (modifierPane.getWidthi() - 4) / 2,
                16,
                I18n.format("wizardry.modifier." + attributeName));
        attribTitle.setTooltipText(I18n.format(
                "wizardry.spell.wizardry:" + pattern + ":" + moduleName + "." + attributeName, attributeAmount));
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
        modifierMinuses.put(attributeName, minus);

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
        modifierPluses.put(attributeName, plus);

        minus.BUS.hook(GuiLayerEvents.MouseMoveOver.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attributeName) > 0)
                minus.setSprite(
                        modifiers.get(attributeName) <= 0 ?
                                SPRITE_MODIFIER_MINUS_DISABLED :
                                SPRITE_MODIFIER_MINUS_HOVER);
        });
        minus.BUS.hook(GuiLayerEvents.MouseMoveOff.class, (event) -> {
            minus.setSprite(modifiers.get(attributeName) <= 0 ? SPRITE_MODIFIER_MINUS_DISABLED : SPRITE_MODIFIER_MINUS);
        });
        minus.BUS.hook(GuiLayerEvents.MouseDown.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attributeName) > 0)
                minus.setSprite(
                        modifiers.get(attributeName) <= 0 ?
                                SPRITE_MODIFIER_MINUS_DISABLED :
                                SPRITE_MODIFIER_MINUS_PRESSED);
        });
        minus.BUS.hook(GuiLayerEvents.MouseUp.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attributeName) > 0)
                minus.setSprite(
                        modifiers.get(attributeName) <= 0 ?
                                SPRITE_MODIFIER_MINUS_DISABLED :
                                SPRITE_MODIFIER_MINUS_HOVER);
        });
        minus.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            if (minus.getMouseOver() && modifiers.get(attributeName) > 0) {
                modifiers.put(attributeName, modifiers.get(attributeName) - 1);
                tokens++;
                modifierPaneRightTitle.setText(tokens + " tokens left");
                modifierPaneRightTitle.markTextDirty();

                knob.getPos_rm()
                        .animate(new Vec2d(barX + (modifiers.get(attributeName) / (double) attributeAmount) * barWidth,
                                knob.getY()), 4, Easing.easeInOutQuart);
                if (modifiers.get(attributeName) <= 0) minus.setSprite(SPRITE_MODIFIER_MINUS_DISABLED);

                for (Map.Entry<String, SpriteLayer> entry : modifierMinuses.entrySet()) {
                    entry.getValue()
                            .setSprite(modifiers.get(entry.getKey()) <= 0 ?
                                    SPRITE_MODIFIER_MINUS_DISABLED :
                                    SPRITE_MODIFIER_MINUS);
                }
                for (Map.Entry<String, SpriteLayer> entry : modifierPluses.entrySet()) {
                    entry.getValue().setSprite(modifiers.get(entry.getKey()) >= attributeAmount || tokens <= 0 ?
                            SPRITE_MODIFIER_PLUS_DISABLED :
                            SPRITE_MODIFIER_PLUS);
                }

                friendlyModifierDescriptions.put(attributeName, I18n.format(
                        "wizardry.spell.wizardry:" + pattern + ":" + moduleName + "." + attributeName,
                        attribValues.get(modifiers.get(attributeName))));
                friendlyModifier.setText(friendlyModifierDescriptions.values()
                        .stream()
                        .map(str -> "- " + str)
                        .collect(Collectors.joining("\n\n")));
                friendlyModifier.fitToText(TextLayer.FitType.VERTICAL);
                friendlyModifier.markTextDirty();
                friendlyModifier.getScale_rm().animate(new Vec2d(0.6, 0.6), 2, Easing.easeOutExpo);
                friendlyModifier.getScale_rm().animate(new Vec2d(0.5, 0.5), 3, Easing.easeInBounce, 2);

            }
        });
        plus.BUS.hook(GuiLayerEvents.MouseMoveOver.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attributeName) < attributeAmount)
                plus.setSprite(modifiers.get(attributeName) >= attributeAmount || tokens <= 0 ?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_HOVER);
        });
        plus.BUS.hook(GuiLayerEvents.MouseMoveOff.class, (event) -> {
            plus.setSprite(modifiers.get(attributeName) >= attributeAmount || tokens <= 0 ?
                    SPRITE_MODIFIER_PLUS_DISABLED :
                    SPRITE_MODIFIER_PLUS);
        });
        plus.BUS.hook(GuiLayerEvents.MouseDown.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attributeName) < attributeAmount)
                plus.setSprite(modifiers.get(attributeName) >= attributeAmount || tokens <= 0 ?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_PRESSED);
        });
        plus.BUS.hook(GuiLayerEvents.MouseUp.class, (event) -> {
            if (plus.getMouseOver() && modifiers.get(attributeName) < attributeAmount)
                plus.setSprite(modifiers.get(attributeName) >= attributeAmount || tokens <= 0 ?
                        SPRITE_MODIFIER_PLUS_DISABLED : SPRITE_MODIFIER_PLUS_HOVER);
        });
        plus.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            if (plus.getMouseOver())
                if (tokens > 0) {
                    if (modifiers.get(attributeName) < attributeAmount) {
                        tokens--;
                        modifierPaneRightTitle.setText(tokens + " tokens left");
                        modifierPaneRightTitle.markTextDirty();

                        modifiers.put(attributeName, modifiers.get(attributeName) + 1);
                        knob.getPos_rm()
                                .animate(new Vec2d(
                                        barX + (modifiers.get(attributeName) / (double) attributeAmount) * barWidth,
                                        knob.getY()), 4, Easing.easeInOutQuart);
                        if (modifiers.get(attributeName) >= attributeAmount)
                            plus.setSprite(SPRITE_MODIFIER_PLUS_DISABLED);
                        for (Map.Entry<String, SpriteLayer> entry : modifierMinuses.entrySet()) {
                            entry.getValue()
                                    .setSprite(modifiers.get(entry.getKey()) <= 0 ?
                                            SPRITE_MODIFIER_MINUS_DISABLED :
                                            SPRITE_MODIFIER_MINUS);
                        }
                        for (Map.Entry<String, SpriteLayer> entry : modifierPluses.entrySet()) {
                            entry.getValue().setSprite(modifiers.get(entry.getKey()) >= attributeAmount || tokens <= 0 ?
                                    SPRITE_MODIFIER_PLUS_DISABLED :
                                    SPRITE_MODIFIER_PLUS);
                        }

                        friendlyModifierDescriptions.put(attributeName, I18n.format(
                                "wizardry.spell.wizardry:" + pattern + ":" + moduleName + "." + attributeName,
                                attribValues.get(modifiers.get(attributeName))));
                        friendlyModifier.setText(friendlyModifierDescriptions.values()
                                .stream()
                                .map(str -> "- " + str)
                                .collect(Collectors.joining("\n\n")));
                        friendlyModifier.fitToText(TextLayer.FitType.VERTICAL);
                        friendlyModifier.markTextDirty();
                        friendlyModifier.getScale_rm().animate(new Vec2d(0.6, 0.6), 2, Easing.easeOutExpo);
                        friendlyModifier.getScale_rm().animate(new Vec2d(0.5, 0.5), 3, Easing.easeInBounce, 2);
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
