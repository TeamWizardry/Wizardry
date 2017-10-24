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
		list = new ComponentList(0, 0);
		add(list);
		//list.setChildScale(1/2.0);
		list.setMarginBottom(18);
		refresh();
	}

	public void refresh() {
		if (worktable.selectedcomponent == null) {
			setVisible(false);
			setEnabled(false);
			return;
		} else {
			setVisible(true);
			setEnabled(true);
		}

		HashSet<GuiComponent> temp = new HashSet<>(list.getChildren());
		for (GuiComponent component : temp) {
			list.remove(component);
		}

		Module module = worktable.getModule(worktable.selectedcomponent);
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

			bar.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				for (GuiComponent comp : worktable.paperComponents.keySet()) if (comp.hasTag("dragging")) return txt;

				txt.add(TextFormatting.GOLD + module.getReadableName());
				if (GuiScreen.isShiftKeyDown())
					txt.add(TextFormatting.GRAY + module.getDescription());
				else txt.add(TextFormatting.GRAY + "<Sneak for info>");
				return txt;
			});

			// TODO animate here
			bar.BUS.hook(GuiComponentEvents.MouseClickEvent.class, (event) -> {
				if (!event.component.getMouseOver()) return;

				int i = worktable.selectedcomponent.hasData(Integer.class, modifier.getID()) ? worktable.selectedcomponent.getData(Integer.class, modifier.getID()) : 0;

				if (event.getButton() == EnumMouseButton.LEFT)
					worktable.selectedcomponent.setData(Integer.class, modifier.getID(), ++i);
				else if (event.getButton() == EnumMouseButton.RIGHT)
					worktable.selectedcomponent.setData(Integer.class, modifier.getID(), --i);

				Vec2d r = bar.thisPosToOtherContext(worktable.getMainComponents(), bar.getPos());
				ComponentSprite fakePlate = new ComponentSprite(TableModule.plate, 0, 0, 16, 16);
				worktable.getMainComponents().add(fakePlate);

				ComponentSprite fakeIconComp = new ComponentSprite(icon, 2, 2, 12, 12);
				fakePlate.add(fakeIconComp);

				ScheduledEventAnimation scheduled = new ScheduledEventAnimation(40, fakePlate::invalidate);
				worktable.animator.add(scheduled);

				KeyframeAnimation<ComponentSprite> animX = new KeyframeAnimation<>(fakePlate, "pos.x");
				animX.setDuration(40);
				animX.setKeyframes(new Keyframe[]{
						new Keyframe(0, fakePlate.getPos().getX(), Easing.linear),
						new Keyframe(1f, r.getX(), Easing.easeOutQuart),
				});
				worktable.animator.add(animX);

				KeyframeAnimation<ComponentSprite> animY = new KeyframeAnimation<>(fakePlate, "pos.y");
				animY.setDuration(40);
				animY.setKeyframes(new Keyframe[]{
						new Keyframe(0, fakePlate.getPos().getY(), Easing.linear),
						new Keyframe(1f, r.getY(), Easing.easeOutQuart),

				});
				worktable.animator.add(animY);
			});

			list.add(bar);
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
