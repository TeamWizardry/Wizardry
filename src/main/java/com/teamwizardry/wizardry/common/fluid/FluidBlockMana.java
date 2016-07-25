package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.trackerobject.BookTrackerObject;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
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
import net.minecraft.util.math.Vec3d;
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
        SparkleFX ambient =  GlitterFactory.getInstance().createSparkle(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), 30);
        ambient.setAlpha(0.5f);
        ambient.setScale(0.5f);
        ambient.setFadeIn();
        ambient.setFadeOut();
        ambient.setShrink();
        ambient.setJitter(5, 0.2, 0, 0.2);
        ambient.addMotion(0, 0.1, 0);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {

            SparkleFX ambient = GlitterFactory.getInstance().createSparkle(worldIn, entityIn.getPositionVector(), new Vec3d(0.5, 0.3, 0.5), 30);
            ambient.setAlpha(0.5f);
            ambient.setScale(0.5f);
            ambient.setFadeIn();
            ambient.setFadeOut();
            ambient.setShrink();
            ambient.setRandomDirection(0.1, 0.1, 0.1);
            ambient.addMotion(0, 0.05, 0);

            if (entityIn instanceof EntityItem && new BlockPos(entityIn.getPositionVector()).equals(pos) && state.getValue(BlockFluidClassic.LEVEL) == 0) {
                EntityItem ei = (EntityItem) entityIn;
                ItemStack stack = ei.getEntityItem();

                if (stack.getItem() instanceof IExplodable) {

                    for (int i = 0; i < 10; i++) {
                        SparkleFX fizz = GlitterFactory.getInstance().createSparkle(worldIn, entityIn.getPositionVector().add(new Vec3d(0, 0.5, 0)), 30);
                        fizz.setScale(0.5f);
                        fizz.setAlpha(0.5f);
                        fizz.setShrink();
                        fizz.setFadeOut();
                        fizz.setJitter(10, 0.01, 0, 0.01);
                        fizz.addMotion(0, 0.1, 0);
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
                                if (compound.getInteger("reactionCooldown") % 5 == 0)
                                    worldIn.playSound(null, ei.posX, ei.posY, ei.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.3F, ThreadLocalRandom.current().nextFloat() * 0.4F + 0.8F);
                            }
                        } else stack.getTagCompound().setInteger("reactionCooldown", 0);
                    } else stack.setTagCompound(new NBTTagCompound());
                }

                if (stack.getItem() == Items.BOOK) {
                    BookTrackerObject book = new BookTrackerObject(ei);
                    if (!bookTracker.contains(book)) {
                        if (ei.getEntityItem().stackSize == 1) {
                            ei.setDead();
                            bookTracker.add(book);
                        }
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
