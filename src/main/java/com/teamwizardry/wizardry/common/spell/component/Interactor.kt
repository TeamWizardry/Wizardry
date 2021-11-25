package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.librarianlib.core.util.kotlin.InconceivableException
import com.teamwizardry.wizardry.common.block.IManaNode
import net.minecraft.block.*
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.cos
import kotlin.math.sin

class Interactor {
    enum class InteractorType {
        ENTITY, BLOCK
    }

    val type: InteractorType
    val entity: LivingEntity?
    val blockPos: BlockPos?
    private val dir: Direction?

    /**
     * Create an Interactor attached to a given [BlockPos].
     * Spells cast on this block should take the [Direction]
     * into account, but this is by no means required.
     */
    constructor(pos: BlockPos?, dir: Direction?) {
        type = InteractorType.BLOCK
        blockPos = pos
        this.dir = dir
        entity = null
    }

    /**
     * Create an Interactor attached to a given [LivingEntity].
     * Spells cast on this entity are not guaranteed to have consistent
     * locations or directions, take this into account when dealing with
     * non-instantaneous effects.
     */
    constructor(entity: LivingEntity?) {
        type = InteractorType.ENTITY
        this.entity = entity
        blockPos = null
        dir = null
    }

    /**
     * Gives the location the Interactor is currently seeing.
     * Returns the current eye position of the linked [LivingEntity]
     * or the center of the linked [BlockPos].
     */
    val pos: Vec3d
        get() {
            return when (type) {
                InteractorType.ENTITY -> entity!!.pos.add(0.0, entity.getEyeHeight(entity.pose).toDouble(), 0.0)
                InteractorType.BLOCK -> Vec3d.ofCenter(blockPos)
            }
        }

    /**
     * This is marked as client only because if the caster is a player, we want the origin to be
     * their physical hand.
     * Never use this outside of rendering purposes only.
     */
    val clientPos: Vec3d
        get() {
            val pos: Vec3d = pos
            if (type == InteractorType.ENTITY && entity is PlayerEntity) {
                val offX = 0.5f * sin(Math.toRadians((-90.0f - entity.bodyYaw).toDouble())).toFloat()
                val offZ = 0.5f * cos(Math.toRadians((-90.0f - entity.bodyYaw).toDouble())).toFloat()
                return Vec3d(offX.toDouble(), 0.0, offZ.toDouble()).add(pos)
            }
            return pos
        }

    /**
     * Tells the "direction" this Interactor is facing. For [LivingEntity] Interactors
     * this is their look vector, for [BlockPos] Interactors it is the [Vec3d] form
     * of their [Direction].
     */
    val look: Vec3d
        get() {
            return when (type) {
                InteractorType.ENTITY -> entity!!.rotationVector
                InteractorType.BLOCK -> Vec3d.of(dir?.vector)
            }
        }

    /**
     * Drains mana from and adds burnout to the Interactor's target.
     * @return Returns `true` if enough mana was drained to cast the spell.
     * Returns `false` otherwise, although mana and burnout are still modified.
     */
    fun consumeCost(world: World, mana: Double, burnout: Double): Boolean {
        return when (type) {
            InteractorType.BLOCK -> {
                val block: Block = world.getBlockState(blockPos).block
                if (block is IManaNode) (block as IManaNode).removeMana(world, blockPos, mana) <= 0 else false
            }
            InteractorType.ENTITY -> //                IManaCapability cap = this.entity.getCapability(MANA_CAPABILITY).orElse(null);
//                if (cap == null) return false;
//                cap.setBurnout(Math.min(cap.getBurnout() + burnout, cap.getMaxBurnout()));
//                if (cap.getMana() < mana)
//                {
//                    cap.setMana(0);
//                    return false;
//                }
//                cap.setMana(cap.getMana() - mana);
                true
        }
    }

    fun toNBT(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString("type", type.toString())
        when (type) {
            InteractorType.ENTITY -> nbt.putInt("entity", entity?.id ?: 0)
            InteractorType.BLOCK -> {
                val pos = NbtCompound()
                pos.putInt("x", blockPos!!.x)
                pos.putInt("y", blockPos.y)
                pos.putInt("z", blockPos.z)
                nbt.put("block", pos)
                nbt.putInt("dir", dir!!.id)
            }
        }
        return nbt
    }

    companion object {
        fun fromNBT(world: World, nbt: NbtCompound): Interactor? {
            return when (InteractorType.valueOf(nbt.getString("type"))) {
                InteractorType.ENTITY -> {
                    val entity: Entity? = world.getEntityById(nbt.getInt("entity"))
                    if (entity is LivingEntity) Interactor(entity) else null
                }
                InteractorType.BLOCK -> {
                    val pos: NbtCompound = nbt.getCompound("block")
                    Interactor(
                        BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z")),
                        Direction.byId(nbt.getInt("dir"))
                    )
                }
            }
        }
    }
}