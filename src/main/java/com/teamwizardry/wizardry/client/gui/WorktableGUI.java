package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Mosaic;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class WorktableGUI extends FacadeScreen {

	protected static final Sprite SPRITE_BACKGROUND = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/table.png"), 512, 256).getSprite("");
	protected static final Sprite SPRITE_SCROLL_END = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/scroll_end.png"), 9, 5).getSprite("");
	protected static final Sprite SPRITE_SCROLL_MIDDLE = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/scroll_middle.png"), 1, 1).getSprite("");
	protected static final Sprite SPRITE_SCROLL_BUTTON = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/scroll_button.png"), 7, 16).getSprite("");
	protected static final Sprite SPRITE_BUTTON = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/button.png"), 32, 16).getSprite("");
	protected static final Sprite SPRITE_BUTTON_DEACTIVATED = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/button_deactivated.png"), 32, 16).getSprite("");
	protected static final Sprite SPRITE_BUTTON_PRESSED = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/button_pressed.png"), 32, 16).getSprite("");
	protected static final Sprite SPRITE_PLUS_PAPER = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/plus_paper.png"), 69, 76).getSprite("");
	protected static final Sprite SPRITE_BLANK_PAPER = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/blank_paper.png"), 69, 76).getSprite("");
	protected static final Sprite SPRITE_TALL_PAPER = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/big_paper.png"), 89, 118).getSprite("");
	protected static final Sprite SPRITE_HUGE_PAPER_1 = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/huge_paper_1.png"), 154, 155).getSprite("");
	protected static final Sprite SPRITE_HUGE_PAPER_2 = new Mosaic(new ResourceLocation("wizardry:textures/gui/worktable/huge_paper_2.png"), 154, 155).getSprite("");

	protected static final Sprite ICON_HUMAN_TARGET = new Mosaic(new ResourceLocation("wizardry:textures/gui/icons/human_target.png"), 32, 32).getSprite("");
	protected static final Sprite ICON_ELECTRIC = new Mosaic(new ResourceLocation("wizardry:textures/gui/icons/electric.png"), 32, 32).getSprite("");


	public WorktableGUI() {
		super(new StringTextComponent("Worktable"));

		getMain().setSize(new Vec2d(512, 256));

		SpriteLayer backgroundLayer = new SpriteLayer(SPRITE_BACKGROUND);
		getMain().add(backgroundLayer);

		WScrollPane scrollPane = new WScrollPane(22, 64, 345, 172);
		scrollPane.getContent().setSize(new Vec2d(SPRITE_PLUS_PAPER.getWidth() + 8,
				SPRITE_PLUS_PAPER.getHeight() + 8).mul(20, 1));

		for (int i = 0; i < 20; i++) {
			SpriteLayer plusSprite = new SpriteLayer(SPRITE_PLUS_PAPER);
			plusSprite.setPos(new Vec2d(i * SPRITE_PLUS_PAPER.getWidth() + (8 * (i + 1)), 8));
			plusSprite.BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
				getMain().add(new LayerPickPatternType(22, 64, 345, 172));
			});
			scrollPane.getContent().add(plusSprite);
		}

		getMain().add(scrollPane);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
