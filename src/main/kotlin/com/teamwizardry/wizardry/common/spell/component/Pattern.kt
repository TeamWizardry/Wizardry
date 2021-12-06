package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.wizardry.client.lib.LibTheme
import com.teamwizardry.wizardry.common.init.ModPatterns
import com.teamwizardry.wizardry.common.utils.RandUtil
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.awt.Color

/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the `modid:name` pair the pattern was
 * registered under
 */
abstract class Pattern {
    val id: Identifier get() = ModPatterns.PATTERN.getId(this)

    open val colors: Array<Color> = arrayOf(
        LibTheme.accentColor,
        LibTheme.hintColor,
        LibTheme.backgroundColor
    )

    open fun run(world: World, instance: Instance, target: Interactor) {
        when (target.type) {
            Interactor.InteractorType.BLOCK -> {
                when (instance.targetType) {
                    TargetType.ALL, TargetType.BLOCK -> if (instance.caster.consumeCost(world, instance.manaCost))
                        affectBlock(world, target, instance)
                    else -> {}
                }
            }
            Interactor.InteractorType.ENTITY -> {
                when (instance.targetType) {
                    TargetType.ALL, TargetType.ENTITY -> if (instance.caster.consumeCost(world, instance.manaCost))
                        affectEntity(world, target, instance)
                    else -> {}
                }
            }
        }
        if (!disableAutomaticRenderPacket()) sendRenderPacket(world, instance, target)
    }

    protected open fun disableAutomaticRenderPacket(): Boolean {
        return false
    }

    /**
     * Call this whenever you want to send a render packet of the current instance to the client.
     * Shapes trigger this with an updated target [Interactor] so the client can render exactly where the target is.
     * Effects also use this for obvious reasons.
     */
    protected fun sendRenderPacket(world: World, instance: Instance, target: Interactor) {
//        WizConsts.getCourier().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPos().x,
//                        target.getPos().y,
//                        target.getPos().z,
//                        256,
//                        world.getDimensionKey())),
//                new CRenderSpellPacket.Packet(instance.toNBT(), target.toNBT()));
    }

    open fun runClient(world: World, instance: Instance, target: Interactor) {}
    abstract fun affectEntity(world: World, entity: Interactor, instance: Instance)
    abstract fun affectBlock(world: World, block: Interactor, instance: Instance)
    protected val randomColor: Color
        get() = colors[RandUtil.nextInt(colors.size)]
}