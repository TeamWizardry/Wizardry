package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.GuiBase;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.math.Rect2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class WorktableGui2 extends GuiBase {

	private static final int sidebarWidth = 100;
	private static final int recipeBarHeight = 50;
	protected static final int cardWidth = 100;
	protected static final int cardHeight = 200;

	public final Set<ComponentAddCard> cards = new HashSet<>();

	public WorktableGui2(BlockPos pos) {
		getMain().setSize(new Vec2d(512, 256));

		GuiComponent base = new GuiComponent();
		getMain().add(base);

		RectLayer bg = new RectLayer(Color.DARK_GRAY, 0, 0, 512, 256);
		getMain().add(bg);

		RectLayer spellInfo = new RectLayer(Color.ORANGE);
		bg.add(spellInfo);

		RectLayer spellRecipe = new RectLayer(Color.RED);
		bg.add(spellRecipe);

		RectLayer cardArea = new RectLayer(Color.CYAN);
		cardArea.setClipToBounds(true);
		bg.add(cardArea);

		getMain().BUS.hook(GuiLayerEvents.LayoutChildren.class, e -> {
			base.setFrame(bg.getFrame());
			spellInfo.setFrame(new Rect2d(bg.getWidthi() - sidebarWidth - 5, 5, sidebarWidth, bg.getHeighti() - 10));
			spellRecipe.setFrame(new Rect2d(5, 5, bg.getWidthi() - 15 - sidebarWidth, recipeBarHeight));
			cardArea.setFrame(new Rect2d(5, spellRecipe.getHeighti() + 10, bg.getWidthi() - 15 - spellInfo.getWidthi(), bg.getHeighti() - 15 - spellRecipe.getHeighti()));

			card.setPos(new Vec2d(card.index * cardWidth + card.index * 5, cardArea.getHeighti() / 2.0 - cardHeight / 2.0));
		});
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
