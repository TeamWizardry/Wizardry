package me.lordsaad.wizardry.tileentities;

import me.lordsaad.wizardry.fluid.FluidBlockMana;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TileEntityManaBattery extends TileEntity implements ITickable {

    public int MAX_MANA = 1000000;
    public int current_mana = 0;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("mana")) current_mana = compound.getInteger("mana");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("mana", current_mana);
        return compound;
    }

    @Override
    public void update() {
        World world = this.getWorld();
        Random rand = new Random();
        int chance = rand.nextInt(50);
        if (chance == 1) {
            int x = rand.nextInt(3) - 1;
            int z = rand.nextInt(3) - 1;
            BlockPos pos = this.getPos().add(x, -2, z);
            if (world.getBlockState(pos) == FluidBlockMana.instance.getDefaultState()) {
                this.current_mana += 1000;
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }
}
