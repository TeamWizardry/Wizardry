package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Mosaic;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

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

    protected static final Sprite ICON_HUMAN_TARGET = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/icons/human_target.png"), 32, 32).getSprite("");
    protected static final Sprite ICON_ELECTRIC = new Mosaic(new ResourceLocation(
            "wizardry:textures/gui/icons/electric.png"), 32, 32).getSprite("");

    protected final WButton backButton;
    protected final SpriteLayer shapeGrid;
    protected final SpriteLayer actionGrid;
    protected final GuiLayer sideBarPane;
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
        setPatternsToGrid(shapeGrid, PatternShape.class);

        actionGrid = new SpriteLayer(SPRITE_GRID,
                sideBarPane.getWidthi() / 2,
                sideBarPane.getHeighti() + SPRITE_GRID.getHeight() / 2);
        actionGrid.setAnchor(new Vec2d(0.5, 0.5));
        sideBarPane.add(actionGrid);
        setPatternsToGrid(actionGrid, PatternEffect.class);
    }

    private void setPatternsToGrid(GuiLayer parent, Class<? extends Pattern> clazz) {
        IForgeRegistry<Pattern> registry = GameRegistry.findRegistry(Pattern.class);
        int row = 0;
        int column = 0;
        for (Map.Entry<ResourceLocation, Pattern> patternEntry : registry.getEntries()) {
            //TODO: always fails: if (!(patternEntry.getValue().getClass().isInstance(clazz))) continue;

            SpriteLayer cell = new SpriteLayer(SPRITE_GRID_CELL,
                    column * 24 + 2 + 2 * column,
                    26 + row * 24 + 2 * row,
                    24,
                    24);
            SpriteLayer patternSprite = new SpriteLayer(new Mosaic(new ResourceLocation(
                    "wizardry:textures/modules/" + patternEntry.getValue().getRegistryName().getPath() +
                            ".png"), 32, 32).getSprite(""), 2, 2, 24 - 4, 24 - 4);
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
