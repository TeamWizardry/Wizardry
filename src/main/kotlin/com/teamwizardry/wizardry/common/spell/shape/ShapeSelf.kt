package com.teamwizardry.wizardry.common.spell.shape

import com.teamwizardry.librarianlib.etcetera.Raycaster
import com.teamwizardry.wizardry.common.init.ModSounds
import com.teamwizardry.wizardry.common.init.ModSounds.playSound
import com.teamwizardry.wizardry.common.spell.component.Attributes.RANGE
import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.Interactor
import com.teamwizardry.wizardry.common.spell.component.PatternShape
import com.teamwizardry.wizardry.common.utils.ColorUtils
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color

class ShapeSelf : PatternShape() {
    override fun run(world: World, instance: Instance, target: Interactor) {
        playSound(world, instance.caster, target, ModSounds.SUBTLE_MAGIC_BOOK_GLINT)
        super.run(world, instance, target)
    }

    @Environment(EnvType.CLIENT)
    override fun runClient(world: World, instance: Instance, target: Interactor) {

    }
}