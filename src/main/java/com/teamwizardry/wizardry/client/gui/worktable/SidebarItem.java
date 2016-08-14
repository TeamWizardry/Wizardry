package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.util.Utils;
import com.teamwizardry.wizardry.api.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class SidebarItem extends ModuleTemplate {
	
	public SidebarItem(int posX, int posY, Module constructor, GuiComponent<?> paper) {
		super(posX, posY, constructor, paper);
		
		getResult().getMouseDown().add( (c, pos, button) -> {
			if(button == EnumMouseButton.LEFT && c.getMouseOverThisFrame()) {
				ModuleTemplatePaper m = new ModuleTemplatePaper(pos.getXi(), pos.getYi(), constructor, paper);
				m.drag.setMouseDown(true);
				m.drag.setClickPos(pos.sub(6, 6));
				paper.add(m.get());
				return true;
			}
			return false;
		});
		getResult().getPostDraw().add( (c, pos, partialTicks) -> {
			if(c.getMouseOverThisFrame()) {
				List<String> txt = new ArrayList<>();
	            txt.add(TextFormatting.GOLD + module.getDisplayName());
	            txt.addAll(Utils.INSTANCE.padString(module.getDescription(), 30));
	            c.setTooltip(txt);
			}
		});
		
		getResult().addTag("sidebarItem");
	}
	
	
	
}
