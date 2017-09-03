package com.teamwizardry.wizardry.common.potion;

/*
 * This file was created at 01:06 on 03 Sep 2017 by InsomniaKitten
 *
 * It is distributed as part of the Wizardry mod.
 * Source code is visible at: https://github.com/InsomniaKitten/Wizardry
 *
 * Copyright (c) InsomniaKitten 2017. All Rights Reserved.
 */

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.librarianlib.features.helpers.VariantHelper;
import com.teamwizardry.wizardry.Wizardry;
import org.jetbrains.annotations.NotNull;

public class PotionBase extends PotionMod {

    public PotionBase(@NotNull String name, boolean badEffect, int color) {
        super(name, badEffect, color);
        setPotionName("potion." + Wizardry.MODID + VariantHelper.toSnakeCase(name));
    }
}
