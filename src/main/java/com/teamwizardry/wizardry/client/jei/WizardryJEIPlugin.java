package com.teamwizardry.wizardry.client.jei;

import com.teamwizardry.wizardry.client.jei.fire.FireCraftingCategory;
import com.teamwizardry.wizardry.client.jei.fire.FireRecipeJEI;
import com.teamwizardry.wizardry.client.jei.mana.ManaCraftingCategory;
import com.teamwizardry.wizardry.client.jei.mana.ManaRecipeJEI;
import com.teamwizardry.wizardry.crafting.burnable.FireRecipes;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;
import com.teamwizardry.wizardry.init.ModItems;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Collectors;

/**
 * @author WireSegal
 * Created at 4:41 PM on 1/13/18.
 */
@JEIPlugin
@SideOnly(Side.CLIENT)
public class WizardryJEIPlugin implements IModPlugin {
    public static IJeiHelpers helpers;

    public static FireCraftingCategory fireCategory;
    public static ManaCraftingCategory manaCategory;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        helpers = registry.getJeiHelpers();
        registry.addRecipeCategories(fireCategory = new FireCraftingCategory());
        registry.addRecipeCategories(manaCategory = new ManaCraftingCategory());
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(FireRecipes.RECIPES.entrySet().stream()
                .map(FireRecipeJEI::new).collect(Collectors.toList()),
                fireCategory.getUid());

        registry.addRecipes(ManaRecipes.RECIPE_REGISTRY.values().stream()
                        .filter(recipe -> !recipe.getOutput().isEmpty() || recipe.getFluidOutput() != null)
                        .map(ManaRecipeJEI::new).collect(Collectors.toList()),
                manaCategory.getUid());

        registry.addRecipeCatalyst(new ItemStack(Items.FIRE_CHARGE), fireCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(ModItems.MANA_ORB), manaCategory.getUid());
    }
}
