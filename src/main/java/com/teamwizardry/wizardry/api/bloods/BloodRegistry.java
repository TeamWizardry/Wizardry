package com.teamwizardry.wizardry.api.bloods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//some vampiric shit right there, folks
public final class BloodRegistry {

    private static final BiMap<IBloodType, Integer> values = HashBiMap.create();

    public static BiMap<IBloodType, Integer> getRegistry() {
        return values;
    }

    public static final IBloodType TERRABLOOD = register(new TerraBlood());
    public static final IBloodType AQUABLOOD = register(new AquaBlood());
    public static final IBloodType ZEPHYRBLOOD = register(new AeroBlood());
    public static final IBloodType PYROBLOOD = register(new PyroBlood());

    private static int ID = 0;

    public static IBloodType register(IBloodType blood) {
        values.putIfAbsent(blood, ID++);
        return blood;
    }
    public static IBloodType getBloodTypeById(int id) {
        return values.inverse().get(id);
    }
    public static int getBloodTypeId(IBloodType iBloodType) {
        return values.get(iBloodType);
    }
}

