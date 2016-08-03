package com.teamwizardry.wizardry.api.bloods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//some vampiric shit right there, folks
public final class BloodRegistry {

    private static final BiMap<IBloodType, String> values = HashBiMap.create();

    public static BiMap<IBloodType, String> getRegistry() {
        return values;
    }

    public static final IBloodType TERRABLOOD = register(new TerraBlood(), "terra");
    public static final IBloodType AQUABLOOD = register(new AquaBlood(), "aqua");
    public static final IBloodType ZEPHYRBLOOD = register(new AeroBlood(), "zephyr");
    public static final IBloodType PYROBLOOD = register(new PyroBlood(), "pyro");

    public static IBloodType register(IBloodType blood, String registryName) {
        values.putIfAbsent(blood, registryName);
        return blood;
    }

    public static IBloodType getBloodTypeById(String id) {
        if (id == null) return null;
        return values.inverse().get(id);
    }

    public static String getBloodTypeId(IBloodType iBloodType) {
        return values.get(iBloodType);
    }
}

