package me.lordsaad.wizardry.gui.book.util;

import me.lordsaad.wizardry.gui.book.MainIndex;
import me.lordsaad.wizardry.gui.book.pages.GuiPageSubindex;
import me.lordsaad.wizardry.gui.book.pages.GuiPageText;
import me.lordsaad.wizardry.network.PacketHandler;
import me.lordsaad.wizardry.network.packets.PacketUpdateSavedPage;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the different types of pages and constructs pages based on their path
 *
 * @author piercecorcoran
 */
public class PageRegistry {

    public static final PageRegistry INSTANCE = new PageRegistry();
    private static Map<String, IPageGuiSupplier> map;

    private PageRegistry() {
        map = new HashMap<>();
//		register("error", (parent, node, path) -> { return new GuiPageText(parent, node, path); });
        register("text", GuiPageText::new);
        register("subindex", GuiPageSubindex::new);
    }

    public static void register(String name, IPageGuiSupplier supplier) {
        map.putIfAbsent(name, supplier);
    }

    public static GuiScreen construct(GuiScreen parent, String path, int pageNum) {

        PacketHandler.net().sendToServer(new PacketUpdateSavedPage(path, pageNum));

        if ("/".equals(path)) {
            return new MainIndex();
        }

        DataNode data = PageDataManager.getPageData(path);

        DataNode pagesList = data.get("pages");
        DataNode pageData = pagesList.get(pageNum);

        String type = pageData.get("type").asStringOr("error");
        if (map.containsKey(type)) {
            if (pageData.isMap()) {
                if (pagesList.get(pageNum + 1).exists()) {
                    pageData.asMap().put("hasNext", new DataNode("true"));
                }
                if (pagesList.get(pageNum + -1).exists()) {
                    pageData.asMap().put("hasPrev", new DataNode("true"));
                }
            }
            return map.get(type).create(parent, pageData, data, path, pageNum);
        }
        return null;
    }

    @FunctionalInterface
    public interface IPageGuiSupplier {
        GuiScreen create(GuiScreen parent, DataNode node, DataNode globalNode, String path, int page);
    }
}
