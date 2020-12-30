package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.api.capability.mana.ManaCapabilityImpl;

public class TileManaBattery extends ManaCapabilityImpl  {
    public TileManaBattery(long mana, long maxMana, long burnout, long maxBurnout) {
        super(mana, maxMana, burnout, maxBurnout);
    }
}
