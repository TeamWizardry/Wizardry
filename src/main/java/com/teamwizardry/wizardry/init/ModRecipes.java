package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.crafting.irecipies.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Demoniaque on 4/8/2016.
 */
@Mod.EventBusSubscriber
public class ModRecipes {

	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipe> evt) {
		IForgeRegistry<IRecipe> r = evt.getRegistry();

		r.register(new RecipeJam().setRegistryName(path("jam")));
		r.register(new RecipePearl().setRegistryName(path("outputPearl")));
		r.register(new RecipeManaSyringe().setRegistryName(path("mana_syringe")));
		r.register(new RecipeSteroidSyringe().setRegistryName(path("steroid_syringe")));
		r.register(new RecipeCrudeHaloInfusion().setRegistryName(path("crude_halo_infusion")));
		r.register(new RecipeCrudeHaloDefusion().setRegistryName(path("crude_halo_defusion")));

	}

	private static ResourceLocation path(String name) {
		return new ResourceLocation(Wizardry.MODID, "recipe_" + name);
	}
}
