package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class ComponentModifiers extends GuiComponent {

	private final WorktableGui worktable;
	private final ComponentList list;

	public ComponentModifiers(WorktableGui worktable) {
		super(384, 50, 80, 170);
		this.worktable = worktable;

		list = new ComponentList(0, 0, 16);
		add(list);
		refresh();
	}

	public void refresh() {
		if (worktable.selectedModule == null) {
			setVisible(false);
			return;
		} else setVisible(true);

		HashSet<GuiComponent> temp = new HashSet<>(list.getChildren());
		for (GuiComponent component : temp) {
			list.relationships.remove(component);
		}

		TableModule selectedModule = worktable.selectedModule;
		if (selectedModule == null) return;

		Module module = selectedModule.getModule();

		ModuleModifier[] applicableModifiers = module.applicableModifiers();
		if (applicableModifiers == null || applicableModifiers.length <= 0) return;

		ModuleModifier[] modifiers = module.applicableModifiers();
		if (modifiers == null) return;

		for (ModuleModifier modifier : modifiers) {
			ComponentRect bar = new ComponentRect(0, 0, getSize().getXi(), 16);
			bar.getColor().setValue(new Color(0x80000000, true));

			TableModule tableModifier = new TableModule(worktable, modifier, false, false);
			bar.add(tableModifier);

			ComponentText text = new ComponentText(20, 4, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			text.getText().setValue(TextFormatting.GREEN + modifier.getShortHandName());
			bar.add(text);

			BasicAnimation<ComponentRect> animPlate = new BasicAnimation<>(bar, "pos.y");
			animPlate.setFrom(-16);
			animPlate.setTo(0);
			animPlate.setEasing(Easing.easeOutBounce);
			animPlate.setDuration(20);
			animPlate.setCompletion(() -> {

			});
			add(animPlate);

			bar.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				txt.add(TextFormatting.GOLD + module.getReadableName());
				if (GuiScreen.isShiftKeyDown())
					txt.add(TextFormatting.GRAY + module.getDescription());
				else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.sneak"));
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
				if (worktable.selectedModule == null) return;

				int i = worktable.selectedModule.hasData(Integer.class, modifier.getID()) ? worktable.selectedModule.getData(Integer.class, modifier.getID()) : 0;

				int status = -1;
				if (event.getButton() == EnumMouseButton.LEFT) {
					worktable.selectedModule.setData(Integer.class, modifier.getID(), ++i);
					status = 0;
				} else if (event.getButton() == EnumMouseButton.RIGHT) {
					if (worktable.selectedModule.hasData(Integer.class, modifier.getID())) {

						if (worktable.selectedModule.getData(Integer.class, modifier.getID()) > 0) {
							worktable.selectedModule.setData(Integer.class, modifier.getID(), --i);
							status = 1;
						} else {
							worktable.selectedModule.removeData(Integer.class, modifier.getID());
						}
					}
				}

				TableModule fakePlate = new TableModule(worktable, modifier, false, false);
				worktable.getMainComponents().add(fakePlate);

				Vec2d from = tableModifier.thisPosToOtherContext(worktable.getMainComponents());
				Vec2d to = worktable.selectedModule.thisPosToOtherContext(worktable.getMainComponents());

				KeyframeAnimation<TableModule> anim = new KeyframeAnimation<>(fakePlate, "pos");
				anim.setDuration(20);
				if (status == 0) {
					anim.setKeyframes(new Keyframe[]{
							new Keyframe(0, from, Easing.easeOutCubic),
							new Keyframe(1f, to, Easing.easeOutCubic)
					});
				} else if (status == 1) {
					anim.setKeyframes(new Keyframe[]{
							new Keyframe(0, to, Easing.easeOutCubic),
							new Keyframe(1f, from, Easing.easeOutCubic)
					});
				}

				anim.setCompletion(fakePlate::invalidate);

				worktable.getMainComponents().add(anim);
			});

			list.add(bar);
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
