package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.init.irecipies.RecipeJam;
import com.teamwizardry.wizardry.init.irecipies.RecipePearl;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Saad on 4/8/2016.
 */
public class ModRecipes {

	public static void initCrafting() {
		GameRegistry.addRecipe(new RecipePearl());
		GameRegistry.addRecipe(new RecipeJam());

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 1),
				ModItems.SYRINGE,
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.mana_fluid"))));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 0),
				new ItemStack(ModItems.SYRINGE, 1, 1)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 0),
				new ItemStack(ModItems.SYRINGE, 1, 2)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 2),
				ModItems.SYRINGE,
				ModItems.DEVIL_DUST,
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.mana_fluid")),
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.nacre_fluid")),
				Items.LAVA_BUCKET));
	}
}
