package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;

import java.util.function.Function;

/**
 * @author WireSegal
 * Created at 9:36 PM on 2/21/18.
 */
public interface ICriterion {
	static ICriterion fromJson(JsonElement element) {
		try {
			JsonObject obj = null;
			Function<JsonObject, ICriterion> provider = null;
			if (element.isJsonPrimitive()) {
				provider = CriterionTypes.getCriterion("entry");
				obj = new JsonObject();
				obj.addProperty("type", "entry");
				obj.add("name", element);
			} else if (element.isJsonArray()) {
				provider = CriterionTypes.getCriterion("and");
				obj = new JsonObject();
				obj.addProperty("type", "and");
				obj.add("values", element);
			} else if (element.isJsonObject()) {
				obj = element.getAsJsonObject();
				provider = CriterionTypes.getCriterion(obj.getAsJsonPrimitive("type").getAsString());
			}

			if (obj == null || provider == null)
				return null;

			return provider.apply(obj);
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}

	boolean isUnlocked(EntityPlayer player, boolean grantedInCode);
}
