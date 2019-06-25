package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.GuiBase;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.math.Rect2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class WorktableGui2 extends GuiBase {

	private static final int sidebarWidth = 100;
	private static final int recipeBarHeight = 50;
	protected static final int cardWidth = 100;
	protected static final int cardHeight = 120;

	public final ComponentModuleSelectionMenu selectModuleType = new ComponentModuleSelectionMenu();

	public WorktableGui2(BlockPos pos) {
		getMain().setSize(new Vec2d(512, 256));

		GuiComponent base = new GuiComponent();
		getMain().add(base);

		RectLayer bg = new RectLayer(Color.DARK_GRAY, 0, 0, 512, 256);
		base.add(bg);

		RectLayer spellInfo = new RectLayer(Color.ORANGE);
		base.add(spellInfo);

		RectLayer spellRecipe = new RectLayer(Color.RED);
		base.add(spellRecipe);

		ComponentMainMenu mainMenu = new ComponentMainMenu(this);
		base.add(mainMenu);

		selectModuleType.setVisible(false);
		mainMenu.add(selectModuleType);

		getMain().BUS.hook(GuiLayerEvents.LayoutChildren.class, e -> {
			base.setFrame(bg.getFrame());
			spellInfo.setFrame(new Rect2d(bg.getWidthi() - sidebarWidth - 5, 5, sidebarWidth, bg.getHeighti() - 10));
			spellRecipe.setFrame(new Rect2d(5, 5, bg.getWidthi() - 15 - sidebarWidth, recipeBarHeight));
			mainMenu.setFrame(new Rect2d(5, spellRecipe.getHeighti() + 10, bg.getWidthi() - 15 - spellInfo.getWidthi(), bg.getHeighti() - 15 - spellRecipe.getHeighti()));
			selectModuleType.setSize(mainMenu.getSize());
		});
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
