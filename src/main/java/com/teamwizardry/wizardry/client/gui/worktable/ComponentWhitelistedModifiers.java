package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ComponentWhitelistedModifiers extends GuiComponent {

	private final WorktableGui worktable;
	private final ComponentList list;

	public ComponentWhitelistedModifiers(WorktableGui worktable, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.worktable = worktable;

		//ComponentText title = new ComponentText(x, y, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		//title.getText().setValue("Applicable Modifiers");
		//add(title);

		list = new ComponentList(0, 0, 16);
		add(list);
		//list.setChildScale(1/2.0);
		refresh();
	}

	public void refresh() {
		if (worktable.selectedComponent == null) {
			setVisible(false);
			addTag("disabled");
			return;
		} else {
			setVisible(true);
			removeTag("disabled");
		}

		HashSet<GuiComponent> temp = new HashSet<>(list.getChildren());
		for (GuiComponent component : temp) {
			list.relationships.remove(component);
		}

		Module module = worktable.getModule(worktable.selectedComponent);
		if (module == null) return;
		if (module.applicableModifiers() == null) return;
		if (Objects.requireNonNull(module.applicableModifiers()).length <= 0) return;

		ModuleModifier[] modifiers = module.applicableModifiers();
		if (modifiers == null) return;

		for (ModuleModifier modifier : modifiers) {
			ComponentRect bar = new ComponentRect(0, 0, getSize().getXi(), 16);
			bar.getColor().setValue(new Color(0x80000000, true));

			ComponentSprite plate = new ComponentSprite(TableModule.plate, 0, 0, 16, 16);
			bar.add(plate);

			Sprite icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + modifier.getID() + ".png"));
			ComponentSprite iconComp = new ComponentSprite(icon, 2, 2, 12, 12);
			plate.add(iconComp);

			ComponentText text = new ComponentText(20, 4, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			text.getText().setValue(TextFormatting.GREEN + modifier.getShortHandName());
			bar.add(text);

			bar.render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				for (GuiComponent comp : worktable.paperComponents.keySet()) if (comp.hasTag("dragging")) return txt;

				txt.add(TextFormatting.GOLD + module.getReadableName());
				if (GuiScreen.isShiftKeyDown())
					txt.add(TextFormatting.GRAY + module.getDescription());
				else txt.add(TextFormatting.GRAY + "<Sneak for info>");
				return txt;
			});

			bar.BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {
				bar.getColor().setValue(new Color(0x66000000, true));
			});
			bar.BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {
				bar.getColor().setValue(new Color(0x80000000, true));
			});
			bar.BUS.hook(GuiComponentEvents.MouseDownEvent.class, event -> {
				if (event.component.getMouseOver()) {
					bar.getColor().setValue(new Color(0x4D000000, true));
				}
			});
			bar.BUS.hook(GuiComponentEvents.MouseUpEvent.class, event -> {
				if (event.component.getMouseOver()) {
					bar.getColor().setValue(new Color(0x80000000, true));
				}
			});

			bar.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (!event.component.getMouseOver()) return;

				int i = worktable.selectedComponent.hasData(Integer.class, modifier.getID()) ? worktable.selectedComponent.getData(Integer.class, modifier.getID()) : 0;

				int status = -1;
				if (event.getButton() == EnumMouseButton.LEFT) {
					worktable.selectedComponent.setData(Integer.class, modifier.getID(), ++i);
					status = 0;
				} else if (event.getButton() == EnumMouseButton.RIGHT) {
					if (worktable.selectedComponent.hasData(Integer.class, modifier.getID())) {

						if (worktable.selectedComponent.getData(Integer.class, modifier.getID()) > 0) {
							worktable.selectedComponent.setData(Integer.class, modifier.getID(), --i);
							status = 1;
						} else {
							worktable.selectedComponent.removeData(Integer.class, modifier.getID());
						}
					}
				}

				ComponentSprite fakePlate = new ComponentSprite(TableModule.plate, 0, 0, 16, 16);
				bar.add(fakePlate);

				ComponentSprite fakeIconComp = new ComponentSprite(icon, 2, 2, 12, 12);
				fakePlate.add(fakeIconComp);

				ScheduledEventAnimation scheduled = new ScheduledEventAnimation(20, fakePlate::invalidate);

				Vec2d r = worktable.selectedComponent.thisPosToOtherContext(bar);

				KeyframeAnimation<ComponentSprite> animX = new KeyframeAnimation<>(fakePlate, "pos.x");
				animX.setDuration(20);
				if (status == 0) {
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(0, 0, Easing.linear),
							new Keyframe(1f, r.getX(), Easing.easeInBack)
					});
				} else if (status == 1) {
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(0, r.getX(), Easing.linear),
							new Keyframe(1f, 0, Easing.easeInBack)
					});
				}

				KeyframeAnimation<ComponentSprite> animY = new KeyframeAnimation<>(fakePlate, "pos.y");
				animY.setDuration(20);
				if (status == 0) {
					animY.setKeyframes(new Keyframe[]{
							new Keyframe(0, 0, Easing.linear),
							new Keyframe(1f, r.getY(), Easing.easeInBack)
					});
				} else if (status == 1) {
					animX.setKeyframes(new Keyframe[]{
							new Keyframe(0, r.getY(), Easing.linear),
							new Keyframe(1f, 0, Easing.easeInBack)
					});
				}
				worktable.getMainComponents().add(scheduled, animX, animY);
			});

			list.add(bar);
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
