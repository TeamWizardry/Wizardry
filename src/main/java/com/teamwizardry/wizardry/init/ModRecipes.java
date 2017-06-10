package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.init.irecipies.RecipeJam;
import com.teamwizardry.wizardry.init.irecipies.RecipePearl;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Saad on 4/8/2016.
 */
public class ModRecipes {

	public static void initCrafting() {
		RecipeSorter.register("wizardry:pearl", RecipePearl.class, RecipeSorter.Category.SHAPELESS, "");
		RecipeSorter.register("wizardry:jam", RecipeJam.class, RecipeSorter.Category.SHAPELESS, "");
		GameRegistry.addRecipe(new RecipePearl());
		GameRegistry.addRecipe(new RecipeJam());

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS, 8),
				"AAA", "ABA", "AAA", 'A', ModBlocks.WISDOM_WOOD_PLANKS, 'B', Items.GOLD_NUGGET));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.WISDOM_STICK, 4),
				" A ", " A ", "   ", 'A', ModBlocks.WISDOM_WOOD_PLANKS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_SLAB),
				"   ", "   ", "AAA", 'A', ModBlocks.WISDOM_WOOD_PLANKS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_TRAPDOOR),
				"   ", "AAA", "AAA", 'A', ModBlocks.WISDOM_WOOD_PLANKS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_DOOR, 3),
				" AA", " AA", " AA", 'A', ModBlocks.WISDOM_WOOD_PLANKS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_FENCE_GATE),
				"   ", "ABA", "ABA", 'B', ModBlocks.WISDOM_WOOD_PLANKS, 'A', ModItems.WISDOM_STICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_FENCE, 3),
				"   ", "ABA", "ABA", 'A', ModBlocks.WISDOM_WOOD_PLANKS, 'B', ModItems.WISDOM_STICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_STAIRS, 4),
				"A  ", "AA ", "AAA", 'A', ModBlocks.WISDOM_WOOD_PLANKS));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.WISDOM_WOOD_PLANKS, 4),
				ModBlocks.WISDOM_WOOD_LOG));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.MAGICIANS_WORKTABLE),
				" A ", "BBB", "C C", 'A', Items.BOOK, 'B', ModBlocks.WISDOM_WOOD_SLAB, 'C', ModBlocks.WISDOM_WOOD_PLANKS));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.JAR),
				" A ", " B ", "   ", 'A', "buttonWood", 'B', Blocks.GLASS));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CRAFTING_PLATE),
				" A ", "CBC", "C C", 'A', Items.CLAY_BALL, 'B', ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS, 'C', ModItems.WISDOM_STICK));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PEARL_HOLDER),
				"A A", "CBC", "CCC", 'A', Items.GOLD_NUGGET, 'B', ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS, 'C', ModBlocks.WISDOM_WOOD_PLANKS));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.MAGIC_WAND),
				"  A", " B ", "A  ", 'A', Items.IRON_INGOT, 'B', new ItemStack(Items.DYE, 1, 0)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 1),
				ModItems.SYRINGE,
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.mana_fluid"))));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.STAFF, 1),
				" A ", " BA", "A  ", 'A', Items.GOLD_NUGGET, 'B', ModBlocks.WISDOM_WOOD_PLANKS));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.GLASS_ORB, 1, 0),
				" A ", "A A", " A ", 'A', Blocks.GLASS));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 0),
				" A ", "A A", "AA ", 'A', Blocks.GLASS_PANE));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 0),
				new ItemStack(ModItems.SYRINGE, 1, 1)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 0),
				new ItemStack(ModItems.SYRINGE, 1, 2)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SYRINGE, 1, 2),
				ModItems.SYRINGE,
				ModItems.DEVIL_DUST,
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.mana_fluid")),
				UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidRegistry.getFluid("wizardry.nacre_fluid")),
				Items.LAVA_BUCKET,
				ModItems.MANA_ORB));

		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.CAPE, " AA", " AA", " AA", 'A', Items.LEATHER));
	}
}
