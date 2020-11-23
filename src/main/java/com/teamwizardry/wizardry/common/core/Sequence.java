package com.teamwizardry.wizardry.common.core;

import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Sequence {

    private final long initTime;
    private final int lifetime;
    private final Map<Float, Consumer<Sequence>> keyframes = new HashMap<>();

    public boolean expired = false;

    public Sequence(World world, int lifetime) {
        this.lifetime = lifetime;
        initTime = world.getGameTime();
    }

    public Sequence event(float time, Consumer<Sequence> func) {
        keyframes.put(time, func);
        return this;
    }

    public void tick(World world) {
        if (expired) return;

        long time = world.getGameTime();
        long diff = time - initTime;
        if (diff > lifetime) {
            expired = true;
            return;
        }
        float t = diff / (float) lifetime;
        if (keyframes.containsKey(t)) {
            keyframes.get(t).accept(this);
        }
    }
}
