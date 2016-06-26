package com.teamwizardry.wizardry.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import com.teamwizardry.wizardry.multiblock.vanillashade.Template;
import com.teamwizardry.wizardry.multiblock.vanillashade.Template.BlockInfo;


/**
 * Used to access the blocks in a template. Made for getActualState()
 * @author Pierce Corcoran
 */
public class TemplateBlockAccess implements IBlockAccess {

	protected Template template;
	
	public TemplateBlockAccess(Template template) {
		this.template = template;
	}
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if(template == null || template.infos() == null)
			return Blocks.AIR.getDefaultState();
		IBlockState state = null;
		for (BlockInfo info : template.infos()) {
			if(info.pos.equals(pos)) {
				state = info.blockState;
				break;
			}
		}
		return state == null ? Blocks.AIR.getDefaultState() : state;
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return getBlockState(pos) == Blocks.AIR.getDefaultState();
	}

	@Override
	public Biome getBiomeGenForCoords(BlockPos pos) {
		return Biomes.PLAINS;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false; // TODO: figure out what this does
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.CUSTOMIZED;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		if(template == null || template.infos() == null)
			return _default;
		IBlockState state = null;
		for (BlockInfo info : template.infos()) {
			if(info.pos.equals(pos)) {
				state = info.blockState;
				break;
			}
		}
		if(state == null)
			return _default;
        return getBlockState(pos).isSideSolid(this, pos, side);
	}

}
