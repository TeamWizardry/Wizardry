package me.lordsaad.wizardry.gui.book.util;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class PageDataManager {

    public static String getLang() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    public static DataNode getPageData(String pagePath) {
        return getData("documentation/%LANG%/" + pagePath);
    }

    public static DataNode getData(String resourcePath) {
        IResource resource;
        DataNode root = DataNode.NULL;
        try {
            resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(Wizardry.MODID, PathUtils.resolve(resourcePath.replace("%LANG%", getLang()) + ".json").substring(1)));
            root = DataParser.parse(resource.getInputStream());
        } catch (IOException e) {
            //TODO: add logger
            try {
                resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(Wizardry.MODID, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1)));
                root = DataParser.parse(resource.getInputStream());
            } catch (IOException e2) {
                //TODO: add logger
                e2.printStackTrace();
            }

        }
        return root;
    }

}
