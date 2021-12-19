package com.teamwizardry.wizardry.mixins;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

public interface Invokers {
    @Mixin(DoorBlock.class)
    interface DoorInvoker {
        @Invoker("<init>")
        public static DoorBlock construct(Settings settings) { throw new AssertionError(); }
    }

    @Mixin(StairsBlock.class)
    interface StairsInvoker {
        @Invoker("<init>")
        public static StairsBlock construct(BlockState state, Settings settings) { throw new AssertionError(); }
    }

    @Mixin(TrapdoorBlock.class)
    interface TrapdoorInvoker {
        @Invoker("<init>")
        public static TrapdoorBlock construct(Settings settings) { throw new AssertionError(); }
    }
}
