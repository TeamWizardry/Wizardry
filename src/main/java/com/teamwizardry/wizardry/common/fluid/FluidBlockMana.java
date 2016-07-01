package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Random;

public class FluidBlockMana extends BlockFluidClassic {

    public static final FluidBlockMana instance = new FluidBlockMana();
    public static ArrayList<BookTrackerObject> bookTracker = new ArrayList<>();

    public FluidBlockMana() {
        super(FluidMana.instance, Material.WATER);
        GameRegistry.registerBlock(this, "mana");
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
        SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 30, 1, 1, 1, true);
        ambient.jitter(5, 0.2, 0, 0.2);
        ambient.setMotion(0, 0.1, 0);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {

            SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.5F, 0.5F, 30, 0.5, 0.1, 0.5, true);
            ambient.jitter(30, 0.1, 0, 0.1);
            ambient.setMotion(0, 0.05, 0);

            if (entityIn instanceof EntityItem && new BlockPos(entityIn.getPositionVector()).equals(pos) && state.getValue(BlockFluidClassic.LEVEL) == 0) {
                EntityItem ei = (EntityItem) entityIn;
                ItemStack stack = ei.getEntityItem();

                if (stack.getItem() instanceof IExplodable) {

                    for (int i = 0; i < 10; i++) {
                        SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldIn, entityIn.posX, entityIn.posY + 0.5, entityIn.posZ, 0.5F, 0.5F, 30, true);
                        fizz.jitter(10, 0.01, 0, 0.01);
                        fizz.setMotion(0, 0.08, 0);
                    }

                    if (stack.hasTagCompound()) {
                        NBTTagCompound compound = stack.getTagCompound();
                        if (compound.hasKey("reactionCooldown")) {
                            if (compound.getInteger("reactionCooldown") >= 100) {
                                compound.setInteger("reactionCooldown", 0);

                                ei.setDead();
                                ((IExplodable) stack.getItem()).explode(entityIn);
                                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                                worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);

                            } else {
                                compound.setInteger("reactionCooldown", compound.getInteger("reactionCooldown") + 1);
                                worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.FIZZING_LOOP, SoundCategory.BLOCKS, 0.3F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);
                            }
                        } else stack.getTagCompound().setInteger("reactionCooldown", 0);
                    } else stack.setTagCompound(new NBTTagCompound());
                }

                if (stack.getItem() == Items.BOOK) {
                    BookTrackerObject book = new BookTrackerObject(ei);
                    if (!bookTracker.contains(book)) {
                        bookTracker.add(book);
                        ei.setDead();
                    }
                }
            } else if (entityIn instanceof EntityPlayer) {
                ((EntityPlayer) entityIn).addStat(Achievements.MANAPOOL);
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
