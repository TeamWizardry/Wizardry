package me.lordsaad.wizardry.fluid;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.IExplodable;
import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

public class FluidBlockMana extends BlockFluidClassic {

    public static final FluidBlockMana instance = new FluidBlockMana();

    public FluidBlockMana() {
        super(FluidMana.instance, Material.WATER);

        this.setQuantaPerBlock(6);
        this.setUnlocalizedName("mana");
    }

    @Override
    public Fluid getFluid() {
        return FluidMana.instance;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 30, 1, 1, 1);
        ambient.jitter(5, 0.2, 0, 0.2);
        ambient.setMotion(0, 0.1, 0);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {

            SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.5F, 0.5F, 30, 0.5, 0.1, 0.5);
            ambient.jitter(30, 0.1, 0, 0.1);
            ambient.setMotion(0, 0.05, 0);

            if (entityIn instanceof EntityItem && new BlockPos(entityIn.getPositionVector()).equals(pos) && state.getValue(BlockFluidClassic.LEVEL) == 0) {
                EntityItem ei = (EntityItem) entityIn;
                ItemStack stack = ei.getEntityItem();

                if (stack.getItem() instanceof IExplodable) {
                    ei.setDead();
                    ((IExplodable) stack.getItem()).explode(entityIn);
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
