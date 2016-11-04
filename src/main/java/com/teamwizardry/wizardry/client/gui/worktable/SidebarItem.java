package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.wizardry.api.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class SidebarItem extends ModuleTemplate {

	public SidebarItem(int posX, int posY, Module constructor, GuiComponent<?> paper) {
		super(posX, posY, constructor, paper);

		getResult().BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
			if ((event.getButton() == EnumMouseButton.LEFT) && event.getComponent().getMouseOver()) {
				ModuleTemplatePaper m = new ModuleTemplatePaper(event.getMousePos().getXi(), event.getMousePos().getYi(), constructor, paper);
				m.drag.setMouseDown(event.getButton());
				m.drag.setClickPos(event.getMousePos().sub(6, 6));
				paper.add(m.get());
				event.cancel();
			}
		});
		getResult().BUS.hook(GuiComponent.PostDrawEvent.class, (event) -> {
			if (event.getComponent().getMouseOver()) {
				List<String> txt = new ArrayList<>();
				txt.add(TextFormatting.GOLD + module.getDisplayName());
//	            txt.addAll(Utils.INSTANCE.padString(module.getDescription(), 30));
				event.getComponent().setTooltip(txt);
			}
		});

		getResult().addTag("sidebarItem");
	}


}
