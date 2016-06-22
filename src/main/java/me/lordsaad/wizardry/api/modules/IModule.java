package me.lordsaad.wizardry.api.modules;

import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 6/21/2016.
 */
public interface IModule {

    ModuleType getType();

    /**
     * @author Seth
     */
    NBTTagCompound getModuleData();
}
