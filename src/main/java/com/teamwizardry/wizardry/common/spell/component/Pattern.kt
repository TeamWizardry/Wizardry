package com.teamwizardry.wizardry.common.spell.component;

import java.awt.Color;

import com.teamwizardry.wizardry.client.lib.LibTheme;
import com.teamwizardry.wizardry.common.utils.RandUtil;

import net.minecraft.world.World;


/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the {@code modid:name} pair the pattern was
 * registered under
 */
public abstract class Pattern {

    private static final Color[] defaultColors = new Color[]{LibTheme.accentColor,
            LibTheme.hintColor,
            LibTheme.backgroundColor};

    public void run(World world, Instance instance, Interactor target) {
        if (instance == null) return;
        if (instance.getCaster() == null || target == null)
            return;

        switch (target.getType()) {
            case BLOCK: {
                switch (instance.getTargetType()) {
                    case ALL:
                    case BLOCK:
                        if (instance.getCaster().consumeCost(world, instance.getManaCost(), instance.getBurnoutCost()))
                            affectBlock(world, target, instance);
                    default:
                }
                break;
            }
            case ENTITY: {
                switch (instance.getTargetType()) {
                    case ALL:
                    case ENTITY:
                        if (instance.getCaster().consumeCost(world, instance.getManaCost(), instance.getBurnoutCost()))
                            affectEntity(world, target, instance);
                    default:
                }
                break;
            }
        }

        if (!disableAutomaticRenderPacket())
            sendRenderPacket(world, instance, target);
    }

    public boolean disableAutomaticRenderPacket() {
        return false;
    }

    /**
     * Call this whenever you want to send a render packet of the current instance to the client.
     * Shapes trigger this with an updated target {@link Interactor} so the client can render exactly where the target is.
     * Effects also use this for obvious reasons.
     */
    protected void sendRenderPacket(World world, Instance instance, Interactor target) {
//        WizConsts.getCourier().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPos().x,
//                        target.getPos().y,
//                        target.getPos().z,
//                        256,
//                        world.getDimensionKey())),
//                new CRenderSpellPacket.Packet(instance.toNBT(), target.toNBT()));
    }

    public void runClient(World world, Instance instance, Interactor target) {
    }

    public abstract void affectEntity(World world, Interactor entity, Instance instance);

    public abstract void affectBlock(World world, Interactor block, Instance instance);

    public Color[] getColors() {
        return defaultColors;
    }

    protected Color getRandomColor() {
        return getColors()[RandUtil.nextInt(getColors().length)];
    }
}
