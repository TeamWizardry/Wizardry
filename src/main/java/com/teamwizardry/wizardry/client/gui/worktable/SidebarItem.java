package com.teamwizardry.wizardry.client.gui.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.TextFormatting;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.util.misc.Utils;
import com.teamwizardry.librarianlib.math.Vec2;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;

public class SidebarItem {

	public ComponentSprite result;
	
	protected ModuleList.IModuleConstructor constructor;
	protected Module module;
	protected GuiComponent<?> paper;
	
	public SidebarItem(int posX, int posY, ModuleList.IModuleConstructor constructor, GuiComponent<?> paper) {
		this.constructor = constructor;
		this.module = constructor.construct();
		this.paper = paper;		
		result = new ComponentSprite(module.getType().backgroundSprite, posX, posY, 12, 12);
		result.mouseDown.add( (c, pos, button) -> {
			if(c.mouseOverThisFrame) {
				PaperModule m = new PaperModule(pos.xi, pos.yi, constructor, paper);
				m.drag.mouseDown = true;
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
		
		ComponentSprite glow = new ComponentSprite(WorktableGui.MODULE_DEFAULT_GLOW, 0, 0, 12, 12);
		glow.setVisible(false);
		result.add(glow);
		result.mouseIn.add( (c, pos) -> {
			glow.setVisible(true);
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			glow.setVisible(false);
			return false;
		});
		
		ComponentSprite icon = new ComponentSprite(this.module.getStaticIcon(), 2, 2, 8, 8);
		result.add(icon);
		result.mouseIn.add( (c, pos) -> {
			icon.setSprite(this.module.getAnimatedIcon());
			return false;
		});
		result.mouseOut.add( (c, pos) -> {
			icon.setSprite(this.module.getStaticIcon());
			return false;
		});
		
		result.addTag("sidebarItem");
	}
	
	
	
}
