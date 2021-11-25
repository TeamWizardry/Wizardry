package com.teamwizardry.wizardry.common.spell.shape

import com.teamwizardry.librarianlib.etcetera.Raycaster
import com.teamwizardry.wizardry.PROXY
import com.teamwizardry.wizardry.client.lib.LibTheme
import com.teamwizardry.wizardry.client.particle.GlitterBox
import com.teamwizardry.wizardry.common.init.ModSounds
import com.teamwizardry.wizardry.common.init.ModSounds.playSound
import com.teamwizardry.wizardry.common.spell.component.Attributes.RANGE
import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.Interactor
import com.teamwizardry.wizardry.common.spell.component.PatternShape
import com.teamwizardry.wizardry.common.utils.ColorUtils
import com.teamwizardry.wizardry.common.utils.RandUtil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color

class ShapeRay : PatternShape() {
    override fun run(world: World, instance: Instance, target: Interactor) {
        val start: Vec3d = target.pos
        val end: Vec3d = start.add(target.look.multiply(instance.getAttributeValue(RANGE)))
        val sourceEntity: Entity? = target.entity
        ray.cast(
                world, Raycaster.BlockMode.VISUAL, Raycaster.FluidMode.ANY,
                { entity: Entity -> entity is LivingEntity && entity != sourceEntity },  // TODO - where'd the better equality check move to?
                start.x,
                start.y,
                start.z,
                end.x,
                end.y,
                end.z
        )
        val newTarget: Interactor = when (ray.hitType) {
            Raycaster.HitType.NONE, Raycaster.HitType.BLOCK, Raycaster.HitType.FLUID -> {
                val dir: Vec3d = end.subtract(start)
                val hit = Vec3d(ray.hitX, ray.hitY, ray.hitZ)
                Interactor(BlockPos(hit), Direction.getFacing(dir.x, dir.y, dir.z))
            }
            Raycaster.HitType.ENTITY -> Interactor(ray.entity as LivingEntity)
        }
        ray.reset()
        playSound(world, instance.caster, target, ModSounds.SUBTLE_MAGIC_BOOK_GLINT)
        super.run(world, instance, newTarget)
    }

    @Environment(EnvType.CLIENT)
    override fun runClient(world: World, instance: Instance, target: Interactor) {
        val colors: Array<Color>? = ColorUtils.mergeColorSets(instance.effectColors)
        val v1: Vec3d = instance.caster.clientPos
        val v2: Vec3d = target.pos
        for (i in 0..29) {
            val a = i / 30.0
            PROXY.spawnParticle(
                    GlitterBox.GlitterBoxFactory()
                    .setOrigin(
                        v1.getX() * a + v2.getX() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035),
                        v1.getY() * a + v2.getY() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035),
                        v1.getZ() * a + v2.getZ() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035)
                    )
                    .setGravity(RandUtil.nextFloat(-0.001f, -0.003f))
                    .setInitialColor(colors?.get(0) ?: LibTheme.accentColor)
                    .setGoalColor(colors?.get(1) ?: LibTheme.hintColor)
                    .setInitialSize(RandUtil.nextFloat(0.05f, 0.2f))
                    .setGoalSize(0f)
                    .createGlitterBox(RandUtil.nextInt(5, 10))
            )
        }
    }

    companion object {
        private val ray: Raycaster = Raycaster()
    }
}