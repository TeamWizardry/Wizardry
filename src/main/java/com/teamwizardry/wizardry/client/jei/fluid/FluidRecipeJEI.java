package com.teamwizardry.wizardry.client.jei.fluid;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.client.jei.WizardryJEIPlugin;
import com.teamwizardry.wizardry.crafting.mana.FluidRecipeLoader;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static com.teamwizardry.wizardry.client.jei.WizardryJEIPlugin.manaCategory;

/**
 * @author WireSegal
 * Created at 4:51 PM on 1/13/18.
 */
@SideOnly(Side.CLIENT)
public class FluidRecipeJEI implements IRecipeWrapper {

	private final FluidRecipeLoader.FluidCrafter builder;

	public FluidRecipeJEI(FluidRecipeLoader.FluidCrafter builder) {
		this.builder = builder;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		List<List<ItemStack>> stacks = Lists.newArrayList();
		stacks.add(Lists.newArrayList(builder.getMainInput().getMatchingStacks()));
		for (Ingredient ingredient : builder.getInputs())
			stacks.add(Lists.newArrayList(ingredient.getMatchingStacks()));

		if (!isFluidOutput())
			for (List<ItemStack> stackList : stacks)
				stackList.removeIf(Ingredient.fromStacks(builder.getOutput())::apply);

		ingredients.setInputLists(ItemStack.class, stacks);
		ingredients.setInput(FluidStack.class, new FluidStack(builder.getFluid(), 1000));

		if (isFluidOutput())
			ingredients.setOutput(FluidStack.class, builder.getFluidOutput());
		else
			ingredients.setOutput(ItemStack.class, builder.getOutput());
	}

	public boolean isFluidOutput() {
		return builder.getFluidOutput() != null;
	}

	@Nonnull
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		IDrawable info = WizardryJEIPlugin.manaCategory.info;
		if (mouseX >= 64 && mouseX <= 64 + info.getWidth() / 2 &&
				mouseY >= 3 && mouseY <= 3 + info.getHeight() / 2) {
			List<String> output = Lists.newArrayList();
			if (builder.isBlock())
				TooltipHelper.addToTooltip(output, "jei.recipe.block." + manaCategory.getUid());
			if (builder.doesConsume() && (!builder.isBlock() || builder.getRequired() > 1))
				TooltipHelper.addToTooltip(output, "jei.recipe.consumes." + manaCategory.getUid());
			if (builder.getRequired() > 1)
				TooltipHelper.addToTooltip(output, "jei.recipe.requires." + manaCategory.getUid(), builder.getRequired());
			return output;
		}

		return Collections.emptyList();
	}

	public int rows() {
		return (builder.getInputs().size() + 2) / 3;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		FluidCraftingCategory category = WizardryJEIPlugin.manaCategory;

		if (builder.isBlock() || builder.doesConsume() || builder.getRequired() > 1) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5, 0.5, 1.0);
			category.info.draw(minecraft, 64 * 2, 3 * 2);
			GlStateManager.popMatrix();
		}
		for (int i = 0; i < rows(); i++)
			category.slots.draw(minecraft, 11, 46 + 18 * i);
	}
}
