package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.librarianlib.foundation.capability.SimpleCapabilityStorage;
import com.teamwizardry.librarianlib.foundation.registration.CapabilitySpec;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.mana.ManaCapability;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    public static void registerCapabilities(RegistrationManager reginald) {
        reginald.add(
                new CapabilitySpec<>(IManaCapability.class,
                        new SimpleCapabilityStorage<>(),
                        () -> new ManaCapability(0, 1000, 0, 1000)));
    }
}
