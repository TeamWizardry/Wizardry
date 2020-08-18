package com.teamwizardry.wizardry.client.gui;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.librarianlib.mosaic.Mosaic;
import com.teamwizardry.librarianlib.mosaic.Sprite;
import com.teamwizardry.wizardry.client.lib.LibTheme;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class WorktableGUI extends FacadeScreen {

	public WorktableGUI() {
		super(new StringTextComponent("Worktable"));

		RectLayer background = new RectLayer(LibTheme.backgroundColor, 0, 0, 400, 200);
		getMain().add(background);
		getMain().setSize(new Vec2d(400, 200));

		Sprite dirt = new Mosaic(new ResourceLocation("minecraft:textures/block/dirt.png"), 16, 16).getSprite("");
		Sprite stone = new Mosaic(new ResourceLocation("minecraft:textures/block/stone.png"), 16, 16).getSprite("");

		SpriteLayer layer = new SpriteLayer(dirt);
		layer.setPos(new Vec2d(32, 32));
		layer.setRotation(Math.toRadians(15.0));

		layer.BUS.hook(GuiLayerEvents.MouseMove.class, mouseMove -> {
			if (layer.getMouseOver()) {
				layer.setSprite(stone);
			} else {
				layer.setSprite(dirt);
			}
		});

		getMain().add(layer);
	}
}
