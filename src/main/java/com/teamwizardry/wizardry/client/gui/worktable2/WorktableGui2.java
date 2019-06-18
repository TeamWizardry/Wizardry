package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.facade.GuiBase;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer;
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.math.Rect2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import kotlin.jvm.functions.Function1;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class WorktableGui2 extends GuiBase {

	private static final int sidebarWidth = 100;
	private static final int recipeBarHeight = 50;
	private static final int cardWidth = 100;
	private static final int cardHeight = 100;

	public WorktableGui2(BlockPos pos) {
		getMain().setSize(new Vec2d(512, 256));

		RectLayer base = new RectLayer(Color.DARK_GRAY, 0, 0, 512, 256);
		getMain().add(base);

		RectLayer spellInfo = new RectLayer(Color.ORANGE, base.getWidthi() - sidebarWidth - 5, 5, sidebarWidth, base.getHeighti() - 10);
		base.add(spellInfo);

		RectLayer spellRecipe = new RectLayer(Color.RED, 5, 5, base.getWidthi() - 15 - sidebarWidth, recipeBarHeight);
		base.add(spellRecipe);

		getMain().BUS.hook(GuiLayerEvents.LayoutChildren.class, e -> {
			spellInfo.setFrame(new Rect2d(base.getWidthi() - sidebarWidth - 5, 5, sidebarWidth, base.getHeighti() - 10));
			spellRecipe.setFrame(new Rect2d(5, 5, base.getWidthi() - 15 - sidebarWidth, recipeBarHeight));
		});
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private static class ComponentAddCard extends GuiComponent {

		public ComponentAddCard() {

		}

		@Override
		public double getHeight() {
			return super.getHeight();
		}

		@Override
		public void setHeight(double value) {

		}

		@Override
		public float getHeightf() {
			return 0;
		}

		@Override
		public void setHeightf(float value) {

		}

		@Override
		public int getHeighti() {
			return 0;
		}

		@Override
		public void setHeighti(int value) {

		}

		@Override
		public double getWidth() {
			return 0;
		}

		@Override
		public void setWidth(double value) {

		}

		@Override
		public float getWidthf() {
			return 0;
		}

		@Override
		public void setWidthf(float value) {

		}

		@Override
		public int getWidthi() {
			return 0;
		}

		@Override
		public void setWidthi(int value) {

		}

		@Override
		public double getX() {
			return 0;
		}

		@Override
		public void setX(double value) {

		}

		@Override
		public float getXf() {
			return 0;
		}

		@Override
		public void setXf(float value) {

		}

		@Override
		public int getXi() {
			return 0;
		}

		@Override
		public void setXi(int value) {

		}

		@Override
		public double getY() {
			return 0;
		}

		@Override
		public void setY(double value) {

		}

		@Override
		public float getYf() {
			return 0;
		}

		@Override
		public void setYf(float value) {

		}

		@Override
		public int getYi() {
			return 0;
		}

		@Override
		public void setYi(int value) {

		}

		@Nullable
		@Override
		public Rect2d getContentsBounds() {
			return null;
		}

		@Nullable
		@Override
		public Rect2d getContentsBounds(@NotNull Function1<? super GuiLayer, Boolean> includeLayer) {
			return null;
		}
	}
}
