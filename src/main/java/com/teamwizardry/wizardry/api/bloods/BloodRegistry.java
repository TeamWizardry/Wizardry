package com.teamwizardry.wizardry.api.bloods;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

//some vampiric shit right there, folks
public class BloodRegistry {
    public IBloodType NULLBLOOD = register(null);
    public IBloodType TERRABLOOD = register(new TerraBlood());
    public IBloodType AQUABLOOD = register(new AquaBlood());
    public IBloodType AEROBLOOD = register(new AeroBlood());
    public IBloodType PYROBLOOD = register(new PyroBlood());
    private BloodRegistry() {}
    public static BloodRegistry getRegistry() {
        return instance;
    }
    public static BloodRegistry instance = new BloodRegistry();
    public BiMap<IBloodType, Integer> values = HashBiMap.create(512);
    private int ID = 0;
    public IBloodType register(IBloodType blood) {
        return getBloodTypeById(values.putIfAbsent(blood, ID++));
    }
    public IBloodType getBloodTypeById(int id) {
        return values.inverse().get(id);
    }
    public int getBloodTypeId(IBloodType iBloodType) {
        return values.get(iBloodType);
    }
}

