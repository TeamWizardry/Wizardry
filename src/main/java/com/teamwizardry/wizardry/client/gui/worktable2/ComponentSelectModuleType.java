package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.eventbus.Hook;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.features.math.Align2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.function.Consumer;

public class ComponentSelectModuleType extends GuiComponent {

	private ComponentModuleType effects = new ComponentModuleType(ModuleType.EFFECT, componentModuleType -> hide());
	private ComponentModuleType shapes = new ComponentModuleType(ModuleType.SHAPE, componentModuleType -> hide());
	private RectLayer bg = new RectLayer(Color.GRAY);

	public ComponentSelectModuleType() {
		setClipToBounds(true);

		add(shapes, effects);
	}

	public void hide() {
		shapes.getPos_rm().animate(Vec2d.ZERO, new Vec2d(0, getHeight()), 10, Easing.easeInCubic);
		effects.getPos_rm().animate(Vec2d.ZERO, new Vec2d(0, getHeight()), 15, Easing.easeInCubic).completion(() -> setVisible(false));
	}

	public void reveal() {
		setVisible(true);

		shapes.getPos_rm().animate(new Vec2d(0, getHeight()), Vec2d.ZERO, 15, Easing.easeOutQuart);
		effects.getPos_rm().animate(new Vec2d(0, getHeight()), Vec2d.ZERO, 10, Easing.easeOutQuart);
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		bg.setSize(getSize());

		shapes.setPos(new Vec2d(0, shapes.getY()));
		shapes.setSize(new Vec2d(getWidth() / 2.0 - 5, getHeight()));

		effects.setPos(new Vec2d(getWidth() / 2.0 + 5, effects.getY()));
		effects.setSize(new Vec2d(getWidth() / 2.0, getHeight()));
	}

	private static class ComponentModuleType extends GuiComponent {

		private final ModuleType type;
		private final Consumer<ComponentModuleType> onClick;

		private final RectLayer bg;
		private final TextLayer text;

		public ComponentModuleType(ModuleType type, Consumer<ComponentModuleType> onClick) {
			this.type = type;
			this.onClick = onClick;

			bg = new RectLayer(Color.GREEN);
			add(bg);

			text = new TextLayer(StringUtils.capitalize(type.name));
			text.setAlign(Align2d.CENTER);
			add(text);
		}

		@Hook
		private void mouseOut(GuiComponentEvents.MouseLeaveEvent e) {
			bg.setColor(Color.GREEN);
		}

		@Hook
		private void mouseIn(GuiComponentEvents.MouseEnterEvent e) {
			bg.setColor(Color.YELLOW);
		}

		@Hook
		private void click(GuiComponentEvents.MouseClickEvent e) {
			onClick.accept(this);
		}

		@Override
		public void layoutChildren() {
			super.layoutChildren();
			bg.setSize(getSize());
			text.setSize(getSize());
		}
	}
}
