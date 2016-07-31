package com.teamwizardry.wizardry.api.bloods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//some vampiric shit right there, folks
public class BloodRegistry {
    //public static final IBloodType NULLBLOOD = getRegistry().register(null);
    public static final IBloodType TERRABLOOD = getRegistry().register(new TerraBlood());
    public static final IBloodType AQUABLOOD = getRegistry().register(new AquaBlood());
    public static final IBloodType ZEPHYRBLOOD = getRegistry().register(new AeroBlood());
    public static final IBloodType PYROBLOOD = getRegistry().register(new PyroBlood());
    private BloodRegistry() {}
    public static BloodRegistry getRegistry() {
        return instance;
    }
    public static BloodRegistry instance = new BloodRegistry();
    public BiMap<IBloodType, Integer> values = HashBiMap.create(512);
    private int ID = 0;
    public IBloodType register(IBloodType blood) {
        values.putIfAbsent(blood, ID++);
        return blood;
    }
    public IBloodType getBloodTypeById(int id) {
        return values.inverse().get(id);
    }
    public int getBloodTypeId(IBloodType iBloodType) {
        return values.get(iBloodType);
    }
}

