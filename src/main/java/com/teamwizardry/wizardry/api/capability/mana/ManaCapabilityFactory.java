package com.teamwizardry.wizardry.api.capability.mana;

import java.util.concurrent.Callable;

public class ManaCapabilityFactory implements Callable<IManaCapability> {
    @Override
    public IManaCapability call() throws Exception {
        return new ManaCapabilityImpl(0, 0, 0, 0);
    }
}
