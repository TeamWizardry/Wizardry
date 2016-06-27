package com.teamwizardry.wizardry.multiblock.vanillashade;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ITemplateProcessor {
    @Nullable
    Template.BlockInfo func_189943_a(World p_189943_1_, BlockPos p_189943_2_, Template.BlockInfo p_189943_3_);
}