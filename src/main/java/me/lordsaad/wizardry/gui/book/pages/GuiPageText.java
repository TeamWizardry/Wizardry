package me.lordsaad.wizardry.gui.book.pages;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import me.lordsaad.wizardry.gui.book.util.DataNode;

public class GuiPageText extends GuiPageCommon {
	
	private String text;
	
	public GuiPageText(GuiScreen parent, DataNode data, DataNode globalData, String path, int page) {
		super(parent, data, globalData, path, page);
		List<DataNode> list = data.get("text").asList();
		String str = "";
		for (DataNode node : list) {
			str += node.asStringOr("!! ERROR - text list element not a string !!") + "§r§0\n";
		}
		str.replaceAll("(?<!\\\\)&([0-9a-fA-FklmnorKLMNOR])", "§$1");
		this.text = str;
	}

	@Override
	public void drawPage(int mouseX, int mouseY, float partialTicks) {
		FontRenderer fr = mc.fontRendererObj;
		fr.drawSplitString(text, 0, 0, viewWidth, 0x000000);
	}
	
}
