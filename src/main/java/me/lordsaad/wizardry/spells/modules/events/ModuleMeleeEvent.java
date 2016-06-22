package me.lordsaad.wizardry.spells.modules.events;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleMeleeEvent implements IModule {

    private IModule[] modules;

    public ModuleMeleeEvent(IModule... modules) {
        this.modules = modules;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }

    public void tick(World world, EntityPlayer source) {

    }
}
