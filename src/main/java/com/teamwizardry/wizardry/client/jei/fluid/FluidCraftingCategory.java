package com.teamwizardry.wizardry.client.jei.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.jei.WizardryJEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 4:50 PM on 1/13/18.
 */
@SideOnly(Side.CLIENT)
public class FluidCraftingCategory implements IRecipeCategory<FluidRecipeJEI> {

	private static final ResourceLocation RECIPE_BACKGROUND = new ResourceLocation("jei", "textures/gui/recipe_background.png");

	public IDrawable info = WizardryJEIPlugin.helpers.getGuiHelper()
			.createDrawable(RECIPE_BACKGROUND, 212, 39, 16, 16);
	public IDrawable background = WizardryJEIPlugin.helpers.getGuiHelper()
			.createDrawable(new ResourceLocation(Wizardry.MODID, "textures/gui/categories.png"), 0, 0, 76, 100);
	public IDrawable slots = WizardryJEIPlugin.helpers.getGuiHelper()
			.createDrawable(new ResourceLocation(Wizardry.MODID, "textures/gui/categories.png"), 0, 128, 54, 18);


	@Nonnull
	@Override
	public String getUid() {
		return Wizardry.MODID + ":fluid";
	}

	@Nonnull
	@Override
	public String getTitle() {
		return I18n.format("jei.recipe." + getUid());
	}

	@Nonnull
	@Override
	public String getModName() {
		return Wizardry.MODNAME;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		return background;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull FluidRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 0, 13);
		recipeLayout.getFluidStacks().init(0, true, 30, 14);

		recipeLayout.getItemStacks().init(1, true, 11, 46);
		recipeLayout.getItemStacks().init(2, true, 29, 46);
		recipeLayout.getItemStacks().init(3, true, 47, 46);
		recipeLayout.getItemStacks().init(4, true, 11, 64);
		recipeLayout.getItemStacks().init(5, true, 29, 64);
		recipeLayout.getItemStacks().init(6, true, 47, 64);
		recipeLayout.getItemStacks().init(7, true, 11, 82);
		recipeLayout.getItemStacks().init(8, true, 29, 82);
		recipeLayout.getItemStacks().init(9, true, 47, 82);


		if (recipeWrapper.isFluidOutput())
			recipeLayout.getFluidStacks().init(1, false, 59, 14);
		else
			recipeLayout.getItemStacks().init(10, false, 58, 13);

		recipeLayout.getItemStacks().set(ingredients);
		recipeLayout.getFluidStacks().set(ingredients);
	}
}
