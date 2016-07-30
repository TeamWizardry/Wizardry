package com.teamwizardry.wizardry.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Saad on 4/8/2016.
 */
public class ModRecipes {

    public static void initCrafting() {
        GameRegistry.addRecipe(new ModIRecipes());
    }
}
