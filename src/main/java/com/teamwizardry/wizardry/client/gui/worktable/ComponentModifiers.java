package com.teamwizardry.wizardry.client.gui.worktable;

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
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceModifier;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComponentModifiers extends GuiComponent {

	public static final int PIXELS_PER_BAR = 16; // units: pixels
	public static final float SLIDE_IN_DURATION = 18; // units: ticks
	public static final float SPACER_DURATION = 5; // units: ticks
	public static final float SLIDE_OUT_DURATION = 3; // units: ticks (/ modifier)
	private final WorktableGui worktable;
	private boolean animationPlaying = false, refreshRequested = false;

	public ComponentModifiers(WorktableGui worktable) {
		super(384, 127, 80, 81);
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

	public void refresh() {
		refreshRequested = true;
	}

	public void set() {
		List<GuiComponent> children = new ArrayList<>(getChildren());

		int childrenSize = children.size(); // units: none (modifier)

		float slideInDist = childrenSize * PIXELS_PER_BAR; // units: pixels

		for (int i = 0; i < childrenSize; i++) {
			int lengthToTravel = (i + 1) * PIXELS_PER_BAR; // units: pixels
			float slideDuration = SLIDE_IN_DURATION * lengthToTravel / slideInDist; // units: ticks
			float waitDuration = SLIDE_IN_DURATION - slideDuration; // units: ticks

			GuiComponent bar = children.get(i);

			BasicAnimation<GuiComponent> animPlate = new BasicAnimation<>(bar, "pos.y");
			animPlate.setEasing(Easing.easeInBack); // Use Easing.easeInOutBack for the sexiest effect. Important: do it for BOTH!
			animPlate.setFrom(lengthToTravel - PIXELS_PER_BAR); // units: pixels
			animPlate.setTo(-PIXELS_PER_BAR); // units: pixels
			animPlate.setDuration(slideDuration); // units: ticks
			animPlate.setCompletion(bar::invalidate);

			add(new ScheduledEventAnimation(waitDuration, () -> add(animPlate)));
		}


		Runnable begin = () -> {

			TableModule selectedModule = worktable.selectedModule;
			if (selectedModule == null) {
				animationPlaying = false;
				return;
			}

			ModuleInstance module = selectedModule.getModule();

			ModuleInstanceModifier[] applicableModifiers = module.applicableModifiers();
			if (applicableModifiers == null || applicableModifiers.length <= 0) {
				animationPlaying = false;
				return;
			}

			ModuleInstanceModifier[] modifiers = module.applicableModifiers();
			if (modifiers == null) {
				animationPlaying = false;
				return;
			}


			int modifiersSize = modifiers.length; // units: none
			float slideOutDist = modifiersSize * PIXELS_PER_BAR; // units: pixels
			float outDuration = SLIDE_OUT_DURATION * modifiersSize; // units: ticks

			add(new ScheduledEventAnimation(SLIDE_IN_DURATION,
					() -> animationPlaying = false));

			for (int i = 0; i < modifiers.length; i++) {
				int lengthToTravel = (i + 1) * PIXELS_PER_BAR; // units: pixels
				float slideDuration = outDuration * lengthToTravel / slideOutDist; // units: ticks

				ModuleInstanceModifier modifier = modifiers[i];

				ComponentRect bar = new ComponentRect(0, 0, getSize().getXi(), PIXELS_PER_BAR);
				bar.getColor().setValue(new Color(0x80000000, true));

				TableModule tableModifier = new TableModule(worktable, modifier, false, true);
				tableModifier.setEnableTooltip(true);
				tableModifier.getTransform().setTranslateZ(80);
				bar.add(tableModifier);

				ComponentText text = new ComponentText(20, 4, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
				text.getText().setValue(TextFormatting.GREEN + modifier.getShortHandName());
				bar.add(text);

				bar.setVisible(true);
				BasicAnimation<ComponentRect> animPlate = new BasicAnimation<>(bar, "pos.y");
				animPlate.setEasing(Easing.easeOutBack); // Use Easing.easeInOutBack for the sexiest effect. Important: do it for BOTH!
				animPlate.setFrom(-PIXELS_PER_BAR); // units: pixels
				animPlate.setTo(lengthToTravel - PIXELS_PER_BAR); // units: pixels
				animPlate.setDuration(slideDuration); // units: ticks
				animPlate.setCompletion(() -> Minecraft.getMinecraft().player.playSound(ModSounds.WHOOSH, 1f, 1f));
				add(animPlate);

				bar.render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
					List<String> txt = new ArrayList<>();

					if (worktable.animationPlaying || tableModifier.getMouseOver()) return txt;

					txt.add(TextFormatting.GOLD + modifier.getReadableName());
					if (GuiScreen.isShiftKeyDown())
						txt.add(TextFormatting.GRAY + modifier.getDescription());
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

					int j = worktable.selectedModule.hasData(Integer.class, modifier.getNBTKey()) ? worktable.selectedModule.getData(Integer.class, modifier.getNBTKey()) : 0;

					int status = -1;
					if (event.getButton() == EnumMouseButton.LEFT) {
						AttributeRange cap = null;

						for (AttributeRegistry.Attribute modifiersAssigned : worktable.selectedModule.getModule().getAttributeRanges().keySet()) {
							for (AttributeModifier attribute : modifier.getAttributeModifiers()) {
								if (modifiersAssigned.getNbtName().equals(attribute.getAttribute().getNbtName())) {
									cap = worktable.selectedModule.getModule().getAttributeRanges().get(modifiersAssigned);
									break;
								}
							}
						}
						if (cap != null && cap.max <= j) {
							Minecraft.getMinecraft().player.playSound(ModSounds.SPELL_FAIL, 1f, 1f);
							return;
						}

						Minecraft.getMinecraft().player.playSound(ModSounds.POP, 1f, 1f);
						worktable.selectedModule.setData(Integer.class, modifier.getNBTKey(), ++j);
						status = 0;
						worktable.setToastMessage("", Color.GREEN);
						worktable.syncToServer();
					} else if (event.getButton() == EnumMouseButton.RIGHT) {
						if (worktable.selectedModule.hasData(Integer.class, modifier.getNBTKey())) {

							if (j > 0) {
								Minecraft.getMinecraft().player.playSound(ModSounds.ZOOM, 1f, 1f);
								worktable.selectedModule.setData(Integer.class, modifier.getNBTKey(), --j);

								if (j <= 0) {
									Minecraft.getMinecraft().player.playSound(ModSounds.SPELL_FAIL, 1f, 1f);
									worktable.selectedModule.removeData(Integer.class, modifier.getNBTKey());
								}
								status = 1;
								worktable.syncToServer();
							}
							worktable.setToastMessage("", Color.GREEN);
						} else Minecraft.getMinecraft().player.playSound(ModSounds.SPELL_FAIL, 1f, 1f);
					}

					if (status == -1) return;

					TableModule fakePlate = new TableModule(worktable, modifier, false, true);
					fakePlate.getTransform().setTranslateZ(80);
					worktable.getMainComponents().add(fakePlate);

					Vec2d from = tableModifier.thisPosToOtherContext(worktable.getMainComponents());
					Vec2d to = worktable.selectedModule.thisPosToOtherContext(worktable.getMainComponents())
							.add(worktable.selectedModule.getSize().sub(tableModifier.getSize()).mul(0.5f));

					KeyframeAnimation<TableModule> anim = new KeyframeAnimation<>(fakePlate, "pos");
					anim.setDuration(20);
					if (status == 0) {
						Vec2d rand = from.add(RandUtil.nextDouble(-10, 10), RandUtil.nextDouble(-10, 10));
						anim.setKeyframes(new Keyframe[]{
								new Keyframe(0, from, Easing.easeOutQuint),
								new Keyframe(0.3f, rand, Easing.easeOutQuint),
								new Keyframe(0.35f, rand, Easing.easeOutQuint),
								new Keyframe(1f, to, Easing.easeInOutQuint)
						});
					} else {
						Vec2d rand = to.add(RandUtil.nextDouble(-10, 10), RandUtil.nextDouble(-10, 10));
						anim.setKeyframes(new Keyframe[]{
								new Keyframe(0, to, Easing.easeOutQuint),
								new Keyframe(0.3f, rand, Easing.easeOutQuint),
								new Keyframe(0.35f, rand, Easing.easeOutQuint),
								new Keyframe(1f, from, Easing.easeInOutQuint)
						});
					}

					anim.setCompletion(fakePlate::invalidate);

					worktable.getMainComponents().add(anim);

					worktable.paper.BUS.fire(new TableModule.ModuleUpdateEvent());
				});

				add(bar);
			}
		};

		if (childrenSize > 0)
			add(new ScheduledEventAnimation(SLIDE_IN_DURATION + SPACER_DURATION, begin));
		else
			begin.run();
	}

	@Override
	public void drawComponent(@Nonnull Vec2d mousePos, float partialTicks) {

	}
}
