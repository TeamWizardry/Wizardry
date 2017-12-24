package com.teamwizardry.wizardry.crafting.jei;

import com.teamwizardry.wizardry.Wizardry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

public class JEIManaCategory implements IRecipeCategory {

	private static final IDrawable background = JEIPlugin.jeiHelpers.getGuiHelper().createBlankDrawable(300, 300);

	@NotNull
	@Override
	public String getUid() {
		return Wizardry.MODID + ".mana_crafting";
	}

	@NotNull
	@Override
	public String getTitle() {
		return I18n.format("jei." + Wizardry.MODID + ".category.mana_crafting");
	}

	@NotNull
	@Override
	public String getModName() {
		return Wizardry.MODNAME;
	}

	@NotNull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {

	}


}
