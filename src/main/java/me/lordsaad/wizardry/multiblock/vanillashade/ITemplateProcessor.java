package me.lordsaad.wizardry.multiblock.vanillashade;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemplateProcessor
{
    @Nullable
    Template.BlockInfo func_189943_a(World p_189943_1_, BlockPos p_189943_2_, Template.BlockInfo p_189943_3_);
}