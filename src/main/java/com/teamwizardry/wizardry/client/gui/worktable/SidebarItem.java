package com.teamwizardry.wizardry.client.gui.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.TextFormatting;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.util.misc.Utils;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class SidebarItem extends ModuleComponent {
	
	public SidebarItem(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		super(posX, posY, constructor, paper);
		
		result.mouseDown.add( (c, pos, button) -> {
			if(button == EnumMouseButton.LEFT && c.mouseOverThisFrame) {
				PaperModule m = new PaperModule(pos.xi, pos.yi, constructor, paper);
				m.drag.mouseDown = true;
				m.drag.clickPos = pos.sub(6, 6);
				paper.add(m.result);
				return true;
			}
			return false;
		});
		result.postDraw.add( (c, pos, partialTicks) -> {
			if(c.mouseOverThisFrame) {
				List<String> txt = new ArrayList<>();
	            txt.add(TextFormatting.GOLD + module.getDisplayName());
	            txt.addAll(Utils.padString(module.getDescription(), 30));
	            c.setTooltip(txt);
			}
		});
		
		result.addTag("sidebarItem");
	}
	
	
	
}
