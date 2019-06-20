package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.GuiBase;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.math.Rect2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;

public class WorktableGui2 extends GuiBase {

	private static final int sidebarWidth = 100;
	private static final int recipeBarHeight = 50;
	private static final int cardWidth = 100;
	private static final int cardHeight = 100;

	public WorktableGui2(BlockPos pos) {
		getMain().setSize(new Vec2d(512, 256));

		RectLayer base = new RectLayer(Color.DARK_GRAY, 0, 0, 512, 256);
		getMain().add(base);

		RectLayer spellInfo = new RectLayer(Color.ORANGE);
		base.add(spellInfo);

		RectLayer spellRecipe = new RectLayer(Color.RED);
		base.add(spellRecipe);


		RectLayer cardArea = new RectLayer(Color.CYAN, 5, spellRecipe.getHeighti() + 10, base.getWidthi() - 15 - spellInfo.getWidthi(), base.getHeighti() - 15 - spellRecipe.getHeighti());
		base.add(cardArea);


		InterpLine distribution = new InterpLine(new Vec3d(0, cardArea.getHeight(), 0), new Vec3d(cardArea.getWidth(), cardArea.getHeight(), 0));
		List<Vec3d> list = distribution.list(5);
		for (int i = 0; i < list.size(); i++) {
			Vec3d vec = list.get(i);
			ComponentAddCard card = new ComponentAddCard(i, i * cardWidth + i * 5, cardArea.getHeighti());
			cardArea.componentWrapper().add(card);
		}

		getMain().BUS.hook(GuiLayerEvents.LayoutChildren.class, e -> {
			spellInfo.setFrame(new Rect2d(base.getWidthi() - sidebarWidth - 5, 5, sidebarWidth, base.getHeighti() - 10));
			spellRecipe.setFrame(new Rect2d(5, 5, base.getWidthi() - 15 - sidebarWidth, recipeBarHeight));

			cardArea.setPos(new Vec2d(5, spellRecipe.getHeighti() + 10));
			cardArea.setSize(new Vec2d(base.getWidthi() - 15 - spellInfo.getWidthi(), base.getHeighti() - 15 - spellRecipe.getHeighti()));
		});
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private static class ComponentAddCard extends GuiComponent {

		private final int index;

		public ComponentAddCard(final int index, final int x, final int y) {
			super(x, y);
			this.index = index;
			RectLayer base = new RectLayer(Color.BLUE, 0, 0, cardWidth, cardHeight);
			add(base);
		}
	}
}
