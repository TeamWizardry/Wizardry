package com.teamwizardry.wizardry.client.jei.fire;

import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.jei.DrawableAtlas;
import com.teamwizardry.wizardry.client.jei.WizardryJEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author WireSegal
 * Created at 4:50 PM on 1/13/18.
 */
@SideOnly(Side.CLIENT)
public class FireCraftingCategory implements IRecipeCategory<FireRecipeJEI> {

	private static final ItemStack TOOLTIP_STACK = new ItemStack(Blocks.BEDROCK);
	public IDrawable background = WizardryJEIPlugin.helpers.getGuiHelper()
			.createDrawable(new ResourceLocation(Wizardry.MODID, "textures/gui/categories.png"), 0, 0, 76, 44);
	public IDrawable flame = new DrawableAtlas(
			Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_0"));

	@Nonnull
	@Override
	public String getUid() {
		return Wizardry.MODID + ":fire";
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

	@Nullable
	@Override
	public IDrawable getIcon() {
		return flame;
	}

	@Nonnull
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX >= 30 && mouseX <= 30 + 16
				&& mouseY >= 14 && mouseY <= 14 + 16) {
			List<String> tooltip = TOOLTIP_STACK.getTooltip(Minecraft.getMinecraft().player,
					Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
			tooltip.set(0, TooltipHelper.local("jei.recipe.tooltip." + getUid()));
			for (int i = 0; i < tooltip.size(); i++)
				tooltip.set(i, tooltip.get(i).replace("minecraft:bedrock", "minecraft:fire"));
			return tooltip;
		}
		return Collections.emptyList();
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull FireRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 0, 13);
		recipeLayout.getItemStacks().init(1, false, 58, 13);

		recipeLayout.getItemStacks().set(ingredients);
	}
}
