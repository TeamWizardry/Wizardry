package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author WireSegal
 * Created at 9:45 PM on 2/21/18.
 */
public class CriterionNot implements ICriterion {

	private final ICriterion criterion;

	public CriterionNot(JsonObject object) {
		JsonElement superCriterion = object.get("criterion");
		if (superCriterion.isJsonArray()) {
			JsonObject obj = new JsonObject();
			obj.addProperty("type", "or");
			obj.add("values", superCriterion);

			superCriterion = obj;
		}

		criterion = ICriterion.fromJson(superCriterion);
	}

	@Override
	public boolean isUnlocked(EntityPlayer player, boolean grantedInCode) {
		return !criterion.isUnlocked(player, grantedInCode);
	}
}
