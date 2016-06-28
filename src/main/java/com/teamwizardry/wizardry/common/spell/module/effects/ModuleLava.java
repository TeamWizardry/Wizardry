package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleLava extends Module {
    public ModuleLava() {

    }

    public void onCollideWithBlock(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.LAVA.getDefaultState());
    }

    public void onCollideWithEntity(World world, Entity entity) {
        world.setBlockState(entity.getPosition(), Blocks.LAVA.getDefaultState());
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }
}