package com.teamwizardry.wizardry.gui.book.util;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class DataNodeParsers {

	public static IBlockState parseBlockState(DataNode data) {
		IBlockState iblockstate = Blocks.AIR.getDefaultState();
		
		if (data.isMap() && data.get("id").isString()) {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(data.get("id").asStringOr("minecraft:bedrock")));
            iblockstate = block.getDefaultState();

            if (data.get("props").isMap()) {
            	Map<String, DataNode> map = data.get("props").asMap();
            	
                BlockStateContainer blockstatecontainer = block.getBlockState();

                for (String s : map.keySet()) {
                    IProperty<?> iproperty = blockstatecontainer.getProperty(s);

                    if (iproperty != null) {
                    	iblockstate = withProperty(iblockstate, iproperty, map.get(s).asString());
                    }
                }
            }

        }
		return iblockstate;
	}

	private static <T extends Comparable<T>> IBlockState withProperty(IBlockState p_190007_0_, IProperty<T> p_190007_1_, String p_190007_2_) {
        return p_190007_0_.withProperty(p_190007_1_, p_190007_1_.parseValue(p_190007_2_).get());
    }
	
}
