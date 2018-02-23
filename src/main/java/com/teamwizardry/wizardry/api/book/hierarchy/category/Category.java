package com.teamwizardry.wizardry.api.book.hierarchy.category;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.wizardry.api.book.hierarchy.IBookElement;
import com.teamwizardry.wizardry.api.book.hierarchy.book.Book;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.client.gui.book.ComponentCategoryPage;
import com.teamwizardry.wizardry.client.gui.book.ComponentEntryPage;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * @author WireSegal
 * Created at 10:19 PM on 2/17/18.
 */
public class Category implements IBookElement {
    public final Book book;

    public final List<Entry> entries;
    public final String titleKey;
    public final String descKey;
    public final JsonElement icon;
    public final Color color;

    public boolean isValid = false;

    public Category(Book book, JsonObject json) {
        this.book = book;
        String title = "";
        String desc = "";
        JsonElement icon = new JsonObject();
        List<Entry> entries = Lists.newArrayList();
        Color color = book.highlightColor;

        try {
            title = json.getAsJsonPrimitive("title").getAsString();
            desc = json.getAsJsonPrimitive("description").getAsString();
            icon = json.get("icon");
            if (json.has("color"))
                color = Book.colorFromJson(json.get("color"));
            JsonArray allEntries = json.getAsJsonArray("entries");
            for (JsonElement entryJson : allEntries) {
                JsonElement parsable = Book.getJsonFromLink(entryJson.getAsString());
                Entry entry = new Entry(this, entryJson.getAsString(), parsable.getAsJsonObject());
                if (entry.isValid)
                    entries.add(entry);
            }
            if (!entries.isEmpty())
                isValid = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.titleKey = title;
        this.descKey = desc;
        this.icon = icon;
        this.entries = entries;
        this.color = color;
    }

    public boolean anyUnlocked(EntityPlayer player) {
        for (Entry entry : entries)
            if (entry.isUnlocked(player))
                return true;
        return false;
    }

    public boolean isSingleEntry() {
        return entries.size() == 1;
    }

    @Override
    public @Nullable IBookElement getBookParent() {
        return book;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiComponent createComponent(GuiBook book) {
        if (isSingleEntry())
            return new ComponentEntryPage(book, entries.get(0));
        return new ComponentCategoryPage(book, this);
    }
}
