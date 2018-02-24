package com.teamwizardry.wizardry.api.book.hierarchy.book;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.book.hierarchy.IBookElement;
import com.teamwizardry.wizardry.api.book.hierarchy.category.Category;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.api.book.hierarchy.page.Page;
import com.teamwizardry.wizardry.client.gui.book.ComponentMainIndex;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.teamwizardry.librarianlib.features.helpers.CommonUtilMethods.getCurrentModId;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Book implements IBookElement {

    public static boolean hasEverReloaded = false;
    private static List<Book> allBooks = Lists.newArrayList();

    static {
        ClientRunnable.registerReloadHandler(new ClientRunnable() {
            @Override
            @SideOnly(Side.CLIENT)
            public void runIfClient() {
                hasEverReloaded = true;
                for (Book book : allBooks)
                    book.reload();
            }
        });
    }

    public final ResourceLocation location;
    public List<Category> categories;
    public String headerKey;
    public String subtitleKey;
    public Color bookColor;
    public Color highlightColor;

    public Book(String name) {
        this(new ResourceLocation(getCurrentModId(), name));
    }

    public Book(ResourceLocation location) {
        this.location = location;

        bookColor = Color.WHITE;
        highlightColor = Color.WHITE;
        headerKey = "";
        subtitleKey = "";
        categories = Lists.newArrayList();
        allBooks.add(this);

        if (hasEverReloaded)
            reload();
    }

    public static Color colorFromJson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber())
                return new Color(primitive.getAsInt());
            else
                return new Color(Integer.decode(element.getAsString()));
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            return new Color(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
        }
        return Color.WHITE;
    }

    public static JsonElement getJsonFromLink(String location) {
        return getJsonFromLink(new ResourceLocation(location));
    }

    public static JsonElement getJsonFromLink(ResourceLocation location) {
        InputStream stream = LibrarianLib.PROXY.getResource(location.getResourceDomain(), "documentation/" + location.getResourcePath() + ".json");
        if (stream == null) return null;

        InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        return new JsonParser().parse(reader);
    }

    public void reload() {
        try {
            JsonElement jsonElement = getJsonFromLink(location);
            if (jsonElement == null || !jsonElement.isJsonObject())
                return;
            JsonObject json = jsonElement.getAsJsonObject();
            bookColor = colorFromJson(json.get("color"));
            highlightColor = colorFromJson(json.get("highlight"));
            headerKey = json.getAsJsonPrimitive("title").getAsString();
            subtitleKey = json.getAsJsonPrimitive("subtitle").getAsString();
            JsonArray allCategories = json.getAsJsonArray("categories");
            categories = Lists.newArrayList();
            for (JsonElement categoryJson : allCategories) {
                Category category = new Category(this, categoryJson.getAsJsonObject());
                if (category.isValid)
                    categories.add(category);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public Map<Entry, String> getContentCache() {
        Map<Entry, String> searchCache = Maps.newHashMap();
        for (Category category : categories) {
            for (Entry entry : category.entries) {
                StringBuilder searchBuilder = new StringBuilder();

	            for (Page page : entry.pages) {
                    Collection<String> searchable = page.getSearchableKeys();
                    if (searchable != null) for (String key : searchable)
                        searchBuilder.append(I18n.format(key)).append(' ');
                    searchable = page.getSearchableStrings();
                    if (searchable != null) for (String value : searchable)
                        searchBuilder.append(value).append(' ');
                }
                searchCache.put(entry, searchBuilder.toString());
            }
        }
        return searchCache;
    }

    @Override
    public @Nullable IBookElement getBookParent() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiComponent createComponent(GuiBook book) {
        return new ComponentMainIndex(book);
    }
}
