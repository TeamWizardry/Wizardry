package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.eventbus.Hook;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent;
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer;
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer;
import com.teamwizardry.librarianlib.features.math.Align2d;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ComponentModuleSelectionMenu extends GuiComponent implements IRevealable {

	private static final Sprite PLATE = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));
	private static final Sprite PLATE_HIGHLIGHTED = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate_highlighted.png"));


	private final ComponentModuleSelector selectShapes = new ComponentModuleSelector(ModuleType.SHAPE);
	private final ComponentModuleSelector selectEffects = new ComponentModuleSelector(ModuleType.EFFECT);

	private final ComponentModuleTypeSelector effects = new ComponentModuleTypeSelector(ModuleType.EFFECT);
	private final ComponentModuleTypeSelector shapes = new ComponentModuleTypeSelector(ModuleType.SHAPE);

	private final RectLayer bg = new RectLayer(Color.GRAY);

	public ComponentModuleSelectionMenu() {
		setClipToBounds(true);

		shapes.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (e) -> {
			shapes.hide(() -> {
			});
			effects.hide(() -> selectShapes.reveal(() -> {
			}));
		});

		effects.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (e) -> {
			shapes.hide(() -> {
			});
			effects.hide(() -> selectEffects.reveal(() -> {
			}));
		});

		add(shapes, effects, selectEffects, selectShapes);
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		bg.setSize(getSize());

		shapes.setPos(new Vec2d(0, shapes.getY()));
		shapes.setSize(new Vec2d(getWidth() / 2.0 - 5, getHeight()));

		effects.setPos(new Vec2d(getWidth() / 2.0 + 5, effects.getY()));
		effects.setSize(new Vec2d(getWidth() / 2.0, getHeight()));

		selectShapes.setSize(getSize());
		selectEffects.setSize(getSize());
	}

	@Override
	public void reveal(Runnable onComplete) {
		setVisible(true);

		shapes.reveal(() -> {
		});
		effects.reveal(() -> {
		});
	}

	@Override
	public void hide(Runnable onComplete) {
		getPos_rm().animate(Vec2d.ZERO, new Vec2d(0, getHeight()), 10, Easing.easeInCubic).completion(onComplete);
	}

	private static class ComponentModule extends GuiComponent {

		@Nullable
		private static ModuleInstance selectedModule = null;
		private final ModuleInstance module;
		private final SpriteLayer plateLayer = new SpriteLayer();
		private final SpriteLayer iconLayer = new SpriteLayer();

		ComponentModule(ModuleInstance module) {
			setVisible(true);

			this.module = module;

			plateLayer.setSprite(PLATE);
			iconLayer.setSprite(new Sprite(module.getIconLocation()));

			add(plateLayer, iconLayer);
		}

		@Hook
		private void mouseOut(GuiComponentEvents.MouseLeaveEvent e) {
			if (selectedModule == module) plateLayer.setSprite(PLATE_HIGHLIGHTED);
			else plateLayer.setSprite(PLATE);
		}

		@Hook
		private void mouseIn(GuiComponentEvents.MouseEnterEvent e) {
			plateLayer.setSprite(PLATE_HIGHLIGHTED);
		}

		@Hook
		private void click(GuiComponentEvents.MouseClickEvent e) {
			if (selectedModule == module) selectedModule = null;
			else selectedModule = module;
		}

		@Override
		public void layoutChildren() {
			super.layoutChildren();

			setSize(plateLayer.getSize());
			iconLayer.setSize(plateLayer.getSize());
		}
	}

	private static class ComponentModuleSelector extends GuiComponent implements IRevealable {

		private final ModuleType type;
		private final RectLayer bg;
		private final TextLayer text;

		private final List<ComponentModule> modules = new ArrayList<>();

		private ComponentModuleSelector(ModuleType type) {
			setVisible(false);

			this.type = type;

			bg = new RectLayer(Color.PINK);
			add(bg);

			text = new TextLayer(type.name.toUpperCase());
			text.setAlign(Align2d.TOP_CENTER);
			text.setScale(1);
			add(text);

			for (ModuleInstance module : ModuleRegistry.INSTANCE.getModules(type)) {
				ComponentModule componentModule = new ComponentModule(module);
				modules.add(componentModule);
				add(componentModule);
			}
		}

		@Override
		public void hide(Runnable onComplete) {
			getPos_rm().animate(Vec2d.ZERO, new Vec2d(0, getHeight()), 10, Easing.easeInCubic).completion(onComplete);
		}

		@Override
		public void reveal(Runnable onComplete) {
			setVisible(true);

			getPos_rm().animate(new Vec2d(0, getHeight()), Vec2d.ZERO, 15, Easing.easeOutQuart).completion(onComplete);

			final int maxColumns = 8;

			int maxRows = 0;
			for (int i = 0; i < modules.size(); i++) {
				if (i != 0 && i % maxColumns == 0) {
					maxRows++;
				}
			}

			final int spacing = 5;

			final double maxWidth = maxColumns * PLATE.getWidth() + maxColumns * spacing;
			final double maxHeight = maxRows * PLATE.getHeight() + maxRows * spacing;
			final double centerX = getWidth() / 2.0 - maxWidth / 2.0;
			final double centerY = getHeight() / 2.0 - maxHeight / 2.0;


			int r = 0, c = 0;
			for (int i = 0; i < modules.size(); i++) {
				ComponentModule module = modules.get(i);

				if (i != 0 && i % maxColumns == 0) {
					c = 0;
					r++;
				} else if (i != 0) c++;

				Vec2d pos = new Vec2d(centerX + c * PLATE.getWidth() + c * spacing, centerY + r * PLATE.getHeight() + r * spacing);
				module.getPos_rm().animate(pos.add(0, getHeight()), pos, 10 + i, Easing.easeOutQuart);
			}
		}

		@Override
		public void layoutChildren() {
			super.layoutChildren();
			bg.setSize(getSize());
			text.setSize(getSize());
		}
	}

	private static class ComponentModuleTypeSelector extends GuiComponent implements IRevealable {

		private final ModuleType type;

		private final RectLayer bg;
		private final TextLayer text;

		private ComponentModuleTypeSelector(ModuleType type) {
			setVisible(false);
			this.type = type;

			bg = new RectLayer(Color.GREEN);
			add(bg);

			text = new TextLayer(type.name.toUpperCase());
			text.setAlign(Align2d.CENTER);
			add(text);
		}

		@Override
		public void hide(Runnable onComplete) {
			getPos_rm().animate(Vec2d.ZERO, new Vec2d(0, getHeight()), 10, Easing.easeInCubic).completion(onComplete);
		}

		@Override
		public void reveal(Runnable onComplete) {
			setVisible(true);

			getPos_rm().animate(new Vec2d(0, getHeight()), Vec2d.ZERO, 15, Easing.easeOutQuart).completion(onComplete);
		}

		@Hook
		private void mouseOut(GuiComponentEvents.MouseLeaveEvent e) {
			bg.setColor(Color.GREEN);
		}

		@Hook
		private void mouseIn(GuiComponentEvents.MouseEnterEvent e) {
			bg.setColor(Color.YELLOW);
		}

		@Override
		public void layoutChildren() {
			super.layoutChildren();
			bg.setSize(getSize());
			text.setSize(getSize());
		}
	}

}
