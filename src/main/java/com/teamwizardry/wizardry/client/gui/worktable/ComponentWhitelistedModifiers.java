package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.util.CubicBezier;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ComponentWhitelistedModifiers extends GuiComponent<ComponentWhitelistedModifiers> {

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

		HashSet<GuiComponent<?>> temp = new HashSet<>(list.getChildren());
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

			bar.getTooltip().func((Function<GuiComponent<ComponentRect>, java.util.List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				for (GuiComponent<?> comp : worktable.paperComponents.keySet()) if (comp.hasTag("dragging")) return txt;

				txt.add(TextFormatting.GOLD + module.getReadableName());
				if (GuiScreen.isShiftKeyDown())
					txt.add(TextFormatting.GRAY + module.getDescription());
				else txt.add(TextFormatting.GRAY + "<Sneak for info>");
				return txt;
			});

			// TODO animate here
			bar.BUS.hook(GuiComponent.MouseClickEvent.class, (event) -> {
				if (!event.getComponent().getMouseOver()) return;

				int i = worktable.selectedcomponent.hasData(Integer.class, modifier.getID()) ? worktable.selectedcomponent.getData(Integer.class, modifier.getID()) : 0;

				if (event.getButton() == EnumMouseButton.LEFT)
					worktable.selectedcomponent.setData(Integer.class, modifier.getID(), ++i);
				else if (event.getButton() == EnumMouseButton.RIGHT)
					worktable.selectedcomponent.setData(Integer.class, modifier.getID(), --i);

				final long[] animStart = {System.currentTimeMillis()};
				int maxTime = 1;

				Vec2d r = bar.posRelativeTo(bar.getPos(), worktable.getMainComponents());
				ComponentSprite fakePlate = new ComponentSprite(TableModule.plate, r.getXi(), r.getYi(), 16, 16);
				worktable.getMainComponents().add(fakePlate);

				ComponentSprite fakeIconComp = new ComponentSprite(icon, 2, 2, 12, 12);
				fakePlate.add(fakeIconComp);

				fakePlate.BUS.hook(GuiComponent.PostDrawEvent.class, (event1) -> {
					double time = (System.currentTimeMillis() - animStart[0]) / 1000.0;

					if (time <= maxTime) {
						float progress = (float) time / (float) maxTime;
						float t = new CubicBezier(0.17f, 0.67f, 0.38f, 0.99f).eval(progress);

						Vec2d to = worktable.selectedcomponent.posRelativeTo(worktable.selectedcomponent.getPos(), worktable.getMainComponents());
						Vec2d from = event.getComponent().posRelativeTo(event.getComponent().getPos(), worktable.getMainComponents());
						Vec2d diff = from.sub(to);
						Vec2d progDist = diff.mul(t);
						event1.getComponent().setPos(from.add(progDist));
					} else {
						event1.getComponent().invalidate();
					}
				});
			});

			list.add(bar);
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
