package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Mosaic;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Module;
import ll.dev.thecodewarrior.bitfont.typesetting.TextLayoutManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Map;

public class WorktableGUI extends FacadeScreen {

    protected static final Sprite SPRITE_BACKGROUND = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/table.png"), 512, 256).getSprite("");
    protected static final Sprite SPRITE_SCROLL_END = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/scroll_end.png"), 9, 5).getSprite("");
    protected static final Sprite SPRITE_SCROLL_MIDDLE = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/scroll_middle.png"), 1, 1).getSprite("");
    protected static final Sprite SPRITE_SCROLL_BUTTON = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/scroll_button.png"), 7, 16).getSprite("");
    protected static final Sprite SPRITE_BUTTON = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/button.png"), 32, 16).getSprite("");
    protected static final Sprite SPRITE_BUTTON_DEACTIVATED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/button_deactivated.png"), 32, 16).getSprite("");
    protected static final Sprite SPRITE_BUTTON_PRESSED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/button_pressed.png"), 32, 16).getSprite("");
    protected static final Sprite SPRITE_BUTTON_HOVER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/button_hover.png"), 32, 16).getSprite("");
    protected static final Sprite SPRITE_PLUS_PAPER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/plus_paper.png"), 69, 76).getSprite("");
    protected static final Sprite SPRITE_BLANK_PAPER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/blank_paper.png"), 69, 76).getSprite("");
    protected static final Sprite SPRITE_TALL_PAPER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/big_paper.png"), 89, 118).getSprite("");
    protected static final Sprite SPRITE_HUGE_PAPER_1 = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/huge_paper_1.png"), 154, 155).getSprite("");
    protected static final Sprite SPRITE_HUGE_PAPER_2 = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/huge_paper_2.png"), 154, 155).getSprite("");
    protected static final Sprite SPRITE_GRID = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/grid.png"), 80, 156).getSprite("");
    protected static final Sprite SPRITE_GRID_CELL = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/grid_cell.png"), 28, 28).getSprite("");

    protected static final Sprite SPRITE_MODIFIER_BAR = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_bar.png"), 85, 14 + 1).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_BOX = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_box.png"), 168, 156).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_KNOB = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_knob.png"), 8, 8).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_MINUS = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_minus.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_MINUS_DISABLED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_minus_disabled.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_MINUS_HOVER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_minus_hover.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_MINUS_PRESSED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_minus_pressed.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_PLUS = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_plus.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_PLUS_DISABLED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_plus_disabled.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_PLUS_HOVER = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_plus_hover.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_PLUS_PRESSED = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_plus_pressed.png"), 14, 14).getSprite("");
    protected static final Sprite SPRITE_MODIFIER_VERTICAL_BAR = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/worktable/modifier_vertical_bar.png"), 2, 14).getSprite("");


    protected static final Sprite ICON_HUMAN_TARGET = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/icons/human_target.png"), 32, 32).getSprite("");
    protected static final Sprite ICON_ELECTRIC = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/icons/electric.png"), 32, 32).getSprite("");

    protected final WButton backButton;
    protected final SpriteLayer shapeGrid;
    protected final SpriteLayer actionGrid;
    protected final GuiLayer sideBarPane;

    /// ---- STORY-BOARDED FIELDS ---- ///
    protected LayerPickPattern pickPattern;
    protected boolean isPickingPattern = false;

    public WorktableGUI() {
        super(new StringTextComponent("Worktable"));

        getMain().setSize(new Vec2d(512, 256));

        SpriteLayer backgroundLayer = new SpriteLayer(SPRITE_BACKGROUND);
        getMain().add(backgroundLayer);

        WScrollPane scrollPane = new WScrollPane(22, 64, 345, 172);
        scrollPane.getContent().setSize(new Vec2d(SPRITE_PLUS_PAPER.getWidth() + 8,
                SPRITE_PLUS_PAPER.getHeight() + 8).mul(20, 1));

        pickPattern = new LayerPickPattern(this, 22, 64, 345, 172);
        for (int i = 0; i < 20; i++) {
            SpriteLayer plusSprite = new SpriteLayer(SPRITE_PLUS_PAPER);
            plusSprite.setPos(new Vec2d(i * SPRITE_PLUS_PAPER.getWidth() + (8 * (i + 1)), 8));
            plusSprite.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
                showBackButton();
                getMain().add(pickPattern);
                isPickingPattern = true;
            });
            scrollPane.getContent().add(plusSprite);
        }
        getMain().add(scrollPane);

        sideBarPane = new GuiLayer(384, 22, 106, 214);
        sideBarPane.setClipToBounds(true);
        getMain().add(sideBarPane);

        backButton = new WButton(null,
                "Back",
                sideBarPane.getWidthi() / 2,
                -SPRITE_BUTTON.getHeight(),
                SPRITE_BUTTON.getWidth(),
                SPRITE_BUTTON.getHeight());
        backButton.setAnchor(new Vec2d(0.5, 0.5));
        backButton.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
            if (backButton.getMouseOver()) {
                back();
            }
        });
        sideBarPane.add(backButton);

        shapeGrid = new SpriteLayer(SPRITE_GRID,
                sideBarPane.getWidthi() / 2,
                sideBarPane.getHeighti() + SPRITE_GRID.getHeight() / 2);
        shapeGrid.setAnchor(new Vec2d(0.5, 0.5));
        sideBarPane.add(shapeGrid);
        setPatternsToGrid(shapeGrid, "SHAPES", PatternShape.class);

        actionGrid = new SpriteLayer(SPRITE_GRID,
                sideBarPane.getWidthi() / 2,
                sideBarPane.getHeighti() + SPRITE_GRID.getHeight() / 2);
        actionGrid.setAnchor(new Vec2d(0.5, 0.5));
        sideBarPane.add(actionGrid);
        setPatternsToGrid(actionGrid, "ACTIONS", PatternEffect.class);
    }

    private void setPatternsToGrid(GuiLayer parent, String title, Class<? extends Pattern> clazz) {
        TextLayer titleLayer = new TextLayer(parent.getWidthi() / 2, 26, parent.getWidthi(), 26, title);
        titleLayer.setAnchor(new Vec2d(0.5, 0.5));
        titleLayer.fitToText(TextLayer.FitType.BOTH);
        titleLayer.setTextAlignment(TextLayoutManager.Alignment.CENTER);
        titleLayer.setPos(new Vec2d(parent.getWidth() / 2.0, 26 / 2.0));
        titleLayer.setColor(Color.WHITE);
        parent.add(titleLayer);

        int row = 0;
        int column = 0;
        for (Map.Entry<String, Module> entry : ComponentRegistry.getModules().entrySet()) {
            if (!clazz.isInstance(entry.getValue().getPattern())) continue;

            SpriteLayer cell = new SpriteLayer(SPRITE_GRID_CELL,
                    column * 24 + 2 + 2 * column,
                    26 + row * 24 + 2 * row,
                    24,
                    24);
            Sprite sprite = new Mosaic(new ResourceLocation(
                    "wizardry:textures/modules/" + entry.getValue().getName() +
                            ".png"), 32, 32).getSprite("");
            SpriteLayer patternSprite = new SpriteLayer(sprite, 2, 2, 24 - 4, 24 - 4);
            patternSprite.setTooltipText(I18n.format(
                    "wizardry.spell.wizardry:" + entry.getValue().getPattern().getRegistryName().getPath() + ":" +
                            entry.getValue().getName()));
            patternSprite.BUS.hook(GuiLayerEvents.MouseClick.class, (event) -> {
                pickPattern.pickPattern(sprite, entry.getValue());
            });
            cell.add(patternSprite);
            parent.add(cell);

            column++;
            if (column > 3) {
                row++;
                column = 0;
            }
        }
    }

    protected void back() {
        if (isPickingPattern) {
            hideComponentGrids();
            pickPattern.hideAway();
            getMain().delay(14, () -> {
                getMain().remove(pickPattern);
                pickPattern = new LayerPickPattern(this, 22, 64, 345, 172);
            });
            hideBackButton();
            isPickingPattern = false;
        }
    }

    protected void showBackButton() {
        backButton.getPos_rm().animate(new Vec2d(sideBarPane.getWidth() / 2.0, 16), 8, Easing.easeInOutQuart);
    }

    protected void hideBackButton() {
        backButton.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0, -SPRITE_BUTTON.getHeight()), 8, Easing.easeInOutQuart);
    }

    protected void hideComponentGrids() {
        shapeGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0,
                                sideBarPane.getHeight() + SPRITE_GRID.getHeight() / 2.0),
                        8,
                        Easing.easeOutQuart);
        actionGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0,
                        sideBarPane.getHeight() + SPRITE_GRID.getHeight() / 2.0), 8, Easing.easeOutQuart);
    }

    protected void revealShapes() {
        showBackButton();
        shapeGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0, sideBarPane.getHeight() / 2.0),
                        8,
                        Easing.easeOutQuart);
        actionGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0,
                        sideBarPane.getHeight() + SPRITE_GRID.getHeight() / 2.0), 8, Easing.easeOutQuart);
    }

    protected void revealActions() {
        showBackButton();
        actionGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0, sideBarPane.getHeight() / 2.0),
                        8,
                        Easing.easeOutQuart);
        shapeGrid.getPos_rm()
                .animate(new Vec2d(sideBarPane.getWidth() / 2.0,
                        sideBarPane.getHeight() + SPRITE_GRID.getHeight() / 2.0), 8, Easing.easeOutQuart);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
