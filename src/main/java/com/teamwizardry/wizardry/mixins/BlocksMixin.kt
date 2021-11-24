package com.teamwizardry.wizardry.mixins

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(Blocks::class)
interface BlocksMixin {
    companion object {
        @Invoker("never")
        fun never(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
            throw AssertionError()
        }

        @Invoker("canSpawnOnLeaves")
        fun canSpawnOnLeaves(state: BlockState?, world: BlockView?, pos: BlockPos?, type: EntityType<*>?): Boolean? {
            throw AssertionError()
        }
    }
}