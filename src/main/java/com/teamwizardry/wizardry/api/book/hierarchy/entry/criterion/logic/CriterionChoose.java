package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.storage.loot.RandomValueRange;

import java.util.List;

/**
 * @author WireSegal
 * Created at 9:51 PM on 2/21/18.
 */
public class CriterionChoose implements ICriterion {

	private static RandomValueRange.Serializer rangeDecoder = new RandomValueRange.Serializer();

	private final RandomValueRange range;
	private final List<ICriterion> criteria = Lists.newArrayList();

	public CriterionChoose(JsonObject object) {
		JsonElement rangeElement = object.get("range");
		if (rangeElement == null)
			rangeElement = new JsonPrimitive(1);
		range = rangeDecoder.deserialize(rangeElement, null, null);
		JsonArray values = object.getAsJsonArray("values");
		for (JsonElement value : values) {
			ICriterion criterion = ICriterion.fromJson(value);
			if (criterion != null)
				criteria.add(criterion);
		}
	}

	@Override
	public boolean isUnlocked(EntityPlayer player, boolean grantedInCode) {
		int matched = 0;
		for (ICriterion criterion : criteria)
			if (criterion.isUnlocked(player, grantedInCode))
				matched++;
		return range.isInRange(matched);
	}

}
