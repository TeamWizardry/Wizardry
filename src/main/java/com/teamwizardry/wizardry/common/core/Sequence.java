package com.teamwizardry.wizardry.common.core;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sequence {

    private final long initTime;
    private final int lifetime;
    private final List<Key> keyframes = new ArrayList<>();

    public boolean expired = false;

    public Sequence(World world, int lifetime) {
        this.lifetime = lifetime;
        initTime = world.getGameTime();
    }

    public Sequence event(float time, Consumer<Sequence> func) {
        keyframes.add(new Key(time, func));
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

        float fraction = diff / (float) lifetime;
        for (Key key : keyframes) {
            if (key.expired) continue;
            if (key.time <= fraction) {
                key.func.accept(this);
                key.expired = true;
            }
        }
    }
}

class Key {
    public final float time;
    public final Consumer<Sequence> func;
    public boolean expired;

    Key(float time, Consumer<Sequence> func) {
        this.time = time;
        this.func = func;
    }
}
