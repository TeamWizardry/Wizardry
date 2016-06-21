package me.lordsaad.wizardry.spells.modules;

import me.lordsaad.wizardry.api.modules.IEffect;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleLavaBucket implements IEffect {

    @Override
    public void onCollideWithBlock(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.LAVA.getDefaultState());
    }

    @Override
    public void onCollideWithEntity(World world, Entity entity) {
        world.setBlockState(entity.getPosition(), Blocks.LAVA.getDefaultState());
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.LAVA_BUCKET, 1);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }
}
