package com.teamwizardry.wizardry.common.core;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class SequenceEventLoop {
    private static final List<Sequence> sequenceList = new ArrayList<>();

    @SubscribeEvent
    public static void tickEvent(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != LogicalSide.SERVER) return;

        List<Sequence> dupe = new ArrayList<>(sequenceList);
        for (Sequence sequence : dupe) {
            sequence.tick(event.world);
            if (sequence.expired) sequenceList.remove(sequence);
        }
    }

    public static void createSequence(Sequence sequence) {
        sequenceList.add(sequence);
    }
}
