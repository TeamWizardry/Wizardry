package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.provider.PageProviderRegistry;

import java.util.function.Function;

public class PageInstanceFactory {

	public static Page getPage(JsonElement element) {
		try {
			JsonObject obj = null;
			Function<JsonObject, Page> provider = null;
			if (element.isJsonPrimitive()) {
				provider = PageProviderRegistry.getPageProvider("text");
				obj = new JsonObject();
				obj.addProperty("type", "text");
				obj.addProperty("value", element.getAsString());
			} else if (element.isJsonObject()) {
				obj = element.getAsJsonObject();
				if (obj.has("type") && obj.get("type").isJsonPrimitive())
					provider = PageProviderRegistry.getPageProvider(obj.getAsJsonPrimitive("type").getAsString());
			}

			if (obj == null || provider == null)
				return null;

			return provider.apply(obj);
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}
}
