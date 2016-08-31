package com.teamwizardry.wizardry.api.capability;

import static com.teamwizardry.wizardry.api.Constants.Data.BLOOD_LEVELS;
import static com.teamwizardry.wizardry.api.Constants.Data.BLOOD_TYPE;
import static com.teamwizardry.wizardry.api.Constants.Data.BURNOUT;
import static com.teamwizardry.wizardry.api.Constants.Data.MANA;
import static com.teamwizardry.wizardry.api.Constants.Data.MAX_BURNOUT;
import static com.teamwizardry.wizardry.api.Constants.Data.MAX_MANA;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;

/**
 * Created by Saad on 8/16/2016.
 */
public class WizardryCapabilityStorage implements Capability.IStorage<IWizardryCapability> {

    public static final WizardryCapabilityStorage INSTANCE = new WizardryCapabilityStorage();

    @Override
    public NBTBase writeNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(BLOOD_TYPE, BloodRegistry.getBloodNameByType(instance.getBloodType()));
        nbt.setInteger(MAX_MANA, instance.getMaxMana());
        nbt.setInteger(MAX_BURNOUT, instance.getMaxBurnout());
        nbt.setInteger(MANA, instance.getMana());
        nbt.setInteger(BURNOUT, instance.getBurnout());
        
        NBTTagCompound compound = new NBTTagCompound();
        for (IBloodType bloodType : instance.getBloodLevels().keySet())
        {
        	compound.setInteger(BloodRegistry.getBloodNameByType(bloodType), instance.getBloodLevel(bloodType));
        }
        nbt.setTag(BLOOD_LEVELS, compound);
        
        return nbt;
    }

    @Override
    public void readNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        ((DefaultWizardryCapability) instance).mana = tag.getInteger(MANA);
        ((DefaultWizardryCapability) instance).maxMana = tag.getInteger(MAX_MANA);
        ((DefaultWizardryCapability) instance).burnout = tag.getInteger(BURNOUT);
        ((DefaultWizardryCapability) instance).maxBurnout = tag.getInteger(MAX_BURNOUT);
        ((DefaultWizardryCapability) instance).bloodType = BloodRegistry.getBloodTypeByName(tag.getString(BLOOD_TYPE));
        
        Map<IBloodType, Integer> bloodLevels = new HashMap<>();
        NBTTagCompound compound = tag.getCompoundTag(BLOOD_LEVELS);
        for (String key : compound.getKeySet())
        {
        	bloodLevels.put(BloodRegistry.getBloodTypeByName(key), compound.getInteger(key));
        }
        ((DefaultWizardryCapability) instance).bloodLevels = bloodLevels;
    }
}
