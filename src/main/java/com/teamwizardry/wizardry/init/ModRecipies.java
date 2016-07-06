package com.teamwizardry.wizardry.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Saad on 4/8/2016.
 */
public class ModRecipies {

    public static void initCrafting() {
        GameRegistry.addRecipe(new ModIRecipies());
    }
}
