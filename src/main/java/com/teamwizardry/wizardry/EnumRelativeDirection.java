package com.teamwizardry.wizardry;

import net.minecraft.util.IStringSerializable;

/**
 * Created by Saad on 6/14/2016.
 */
public enum EnumRelativeDirection implements IStringSerializable {
    LEFT("left"), RIGHT("right");

    private String direction;

    EnumRelativeDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
