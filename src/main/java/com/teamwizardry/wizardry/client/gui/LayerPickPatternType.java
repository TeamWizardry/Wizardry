package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.layer.GuiLayer;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.math.Easing;
import com.teamwizardry.librarianlib.math.Vec2d;

import java.awt.*;

import static com.teamwizardry.wizardry.client.gui.WorktableGUI.*;

public class LayerPickPatternType extends GuiLayer {

	public LayerPickPatternType(int posX, int posY, int width, int height) {
		super(posX, posY, width, height);
		setClipToBounds(true);

		RectLayer bg = new RectLayer(new Color(0x00000000, true), 0, 0, getWidthi(), getHeighti());
		bg.getColor_im().animate(new Color(0x70000000, true), 12);


		SpriteLayer shapesLayer = new SpriteLayer(SPRITE_HUGE_PAPER_1, 0, getHeighti(), getHeighti(), getWidthi() / 2);
		shapesLayer.getPos_rm().animate(Vec2d.ZERO, 8, Easing.easeOutQuart);

		SpriteLayer shapeIconLayer = new SpriteLayer(ICON_HUMAN_TARGET, shapesLayer.getWidthi() / 2, shapesLayer.getHeighti() / 2 - 50);
		shapeIconLayer.setAnchor(new Vec2d(0.5, 0.5));
		shapesLayer.add(shapeIconLayer);

		TextLayer shapeText = new TextLayer(shapesLayer.getWidthi() / 2, shapesLayer.getHeighti() / 2 - 10, "Pick a Shape");
		shapeText.setScale(1.5);
		shapeText.setAnchor(new Vec2d(0.5, 0.5));
		shapeText.fitToText();
		shapesLayer.add(shapeText);

		TextLayer shapeSubtitleText = new TextLayer(shapesLayer.getWidthi() / 2, shapesLayer.getHeighti() / 2 + 10, "Shapes control how the spell is cast");
		shapeSubtitleText.setAnchor(new Vec2d(0.5, 0.5));
		shapeSubtitleText.fitToText();
		shapeSubtitleText.setColor(Color.DARK_GRAY);
		shapeSubtitleText.setWrap(true);
		shapeSubtitleText.setSize(new Vec2d(shapesLayer.getWidth() / 1.5, shapeSubtitleText.getHeight()));
		shapesLayer.add(shapeSubtitleText);


		SpriteLayer actionsLayer = new SpriteLayer(SPRITE_HUGE_PAPER_2, getWidthi() / 2, getHeighti(), getHeighti(), getWidthi() / 2);
		actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, 0), 12, Easing.easeOutQuart);

		SpriteLayer actionsIconLayer = new SpriteLayer(ICON_ELECTRIC, actionsLayer.getWidthi() / 2, actionsLayer.getHeighti() / 2 - 50);
		actionsIconLayer.setAnchor(new Vec2d(0.5, 0.5));
		actionsLayer.add(actionsIconLayer);

		TextLayer elementText = new TextLayer(actionsLayer.getWidthi() / 2, actionsLayer.getHeighti() / 2 - 10, "Pick an Action");
		elementText.setScale(1.5);
		elementText.setAnchor(new Vec2d(0.5, 0.5));
		elementText.fitToText();
		actionsLayer.add(elementText);

		TextLayer actionsSubtitleText = new TextLayer(actionsLayer.getWidthi() / 2, actionsLayer.getHeighti() / 2 + 10, "Actions control what the spell does");
		actionsSubtitleText.setAnchor(new Vec2d(0.5, 0.5));
		actionsSubtitleText.fitToText();
		actionsSubtitleText.setColor(Color.DARK_GRAY);
		actionsSubtitleText.setWrap(true);
		actionsSubtitleText.setSize(new Vec2d(actionsLayer.getWidth() / 1.5, actionsSubtitleText.getHeight()));
		actionsLayer.add(actionsSubtitleText);


		BUS.hook(GuiLayerEvents.MouseClick.class, mouseClick -> {
			if (getParent() != null) {
				shapesLayer.getPos_rm().animate(new Vec2d(0, getHeighti()), 12, Easing.easeInQuart);
				actionsLayer.getPos_rm().animate(new Vec2d(getWidth() / 2.0, getHeighti()), 8, Easing.easeInQuart);
				bg.getColor_im().animate(new Color(0x00000000, true), 12);

				delay(14, () -> getParent().remove(this));
			}
		});

		add(bg, shapesLayer, actionsLayer);
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();
	}
}
