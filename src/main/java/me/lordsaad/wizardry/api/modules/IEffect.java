package me.lordsaad.wizardry.api.modules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public interface IEffect extends IModule {

    void onCollideWithBlock(World world, BlockPos pos);

    void onCollideWithEntity(World world, Entity entity);
}
