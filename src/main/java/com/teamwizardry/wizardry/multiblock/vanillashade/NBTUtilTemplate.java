package com.teamwizardry.wizardry.multiblock.vanillashade;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map.Entry;

public class NBTUtilTemplate {
    public static NBTTagCompound func_190009_a(NBTTagCompound p_190009_0_, IBlockState p_190009_1_) {
        p_190009_0_.setString("Name", Block.REGISTRY.getNameForObject(p_190009_1_.getBlock()).toString());

        if (!p_190009_1_.getProperties().isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            for (Entry<IProperty<?>, Comparable<?>> entry : p_190009_1_.getProperties().entrySet()) {
                IProperty<?> iproperty = (IProperty) entry.getKey();
                nbttagcompound.setString(iproperty.getName(), func_190010_a(iproperty, (Comparable) entry.getValue()));
            }

            p_190009_0_.setTag("Properties", nbttagcompound);
        }

        return p_190009_0_;
    }

    public static IBlockState func_190008_d(NBTTagCompound p_190008_0_) {
        if (!p_190008_0_.hasKey("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        } else {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(p_190008_0_.getString("Name")));
            IBlockState iblockstate = block.getDefaultState();

            if (p_190008_0_.hasKey("Properties", 10)) {
                NBTTagCompound nbttagcompound = p_190008_0_.getCompoundTag("Properties");
                BlockStateContainer blockstatecontainer = block.getBlockState();

                for (String s : nbttagcompound.getKeySet()) {
                    IProperty<?> iproperty = blockstatecontainer.getProperty(s);

                    if (iproperty != null) {
                        iblockstate = func_190007_a(iblockstate, iproperty, nbttagcompound.getString(s));
                    }
                }
            }

            return iblockstate;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String func_190010_a(IProperty<T> p_190010_0_, Comparable<?> p_190010_1_) {
        return p_190010_0_.getName((T) p_190010_1_);
    }

    private static <T extends Comparable<T>> IBlockState func_190007_a(IBlockState p_190007_0_, IProperty<T> p_190007_1_, String p_190007_2_) {
        return p_190007_0_.withProperty(p_190007_1_, p_190007_1_.parseValue(p_190007_2_).get());
    }
}
