package com.teamwizardry.wizardry.common.spell.shape

import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.PatternShape
import net.fabricmc.api.Environment
import net.minecraft.entity.Entity
import net.minecraft.util.math.Direction
import java.awt.Color
import java.util.function.Predicate

class ShapeRay : PatternShape() {
    fun run(world: World?, instance: Instance, target: Interactor) {
        val start: Vec3d = target.getPos()
        val end: Vec3d = start.add(target.getLook().multiply(instance.getAttributeValue(RANGE)))
        val sourceEntity: Entity = target.getEntity()
        ray.cast(
            world, BlockMode.VISUAL, FluidMode.ANY,
            Predicate { entity: Entity -> entity is LivingEntity && entity != sourceEntity },  // TODO - where'd the better equality check move to?
            start.x,
            start.y,
            start.z,
            end.x,
            end.y,
            end.z
        )
        var newTarget: Interactor? = null
        when (ray.hitType) {
            HitType.NONE, HitType.BLOCK, HitType.FLUID -> {
                val dir: Vec3d = end.subtract(start)
                val hit = Vec3d(ray.hitX, ray.hitY, ray.hitZ)
                newTarget = Interactor(BlockPos(hit), Direction.getFacing(dir.x, dir.y, dir.z))
            }
            HitType.ENTITY -> newTarget = Interactor(ray.entity as LivingEntity)
        }
        ray.reset()
        playSound(world, instance.caster, target, ModSounds.SUBTLE_MAGIC_BOOK_GLINT)
        super.run(world, instance, newTarget)
    }

    @Environment(EnvType.CLIENT)
    fun runClient(world: World?, instance: Instance, target: Interactor) {
        val colors: Array<Color> = ColorUtils.mergeColorSets(instance.effectColors)
        val v1: Vec3d? = instance.caster.clientPos
        val v2: Vec3d = target.getPos()
        for (i in 0..29) {
            val a = i / 30.0
            Wizardry.Companion.PROXY.spawnParticle(
                GlitterBoxFactory()
                    .setOrigin(
                        v1.getX() * a + v2.getX() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035),
                        v1.getY() * a + v2.getY() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035),
                        v1.getZ() * a + v2.getZ() * (1 - a) + RandUtil.nextDouble(-0.035, 0.035)
                    )
                    .setGravity(RandUtil.nextFloat(-0.001f, -0.003f))
                    .setInitialColor(colors[0])
                    .setGoalColor(colors[1])
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