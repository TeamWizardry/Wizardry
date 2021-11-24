package com.teamwizardry.wizardry.common.spell.component

import com.teamwizardry.wizardry.client.lib.LibTheme
import java.awt.Color

/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the `modid:name` pair the pattern was
 * registered under
 */
abstract class Pattern {
    fun run(world: World, instance: Instance?, target: Interactor?) {
        if (instance == null) return
        if (instance.getCaster() == null || target == null) return
        when (target.type) {
            InteractorType.BLOCK -> {
                when (instance.getTargetType()) {
                    ALL, BLOCK -> if (instance.getCaster()!!
                            .consumeCost(world, instance.getManaCost(), instance.getBurnoutCost())
                    ) affectBlock(world, target, instance)
                    else -> {}
                }
            }
            InteractorType.ENTITY -> {
                when (instance.getTargetType()) {
                    ALL, ENTITY -> if (instance.getCaster()!!
                            .consumeCost(world, instance.getManaCost(), instance.getBurnoutCost())
                    ) affectEntity(world, target, instance)
                    else -> {}
                }
            }
        }
        if (!disableAutomaticRenderPacket()) sendRenderPacket(world, instance, target)
    }

    private fun disableAutomaticRenderPacket(): Boolean {
        return false
    }

    /**
     * Call this whenever you want to send a render packet of the current instance to the client.
     * Shapes trigger this with an updated target [Interactor] so the client can render exactly where the target is.
     * Effects also use this for obvious reasons.
     */
    protected fun sendRenderPacket(world: World?, instance: Instance?, target: Interactor?) {
//        WizConsts.getCourier().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPos().x,
//                        target.getPos().y,
//                        target.getPos().z,
//                        256,
//                        world.getDimensionKey())),
//                new CRenderSpellPacket.Packet(instance.toNBT(), target.toNBT()));
    }

    fun runClient(world: World?, instance: Instance?, target: Interactor?) {}
    abstract fun affectEntity(world: World?, entity: Interactor?, instance: Instance?)
    abstract fun affectBlock(world: World?, block: Interactor?, instance: Instance?)
    protected val randomColor: Color
        protected get() = colors[RandUtil.nextInt(colors.size)]

    companion object {
        val colors = arrayOf<Color>(
            LibTheme.accentColor,
            LibTheme.hintColor,
            LibTheme.backgroundColor
        )
    }
}