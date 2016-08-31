package com.teamwizardry.wizardry.api.capability;

import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IWizardryCapability {

    int getMana();

    void setMana(int mana, EntityPlayer player);

    int getMaxMana();

    void setMaxMana(int maxMana, EntityPlayer player);

    int getBurnout();

    void setBurnout(int burnout, EntityPlayer player);

    int getMaxBurnout();

    void setMaxBurnout(int maxBurnout, EntityPlayer player);

    IBloodType getBloodType();

    void setBloodType(IBloodType bloodType, EntityPlayer player);
    
    int getBloodLevel(IBloodType bloodType);
    
    Map<IBloodType, Integer> getBloodLevels();
    
    void setBloodLevel(IBloodType bloodType, int level, EntityPlayer player);
    
    void setBloodLevels(Map<IBloodType, Integer> levels, EntityPlayer player);
    
    void incrementBloodLevel(IBloodType bloodType, EntityPlayer player);

    NBTTagCompound saveNBTData();

    void loadNBTData(NBTTagCompound compound);

    void dataChanged(EntityPlayer player);
}
