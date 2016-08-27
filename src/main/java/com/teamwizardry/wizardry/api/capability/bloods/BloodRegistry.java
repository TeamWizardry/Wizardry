package com.teamwizardry.wizardry.api.capability.bloods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// some vampiric shit right here
public final class BloodRegistry {

    private static final BiMap<IBloodType, String> values = HashBiMap.create();
    public static final IBloodType TERRABLOOD = register(new TerraBlood(), "terra");
    public static final IBloodType AQUABLOOD = register(new AquaBlood(), "aqua");
    public static final IBloodType ZEPHYRBLOOD = register(new AeroBlood(), "zephyr");
    public static final IBloodType PYROBLOOD = register(new PyroBlood(), "pyro");

    public static BiMap<IBloodType, String> getRegistry() {
        return values;
    }

    public static IBloodType register(IBloodType blood, String registryName) {
        if (registryName.equals("")) return null;
        values.putIfAbsent(blood, registryName);
        return blood;
    }

    public static IBloodType getBloodTypeByName(String id) {
        if (id == null || id.equals("")) return null;
        return values.inverse().get(id);
    }

    public static String getBloodNameByType(IBloodType iBloodType) {
        if (iBloodType == null) return "";
        return values.get(iBloodType);
    }
}

