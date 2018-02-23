package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game;

import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.Entry;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
public class CriterionEntry implements ICriterion {

	private final ResourceLocation entry;

	public CriterionEntry(JsonObject object) {
		entry = new ResourceLocation(object.getAsJsonPrimitive("name").getAsString());
	}

	@Override
	public boolean isUnlocked(EntityPlayer player, boolean grantedInCode) {
		Entry entryObj = Entry.ENTRIES.get(entry);
		return entryObj != null && entryObj.isUnlocked(player);
	}
}
