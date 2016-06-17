package me.lordsaad.wizardry.gui.book.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;

import me.lordsaad.wizardry.gui.book.pages.GuiPageText;

public class PageRegistry {

	private static Map<String, IPageGuiSupplier> map = new HashMap<>();

	public static final PageRegistry INSTANCE = new PageRegistry();
	
	private PageRegistry() {
//		register("error", (parent, node, path) -> { return new GuiPageText(parent, node, path); });
		register("text", (parent, node, path, page) -> { return new GuiPageText(parent, node, path, page); });
	}
	
	public static void register(String name, IPageGuiSupplier supplier) {
		map.put(name, supplier);
	}
	
	public static GuiScreen construct(GuiScreen parent, String path, int pageNum) {
		
		DataNode data = PageDataManager.getPageData(path);
		DataNode pageData = data.get(pageNum);
		String type = pageData.get("type").asStringOr("error");
		if(map.containsKey(type)) {
			if(pageData.isMap()) {
				if(data.get(pageNum+1).exists()) {
					pageData.asMap().put("hasNext", new DataNode("true"));
				}
				if(data.get(pageNum+-1).exists()) {
					pageData.asMap().put("hasPrev", new DataNode("true"));
				}
			}
			return map.get(type).create(parent, pageData, path, pageNum);
		}
		return null;
	}
	
	@FunctionalInterface
	public static interface IPageGuiSupplier {
		GuiScreen create(GuiScreen parent, DataNode node, String path, int page);
	}
}
