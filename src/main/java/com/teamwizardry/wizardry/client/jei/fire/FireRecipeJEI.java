package com.teamwizardry.wizardry.client.jei.fire;

import com.google.common.collect.Lists;
import com.teamwizardry.wizardry.client.jei.WizardryJEIPlugin;
import com.teamwizardry.wizardry.crafting.burnable.FireRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author WireSegal
 * Created at 4:51 PM on 1/13/18.
 */
@SideOnly(Side.CLIENT)
public class FireRecipeJEI implements IRecipeWrapper {
	private final Ingredient input;
	private final FireRecipe recipe;

	public FireRecipeJEI(Ingredient input, FireRecipe recipe) {
		this.input = input;
		this.recipe = recipe;
	}

	public FireRecipeJEI(Map.Entry<Ingredient, FireRecipe> entry) {
		this(entry.getKey(), entry.getValue());
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		List<ItemStack> stacks = Lists.newArrayList();
		stacks.addAll(Arrays.asList(input.getMatchingStacks()));
		List<List<ItemStack>> lists = Lists.newArrayList();
		lists.add(stacks);
		stacks.removeIf(Ingredient.fromStacks(recipe.getOutput())::apply);

		ingredients.setInputLists(ItemStack.class, lists);

		ingredients.setOutput(ItemStack.class, recipe.getOutput());
	}

	protected void drawGradientRect(int left, int top, int right, int bottom) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) right, (double) top, 0).color(1f, 1f, 1f, .5f).endVertex();
		bufferbuilder.pos((double) left, (double) top, 0).color(1f, 1f, 1f, .5f).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, 0).color(1f, 1f, 1f, .5f).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0).color(1f, 1f, 1f, .5f).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		WizardryJEIPlugin.fireCategory.flame.draw(minecraft, 30, 14);
		if (mouseX >= 30 && mouseX <= 30 + 16
				&& mouseY >= 14 && mouseY <= 14 + 16) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(30, 14, 30 + 16, 14 + 16);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
	}
}
