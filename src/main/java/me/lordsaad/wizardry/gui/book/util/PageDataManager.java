package me.lordsaad.wizardry.gui.book.util;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import me.lordsaad.wizardry.Wizardry;

public class PageDataManager {
	
	public static String getLang() {
		return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}
	
	public static DataNode getPageData(String pagePath) {
		return getData("documentation/" + getLang() + "/" + pagePath);
	}
	
	public static DataNode getData(String resourcePath) {
		IResource resource;
		DataNode root = DataNode.NULL;
		try {
			resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(Wizardry.MODID, PathUtils.resolve( resourcePath + ".json").substring(1)));
			root = DataParser.parse(resource.getInputStream());
		} catch (IOException e) {
			//TODO: add logger
			e.printStackTrace();
		}
		return root;
	}
	
}
