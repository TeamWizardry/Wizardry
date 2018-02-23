package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion;

import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game.CriterionAdvancement;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game.CriterionCode;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game.CriterionEntry;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic.CriterionAnd;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic.CriterionChoose;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic.CriterionNot;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.logic.CriterionOr;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;

public class CriterionTypes {

	private static final HashMap<String, Function<JsonObject, ICriterion>> criterionTypes = new HashMap<>();

	static {
		registerCriterion("and", CriterionAnd::new);
		registerCriterion("or", CriterionOr::new);
		registerCriterion("not", CriterionNot::new);
		registerCriterion("choose", CriterionChoose::new);
		registerCriterion("xor", CriterionChoose::new);
		registerCriterion("entry", CriterionEntry::new);
		registerCriterion("advancement", CriterionAdvancement::new);
		registerCriterion("manual", (json) -> new CriterionCode());
	}

	public static void registerCriterion(@NotNull String name, @NotNull Function<JsonObject, ICriterion> provider) {
		registerCriterion(new ResourceLocation(name), provider);
	}

	public static void registerCriterion(@NotNull ResourceLocation name, @NotNull Function<JsonObject, ICriterion> provider) {
		String key = name.toString();
		if (!criterionTypes.containsKey(key))
			criterionTypes.put(key, provider);
	}

	@Nullable
	public static Function<JsonObject, ICriterion> getCriterion(@NotNull String type) {
		return getCriterion(new ResourceLocation(type.toLowerCase(Locale.ROOT)));
	}

	@Nullable
	public static Function<JsonObject, ICriterion> getCriterion(@NotNull ResourceLocation type) {
		return criterionTypes.getOrDefault(type.toString(), null);
	}
}
