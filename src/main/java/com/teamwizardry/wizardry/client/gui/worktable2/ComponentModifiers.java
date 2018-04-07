package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.Keyframe;
import com.teamwizardry.librarianlib.features.animator.animations.KeyframeAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComponentModifiers extends GuiComponent {

	private final WorktableGui worktable;

	private boolean animationPlaying = false, refreshRequested = false;

	public ComponentModifiers(WorktableGui worktable) {
		super(384, 50, 80, 16 * 5);
		this.worktable = worktable;

		clipping.setClipToBounds(true);

		refresh();

		BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			if (refreshRequested && !animationPlaying) {
				refreshRequested = false;
				animationPlaying = true;
				set();
			}
		});
	}

	private static int factorial(int n) {
		if (n == 1) {
			return 1;
		}
		return n * factorial(n - 1);
	}

	public void refresh() {
		refreshRequested = true;
	}

	public void set() {
		//if (worktable.selectedModule == null) {
		//	setVisible(false);
		//	return;
		//} else setVisible(true);

		List<GuiComponent> children = new ArrayList<>(getChildren());
		float duration = 1 / 5f;
		final int childrenSize = children.size();
		for (int i = 0; i < childrenSize; i++) {
			GuiComponent bar = children.get(i);

			BasicAnimation<GuiComponent> animPlate = new BasicAnimation<>(bar, "pos.y");
			animPlate.setEasing(Easing.easeInBack);
			animPlate.setFrom(i * 16);
			animPlate.setTo(0);
			animPlate.setDuration(-((i + 1) * duration) / (duration * (2 - 1f / (childrenSize - i))) + duration);
			animPlate.setCompletion(bar::invalidate);

			add(animPlate);
		}

		ScheduledEventAnimation begin = new ScheduledEventAnimation(duration, () -> {

			TableModule selectedModule = worktable.selectedModule;
			if (selectedModule == null) {
				animationPlaying = false;
				return;
			}

			Module module = selectedModule.getModule();

			ModuleModifier[] applicableModifiers = module.applicableModifiers();
			if (applicableModifiers == null || applicableModifiers.length <= 0) {
				animationPlaying = false;
				return;
			}

			ModuleModifier[] modifiers = module.applicableModifiers();
			if (modifiers == null) {
				animationPlaying = false;
				return;
			}

			ScheduledEventAnimation animExpiry = new ScheduledEventAnimation(duration, () -> {
				animationPlaying = false;
			});
			add(animExpiry);

			for (int i = 0; i < modifiers.length; i++) {
				ModuleModifier modifier = modifiers[i];

				ComponentRect bar = new ComponentRect(0, i * 16, getSize().getXi(), 16);
				bar.getColor().setValue(new Color(0x80000000, true));

				TableModule tableModifier = new TableModule(worktable, modifier, false, false);
				bar.add(tableModifier);

				ComponentText text = new ComponentText(20, 4, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
				text.getText().setValue(TextFormatting.GREEN + modifier.getShortHandName());
				bar.add(text);

				bar.setVisible(true);
				BasicAnimation<ComponentRect> animPlate = new BasicAnimation<>(bar, "pos.y");
				animPlate.setEasing(Easing.easeOutBack);
				animPlate.setFrom(0);
				animPlate.setTo(i * 16);
				animPlate.setDuration(((modifiers.length - i) + 1) * ((modifiers.length - i) + 1) / duration);

				add(animPlate);

				bar.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
					List<String> txt = new ArrayList<>();

					if (worktable.animationPlaying) return txt;

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

					int j = worktable.selectedModule.hasData(Integer.class, modifier.getID()) ? worktable.selectedModule.getData(Integer.class, modifier.getID()) : 0;

					int status = -1;
					if (event.getButton() == EnumMouseButton.LEFT) {
						worktable.selectedModule.setData(Integer.class, modifier.getID(), ++j);
						status = 0;
					} else if (event.getButton() == EnumMouseButton.RIGHT) {
						if (worktable.selectedModule.hasData(Integer.class, modifier.getID())) {

							if (worktable.selectedModule.getData(Integer.class, modifier.getID()) > 0) {
								worktable.selectedModule.setData(Integer.class, modifier.getID(), --j);
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
					anim.setDuration(40);
					if (status == 0) {
						Vec2d rand = from.add(RandUtil.nextDouble(-10, 10), RandUtil.nextDouble(-10, 10));
						anim.setKeyframes(new Keyframe[]{
								new Keyframe(0, from, Easing.easeOutQuint),
								new Keyframe(0.4f, rand, Easing.easeOutQuint),
								new Keyframe(0.5f, rand, Easing.easeOutQuint),
								new Keyframe(1f, to, Easing.easeInOutQuint)
						});
					} else if (status == 1) {
						Vec2d rand = to.add(RandUtil.nextDouble(-10, 10), RandUtil.nextDouble(-10, 10));
						anim.setKeyframes(new Keyframe[]{
								new Keyframe(0, to, Easing.easeOutQuint),
								new Keyframe(0.4f, rand, Easing.easeOutQuint),
								new Keyframe(0.5f, rand, Easing.easeOutQuint),
								new Keyframe(1f, from, Easing.easeInOutQuint)
						});
					}

					anim.setCompletion(fakePlate::invalidate);

					worktable.getMainComponents().add(anim);
				});

				add(bar);
			}
		});

		add(begin);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
