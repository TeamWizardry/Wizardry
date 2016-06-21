package me.lordsaad.wizardry.api.modules;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public interface IEvent extends IModule {

    void tick(World world, EntityPlayer source);
}
