package com.teamwizardry.wizardry.common.core;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class SequenceEventLoop {
    private static final List<Sequence> sequenceList = new ArrayList<>();

    @SubscribeEvent
    public static void tickEvent(TickEvent.WorldTickEvent event) {
        sequenceList.removeIf(sequence -> {
            sequence.tick(event.world);
            return sequence.expired;
        });
    }

    public static void createSequence(Sequence sequence) {
        sequenceList.add(sequence);
    }
}
