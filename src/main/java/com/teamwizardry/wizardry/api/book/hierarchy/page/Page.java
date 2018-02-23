package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.api.book.provider.PageTypes;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public interface Page {

	static Page fromJson(Entry entry, JsonElement element) {
		try {
			JsonObject obj = null;
			BiFunction<Entry, JsonObject, Page> provider = null;
			if (element.isJsonPrimitive()) {
				provider = PageTypes.getPageProvider("text");
				obj = new JsonObject();
				obj.addProperty("type", "text");
				obj.add("value", element);
			} else if (element.isJsonObject()) {
				obj = element.getAsJsonObject();
				provider = PageTypes.getPageProvider(obj.getAsJsonPrimitive("type").getAsString());
			}

			if (obj == null || provider == null)
				return null;

			return provider.apply(entry, obj);
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}

	@NotNull
	Entry getEntry();

	@Nullable
	default Collection<String> getSearchableStrings() {
		return null;
	}

	@Nullable
	default Collection<String> getSearchableKeys() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	List<GuiComponent> createBookComponents(GuiBook book, Vec2d size);
}
