package com.teamwizardry.wizardry.multiblock.vanillashade;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockRotationProcessor implements ITemplateProcessor {
    private final float field_189944_a;
    private final Random field_189945_b;

    public BlockRotationProcessor(BlockPos p_i47148_1_, PlacementSettings p_i47148_2_) {
        this.field_189944_a = p_i47148_2_.func_189948_f();
        this.field_189945_b = p_i47148_2_.func_189947_a(p_i47148_1_);
    }

    @Nullable
    public Template.BlockInfo func_189943_a(World p_189943_1_, BlockPos p_189943_2_, Template.BlockInfo p_189943_3_) {
        return this.field_189944_a < 1.0F && this.field_189945_b.nextFloat() > this.field_189944_a ? null : p_189943_3_;
    }
}