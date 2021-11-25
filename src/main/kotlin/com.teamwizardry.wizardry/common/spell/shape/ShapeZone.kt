package com.teamwizardry.wizardry.common.spell.shape

import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.init.ModSounds
import com.teamwizardry.wizardry.common.spell.component.Attributes.INTENSITY
import com.teamwizardry.wizardry.common.spell.component.Attributes.RANGE
import com.teamwizardry.wizardry.common.spell.component.Instance
import com.teamwizardry.wizardry.common.spell.component.Interactor
import com.teamwizardry.wizardry.common.spell.component.PatternShape
import com.teamwizardry.wizardry.common.utils.ColorUtils
import com.teamwizardry.wizardry.common.utils.MathUtils
import com.teamwizardry.wizardry.common.utils.RandUtil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.*
import net.minecraft.world.World
import java.awt.Color
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class ShapeZone : PatternShape() {
    override fun run(world: World, instance: Instance, target: Interactor) {
        val center: Vec3d = target.pos
        val range = instance.getAttributeValue(RANGE)
        val procFraction: Double = MathHelper.clamp(instance.getAttributeValue(INTENSITY), 0.0, 1.0)
        val region = Box(BlockPos(center)).expand(range - 1)
        val rangeSq = range * range
        val pointsTag = NbtCompound()
        val interactors: MutableList<Interactor> = ArrayList<Interactor>()
        Wizardry.LOGGER.debug("Range: $rangeSq")

        // Run on entities
        val entities: MutableList<LivingEntity> = world.getEntitiesByClass<LivingEntity>(
            LivingEntity::class.java,
            region,
            Predicate<LivingEntity> { entity: LivingEntity -> entity.getPos().squaredDistanceTo(center) <= rangeSq })
        val numEntityProcs = ceil(entities.size * procFraction).toInt()
        Wizardry.LOGGER.debug(
            """
    Entities: ${entities.size}
    procFrac: $procFraction
    numProcs: $numEntityProcs
    """.trimIndent()
        )
        while (entities.size > numEntityProcs) {
            entities.removeAt((Math.random() * entities.size).toInt())
        }
        for (entity in entities) {
            val interactor = Interactor(entity)
            val point: Vec3d = interactor.pos
            interactors.add(interactor)
            val pointNBT = NbtCompound()
            pointNBT.putDouble("x", point.x)
            pointNBT.putDouble("y", point.y)
            pointNBT.putDouble("z", point.z)
            pointsTag.put(UUID.randomUUID().toString(), pointNBT)
        }

        // Run on blocks
        val blocks: MutableList<BlockPos> = LinkedList<BlockPos>()
        var x = floor(region.minX).toInt()
        while (x < ceil(region.maxX)) {
            var y = floor(region.minY).toInt()
            while (y < ceil(region.maxY)) {
                var z = floor(region.minZ).toInt()
                while (z < ceil(region.maxZ)) {
                    if (center.squaredDistanceTo(x.toDouble(), y.toDouble(), z.toDouble()) <= rangeSq) {
                        blocks.add(BlockPos(x, y, z))
                    }
                    z++
                }
                y++
            }
            x++
        }
        val numBlockProcs = blocks.size * procFraction
        while (blocks.size > numBlockProcs) {
            blocks.removeAt((Math.random() * blocks.size).toInt())
        }
        for (pos in blocks) {
            val direction = Vec3d(
                pos.getX() + 0.5 - center.x,
                pos.getY() + 0.5 - center.y,
                pos.getZ() + 0.5 - center.z
            )
            val interactor = Interactor(pos, Direction.getFacing(direction.x, direction.y, direction.z))
            val point: Vec3d = interactor.pos
            interactors.add(interactor)
            val pointNBT = NbtCompound()
            pointNBT.putDouble("x", point.x)
            pointNBT.putDouble("y", point.y)
            pointNBT.putDouble("z", point.z)
            pointsTag.put(UUID.randomUUID().toString(), pointNBT)
        }
        instance.extraData.putBoolean("exploded", false)
        sendRenderPacket(world, instance, target)
        ModSounds.playSound(world, instance.caster, target, ModSounds.HIGH_PITCHED_SOLO_BLEEP, 0.5f, 2f)

//        SequenceEventLoop.createSequence(new Sequence(world, 10)
//                .event(0f, (seq) -> {
//                    instance.getExtraData().put("points", pointsTag);
//                    instance.getExtraData().putBoolean("exploded", true);
//                    sendRenderPacket(world, instance, target);
//                }).event(1f, (seq) -> {
//                    Sequence sequence = new Sequence(world, interactors.size());
//                    Collections.shuffle(interactors);
//                    for (int i = 0, interactorsSize = interactors.size(); i < interactorsSize; i++) {
//                        Interactor interactor = interactors.get(i);
//                        if (interactor.getPos().squareDistanceTo(center) <= range * range) {
//                            sequence.event((i / (float) interactorsSize),
//                                    (seq1) -> super.run(world, instance, interactor));
//                        }
//                    }
//
//                    SequenceEventLoop.createSequence(sequence);
//
//                }));
    }

    override fun disableAutomaticRenderPacket(): Boolean {
        return true
    }

    @Environment(EnvType.CLIENT)
    override fun runClient(world: World, instance: Instance, target: Interactor) {
        val nbt: NbtCompound = instance.extraData
        val range = instance.getAttributeValue(RANGE)
        val colors: Array<Color>? = ColorUtils.mergeColorSets(instance.effectColors)
        if (nbt.contains("exploded") && !nbt.getBoolean("exploded")) {
            for (i in 0..59) {
                val a = i / 60.0
                val dot: Vec2d = MathUtils.genCirclePerimeterDot(
                    range.toFloat(),
                    (360f * RandUtil.nextFloat(-10f, 10f) * a * Math.PI / 180.0f).toFloat()
                )
                val circleDotPos: Vec3d = target.pos.add(dot.x, 0.0, dot.y)
                //                Wizardry.PROXY.spawnKeyedParticle(
//                        new KeyFramedGlitterBox(RandUtil.nextInt(40, 50))
//                                .pos(target.getPos(), Easing.easeOutQuart)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .color(colors[0])
//                                .color(colors[1])
//                                .size(0, Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(0)
//                                .alpha(1)
//                                .alpha(1)
//                );
            }
        }
        if (nbt.contains("points")) {
            val points: NbtCompound = nbt.getCompound("points")
            for (pointKey in points.getKeys()) {
                val pointTag: NbtCompound = points.getCompound(pointKey)
                val point = Vec3d(pointTag.getDouble("x"), pointTag.getDouble("y"), pointTag.getDouble("z"))
                val yDist: Double = Math.abs(target.pos.y - point.getY())
                for (i in 0..4) {
                    val to: Vec3d = point.add(0.0, RandUtil.nextDouble(-yDist, yDist), 0.0)
                    //                    Wizardry.PROXY.spawnKeyedParticle(
//                            new KeyFramedGlitterBox(20)
//                                    .pos(point)
//                                    .pos(point, Easing.easeOutQuart)
//                                    .pos(to)
//                                    .color(colors[0])
//                                    .color(colors[1])
//                                    .size(0, Easing.easeOutQuart)
//                                    .size(0.3, Easing.linear)
//                                    .size(0.1, Easing.linear)
//                                    .size(0)
//                                    .alpha(1)
//                                    .alpha(1)
//                    );
                }
            }
        }
    }
}